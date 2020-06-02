package com.breakinblocks.nocturnal.util.entity;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.NocturnalConfig;
import com.breakinblocks.nocturnal.entity.ai.EntityAIForgiveRecentlyTainted;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;
import thaumcraft.common.entities.monster.mods.ChampionModifier;

import java.util.Arrays;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class TaintedMobHandler {
	// Thaumcraft uses this to keep track of the mobs that are tainted
	public static final IAttribute THAUMCRAFT_CHAMPION_ATTRIBUTE = ThaumcraftApiHelper.CHAMPION_MOD;
	public static final AttributeModifier THAUMCRAFT_TAINTED_ATTRIBUTE_MOD = Arrays.stream(ChampionModifier.mods)
			.filter((mod) -> mod.effect instanceof ChampionModTainted)
			.map((mod) -> mod.attributeMod)
			.findFirst().orElseThrow(() -> new RuntimeException("Could not find Thaumcraft Tainted Attribute Mod"));
	public static final IAttribute THAUMCRAFT_TAINTED_AI_ATTRIBUTE = ChampionModTainted.TAINTED_MOD;
	// Used to track application of AI changes to tainted mobs
	public static final RangedAttribute NOCTURNAL_TAINTED_AI_ATTRIBUTE = new RangedAttribute(null, "nocturnal.taintedai", 0.0D, 0.0D, 1.0D).setDescription("Tainted AI modifier");
	public static final UUID NOCTURNAL_TAINTED_AI_MOD_UUID = UUID.fromString("81acf41a-6a79-4844-a0c7-0e12c9938740");
	public static final AttributeModifier NOCTURNAL_TAINTED_AI_MOD_INST = new AttributeModifier(NOCTURNAL_TAINTED_AI_MOD_UUID, "AI Applied", 1.0, 0);

	public static boolean isTainted(Entity entity) {
		if (!(entity instanceof EntityLivingBase)) return false;
		if (entity instanceof ITaintedMob) return true;
		if (NocturnalConfig.tainted.playerIsTaintedMob && entity instanceof EntityPlayer) return true;
		final EntityLivingBase living = (EntityLivingBase) entity;
		IAttributeInstance attrInstChampion = living.getEntityAttribute(THAUMCRAFT_CHAMPION_ATTRIBUTE);
		//noinspection ConstantConditions
		return attrInstChampion != null && attrInstChampion.getModifier(THAUMCRAFT_TAINTED_ATTRIBUTE_MOD.getID()) != null;
	}

	@SubscribeEvent
	public static void addAttributeToTrackAIChanges(EntityEvent.EntityConstructing event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof EntityLivingBase)) return;
		final EntityLivingBase living = (EntityLivingBase) entity;
		living.getAttributeMap().registerAttribute(NOCTURNAL_TAINTED_AI_ATTRIBUTE).setBaseValue(0.0d);
	}

	@SubscribeEvent
	public static void resetAttributeToTrackAIChanges(EntityJoinWorldEvent event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof EntityLivingBase)) return;
		final EntityLivingBase living = (EntityLivingBase) entity;
		IAttributeInstance attrAiChanged = living.getAttributeMap().getAttributeInstance(NOCTURNAL_TAINTED_AI_ATTRIBUTE);
		// Remove this so that it so that AI changes can be applied once again
		attrAiChanged.removeModifier(NOCTURNAL_TAINTED_AI_MOD_UUID);
	}

	@SubscribeEvent
	public static void checkForAIChange(LivingEvent.LivingUpdateEvent event) {
		if (!NocturnalConfig.tainted.modifyAIOfTaintedMobs) return;
		final EntityLivingBase livingBase = event.getEntityLiving();
		if (!(livingBase instanceof EntityCreature)) return;
		final EntityCreature creature = (EntityCreature) livingBase;
		// Only modify AI of tainted creatures
		if (!isTainted(creature)) return;
		// Check that our AI changes haven't been applied yet
		final IAttributeInstance attrNocAiChanged = creature.getEntityAttribute(NOCTURNAL_TAINTED_AI_ATTRIBUTE);
		if (attrNocAiChanged.getModifier(NOCTURNAL_TAINTED_AI_MOD_UUID) != null) return;
		// If it's not an EntityMob, we have to wait till Thaumcraft changes the AI before we can
		if (!(creature instanceof EntityMob)) {
			IAttributeInstance attrThaumAiChanged = creature.getEntityAttribute(THAUMCRAFT_TAINTED_AI_ATTRIBUTE);
			if (attrThaumAiChanged.getAttributeValue() == 0.0D)
				return;
		}
		// We can do our AI changes now
		modifyTaintedAI(creature);
		// Make sure we only don't do this again till needed
		attrNocAiChanged.applyModifier(NOCTURNAL_TAINTED_AI_MOD_INST);
	}

	/**
	 * Modifies AI tasks to be hostile to non-tainted mobs
	 */
	public static void modifyTaintedAI(EntityCreature creature) {
		// Entities on client world can be skipped
		if (!creature.isServerWorld()) return;
		final EntityAITasks targetTasks = creature.targetTasks;
		// Add the tasks back in attempting to modify each one
		ImmutableSet.copyOf(targetTasks.taskEntries).forEach(entry -> {
			// Remove task before modifying it so that it gets reset
			targetTasks.removeTask(entry.action);
			final EntityAIBase action = modifyTaintedAIAction(creature, entry.action);
			if (action != null)
				targetTasks.addTask(entry.priority, action);
		});
		// Add a task to stop attacking target if it has changed tainted status recently
		targetTasks.addTask(0, new EntityAIForgiveRecentlyTainted(creature));
		// Add and remove the creature's normal tasks so that they are reset as well
		final EntityAITasks tasks = creature.tasks;
		ImmutableSet.copyOf(tasks.taskEntries).forEach(entry -> {
			tasks.removeTask(entry.action);
			final EntityAIBase action = modifyTaintedAIAction(creature, entry.action);
			if (action != null)
				tasks.addTask(entry.priority, action);
		});
		// Reset the creature's targets since they aren't quite themselves anymore
		if (isTainted(creature.getAttackTarget()))
			creature.setAttackTarget(null);
		if (isTainted(creature.getRevengeTarget()))
			creature.setRevengeTarget(null);
		if (isTainted(creature.getLastAttackedEntity()))
			creature.setLastAttackedEntity(null);
	}

	/**
	 * Attempts to change the given AI to be suitable for a tainted mob
	 */
	public static EntityAIBase modifyTaintedAIAction(EntityCreature creature, EntityAIBase action) {
		// TODO: Find things that don't work with this?
		if (action instanceof EntityAIHurtByTarget) {
			return new EntityAIHurtByTarget(creature, true);
		} else if (action instanceof EntityAINearestAttackableTarget) {
			return new EntityAINearestAttackableTarget<>(
					creature, EntityLivingBase.class, 0, false, false,
					t -> !isTainted(t) && !(t instanceof EntityCreeper)
			);
		} else {
			return action;
		}
	}
}
