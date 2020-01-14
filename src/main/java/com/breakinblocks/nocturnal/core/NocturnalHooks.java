package com.breakinblocks.nocturnal.core;

import com.breakinblocks.nocturnal.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;

public final class NocturnalHooks {
	/**
	 * When a taint swarm doesn't have a target, this hook will intercept the call to find a new one.
	 * We could even use this to make it find a different target to attack if we wanted to.
	 * @param target Can be null, or typically a player.
	 * @param swarm Swarm that is currently searching.
	 * @return Make sure it's a {@link net.minecraft.entity.EntityLivingBase} because it is casted to it.
	 */
	@Optional.Method(modid = Constants.Deps.THAUMCRAFT)
	public static Entity taintSwarmFindPlayer(Entity target, EntityTaintSwarm swarm) {
		// Don't affect summoned swarms
		if(swarm.getIsSummoned()) return target;
		// Prevent targeting of tainted mobs (includes player)
		return target instanceof ITaintedMob ? null : target;
	}

	/**
	 * When a mob becomes a tainted version of itself
	 * This hook will run after the ai is modified (but not if it isn't)
	 *
	 * @param critter The entity that had it's AI modified
	 * @see ChampionModTainted#resetAI(EntityCreature)
	 */
	@Optional.Method(modid = Constants.Deps.THAUMCRAFT)
	public static void championModTaintedResetAI(EntityCreature critter) {
		// Remove target task that targets players
		critter.targetTasks.taskEntries.removeIf(entry -> entry.action instanceof EntityAINearestAttackableTarget);
	}
}
