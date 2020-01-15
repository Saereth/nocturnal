package com.breakinblocks.nocturnal.core.modules;

import com.breakinblocks.nocturnal.core.*;
import net.minecraft.entity.Entity;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Opcodes;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;

import java.util.Arrays;

import static com.breakinblocks.nocturnal.core.CoreUtil.*;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class ThaumcraftTaintedPlayerMob extends NocturnalCoreModuleBase {


	/**
	 * This is so that players are ignored by most tainted mobs.
	 */
	@Transformer(transformedName = NocturnalCoreConstants.ENTITY_PLAYER)
	public void applyITaintedMobToEntityPlayer(ClassNode cn) {
		cn.interfaces.add(typeToPath(NocturnalCoreConstants.I_TAINTED_MOB));
	}

	/**
	 * @see EntityTaintSwarm#findPlayerToAttack()
	 * @see NocturnalHooks#taintedSwarmFindPlayer(Entity, EntityTaintSwarm)
	 */
	@Transformer(transformedName = NocturnalCoreConstants.ENTITY_TAINT_SWARM)
	public void taintedSwarmFindPlayerToAttackHook(ClassNode cn) {
		final MethodNode mn = getMethodNode(cn, "findPlayerToAttack", createMethodDescriptor(NocturnalCoreConstants.ENTITY));
		// We insert this right before returning so we can modify the result as we please
		final AbstractInsnNode insnAReturn = Arrays.stream(mn.instructions.toArray())
				.filter(CoreUtil::isNotLabelOrLine)
				.filter(insn -> insn.getOpcode() == Opcodes.ARETURN)
				.findFirst().orElseThrow(() -> new NocturnalCoreModdingException("Return instruction not found"));
		final InsnList hook = new InsnList();
		// Current stack: target
		hook.add(new VarInsnNode(ALOAD, 0)); // -> this
		hook.add(new MethodInsnNode(INVOKESTATIC,
				typeToPath(NocturnalCoreConstants.NOCTURNAL_HOOKS), "taintSwarmFindPlayer",
				createMethodDescriptor(NocturnalCoreConstants.ENTITY, NocturnalCoreConstants.ENTITY, NocturnalCoreConstants.ENTITY_TAINT_SWARM),
				false
		)); // target, this -> newTarget
		mn.instructions.insertBefore(insnAReturn, hook);
	}
}
