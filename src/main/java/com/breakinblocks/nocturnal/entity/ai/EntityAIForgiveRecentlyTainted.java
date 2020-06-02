package com.breakinblocks.nocturnal.entity.ai;

import com.breakinblocks.nocturnal.util.entity.TaintedMobHandler;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIForgiveRecentlyTainted extends EntityAITarget {
	public boolean lastSelfTainted = false;
	public EntityLivingBase lastTarget = null;
	public boolean lastTargetTainted = false;
	public boolean execute = false;

	public EntityAIForgiveRecentlyTainted(EntityCreature creature) {
		super(creature, false, false);
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if (execute) return true;
		final EntityLivingBase target = taskOwner.getAttackTarget();
		if (target == null) return false;
		if (!lastSelfTainted && TaintedMobHandler.isTainted(this.taskOwner)) {
			lastSelfTainted = true;
			execute = true;
			return true;
		}
		if (lastTarget == target) {
			boolean targetTainted = TaintedMobHandler.isTainted(target);
			// Forgive current target if they have changed tainted status
			if (lastTargetTainted != targetTainted) {
				lastTargetTainted = TaintedMobHandler.isTainted(target);
				execute = true;
				return true;
			}
		} else {
			// Store new target's tainted status
			lastTarget = target;
			lastTargetTainted = TaintedMobHandler.isTainted(target);
		}
		return false;
	}

	@Override
	public void startExecuting() {
		execute = false;
		taskOwner.setAttackTarget(null);
		taskOwner.setRevengeTarget(null);
		taskOwner.setLastAttackedEntity(null);
	}
}
