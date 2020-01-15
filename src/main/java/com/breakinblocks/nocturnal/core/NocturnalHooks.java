package com.breakinblocks.nocturnal.core;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.core.modules.MinecraftEntityAIFix;
import com.breakinblocks.nocturnal.util.entity.DistanceEntity;
import com.breakinblocks.nocturnal.util.entity.TaintedMobHandler;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;

public final class NocturnalHooks {
	/**
	 * Check and fix EntityAITasks
	 *
	 * @see MinecraftEntityAIFix#makeSureAITasksAreValid(org.objectweb.asm.tree.ClassNode)
	 */
	public static void checkAndFixEntityAITasks(EntityAITasks tasks) {
		ImmutableSet<EntityAITasks.EntityAITaskEntry> toRemove = ImmutableSet
				.copyOf(Sets.difference(tasks.executingTaskEntries, tasks.taskEntries));
		toRemove.forEach(entry -> {
			entry.using = false;
			entry.action.resetTask();
			tasks.executingTaskEntries.remove(entry);
			// Can't use this cause it doesn't do anything if it's not in task.taskEntries
			//tasks.removeTask(entry.action);
		});
	}

	/**
	 * When a taint swarm doesn't have a target, this hook will intercept the call to find a new one.
	 * We could even use this to make it find a different target to attack if we wanted to.
	 *
	 * @param target Can be null, or typically a player.
	 * @param swarm  Swarm that is currently searching.
	 * @return Make sure it's a {@link EntityLivingBase} because it is casted to it.
	 */
	@Optional.Method(modid = Constants.Deps.THAUMCRAFT)
	public static Entity taintSwarmFindPlayer(Entity target, EntityTaintSwarm swarm) {
		// Don't affect summoned swarms
		if (swarm.getIsSummoned()) return target;
		// If it already has a non-tainted target, don't change it
		if (target != null && !TaintedMobHandler.isTainted(target)) return target;
		// Make the swarm target nearby non-tainted mobs instead
		double d = 8.0f;
		double dsq = d * d;
		return swarm.world.getEntitiesWithinAABB(EntityLivingBase.class,
				swarm.getEntityBoundingBox().grow(d, d, d),
				(t) -> !TaintedMobHandler.isTainted(t)
		).stream()
				.map((t) -> new DistanceEntity<>(t.getDistanceSq(swarm), t))
				.filter((de) -> de.dist <= dsq)
				.reduce((a, b) -> a.dist < b.dist ? a : b)
				.map((de) -> de.ent)
				.orElse(null);
	}
}
