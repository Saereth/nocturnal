package com.breakinblocks.nocturnal.core.modules;

import com.breakinblocks.nocturnal.core.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.objectweb.asm.tree.*;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;

import java.util.Arrays;

import static com.breakinblocks.nocturnal.core.CoreUtil.*;
import static com.breakinblocks.nocturnal.core.NocturnalCore.OBFUSCATED;
import static com.breakinblocks.nocturnal.core.NocturnalCoreConstants.*;
import static org.objectweb.asm.Opcodes.*;

public class ThaumcraftTaintedPlayerMob extends NocturnalCoreModuleBase {

	/**
	 * Hooks the Flux Taint Potion effect so that it checks our isTainted method as well.
	 * @see NocturnalHooks#potionFluxTaintIsTainted(EntityLivingBase, int)
	 */
	@Transformer(transformedName = POTION_FLUX_TAINT)
	public void hookPotionFluxTaint(ClassNode cn) {
		if (!NocturnalCoreConfig.ThaumcraftTaintedPlayerMob.hookPotionFluxTaint) {
			NocturnalCore.log.info("Skipping hookPotionFluxTaint tweak");
			return;
		}
		NocturnalCore.log.info("Applying hookPotionFluxTaint tweak...");

		final MethodNode mn = getMethodNode(cn,
				OBFUSCATED ? "func_76394_a" : "performEffect",
				createMethodDescriptor("V", ENTITY_LIVING_BASE, "I"));

		final LabelNode insnLabelBeforeInstanceOf = Arrays.stream(mn.instructions.toArray())
				.filter(insn -> insn instanceof LabelNode)
				.map(insn -> (LabelNode) insn)
				.filter(insn -> {
					AbstractInsnNode insnNext = insn.getNext();
					if (insnNext instanceof LineNumberNode)
						insnNext = insnNext.getNext();
					if (insnNext.getOpcode() != ALOAD || ((VarInsnNode) insnNext).var != 1)
						return false;
					insnNext = insnNext.getNext();
					return insnNext.getOpcode() == INSTANCEOF && ((TypeInsnNode) insnNext).desc.equals(typeToPath(I_TAINTED_MOB));
				})
				.findFirst().orElseThrow(() -> new NocturnalCoreModdingException("Label before INSTANCEOF not found"));

		final LabelNode insnLabelIsTainted = Arrays.stream(mn.instructions.toArray())
				.filter(insn -> insn instanceof JumpInsnNode)
				.map(insn -> (JumpInsnNode) insn)
				.filter(insn -> {
					AbstractInsnNode insnPrev = insn.getPrevious();
					return insnPrev.getOpcode() == INSTANCEOF && ((TypeInsnNode) insnPrev).desc.equals(typeToPath(I_TAINTED_MOB));
				})
				.map(insn -> insn.label)
				.findFirst().orElseThrow(() -> new NocturnalCoreModdingException("Label to jump to not found"));
		// Inserted Code between ``
		// if (`NocturnalHooks.potionFluxTaintIsTainted((EntityLivingBase)target, (int)strength) || `target instanceof ITaintedMob || cai != null && (int)cai.getAttributeValue() == 13) {
		//     target.heal(1.0f);
		// } else if ...
		final InsnList hook = new InsnList();
		hook.add(new VarInsnNode(ALOAD, 1)); // -> target
		hook.add(new VarInsnNode(ILOAD, 2)); // target -> target, amplifier
		hook.add(new MethodInsnNode(INVOKESTATIC,
				typeToPath(NocturnalCoreConstants.NOCTURNAL_HOOKS), "potionFluxTaintIsTainted",
				createMethodDescriptor("Z", ENTITY_LIVING_BASE, "I"),
				false
		)); // target, amplifier -> treatAsTainted
		hook.add(new JumpInsnNode(IFNE, insnLabelIsTainted));

		mn.instructions.insertBefore(insnLabelBeforeInstanceOf, hook);
	}

	/**
	 * @see EntityTaintSwarm#findPlayerToAttack()
	 * @see NocturnalHooks#taintSwarmFindPlayer(Entity, EntityTaintSwarm)
	 */
	@Transformer(transformedName = ENTITY_TAINT_SWARM)
	public void taintedSwarmFindPlayerToAttackHook(ClassNode cn) {
		if (!NocturnalCoreConfig.ThaumcraftTaintedPlayerMob.taintedSwarmFindPlayerToAttackHook) {
			NocturnalCore.log.info("Skipping taintedSwarmFindPlayerToAttackHook tweak");
			return;
		}
		NocturnalCore.log.info("Applying taintedSwarmFindPlayerToAttackHook tweak...");

		final MethodNode mn = getMethodNode(cn, "findPlayerToAttack", createMethodDescriptor(ENTITY));
		// We insert this right before returning so we can modify the result as we please
		final AbstractInsnNode insnAReturn = Arrays.stream(mn.instructions.toArray())
				.filter(CoreUtil::isNotLabelOrLine)
				.filter(insn -> insn.getOpcode() == ARETURN)
				.findFirst().orElseThrow(() -> new NocturnalCoreModdingException("Return instruction not found"));
		final InsnList hook = new InsnList();
		// Current stack: target
		hook.add(new VarInsnNode(ALOAD, 0)); // -> this
		hook.add(new MethodInsnNode(INVOKESTATIC,
				typeToPath(NocturnalCoreConstants.NOCTURNAL_HOOKS), "taintSwarmFindPlayer",
				createMethodDescriptor(ENTITY, ENTITY, ENTITY_TAINT_SWARM),
				false
		)); // target, this -> newTarget
		mn.instructions.insertBefore(insnAReturn, hook);
	}
}
