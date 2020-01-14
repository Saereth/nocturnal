package com.breakinblocks.nocturnal.core;

import com.breakinblocks.nocturnal.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Optional;
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
		// Prevent targeting of players
		return target instanceof EntityPlayer ? null : target;
	}
}
