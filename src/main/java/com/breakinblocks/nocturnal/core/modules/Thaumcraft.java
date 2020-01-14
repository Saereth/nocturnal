package com.breakinblocks.nocturnal.core.modules;

import com.breakinblocks.nocturnal.core.CoreUtil;
import com.breakinblocks.nocturnal.core.NocturnalCoreModuleBase;
import com.breakinblocks.nocturnal.core.NocturnalHooks;
import com.breakinblocks.nocturnal.core.Transformer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Opcodes;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;

import java.util.Arrays;

import static com.breakinblocks.nocturnal.core.CoreUtil.*;
import static com.breakinblocks.nocturnal.core.NocturnalCore.OBFUSCATED;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class Thaumcraft extends NocturnalCoreModuleBase {
	public static final String ENTITY = "net.minecraft.entity.Entity";
	public static final String ENTITY_LIVING_BASE = "net.minecraft.entity.EntityLivingBase";
	public static final String ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";
	public static final String I_TAINTED_MOB = "thaumcraft.api.entities.ITaintedMob";
	public static final String ENTITY_TAINT_SWARM = "thaumcraft.common.entities.monster.tainted.EntityTaintSwarm";
	public static final String NOCTURNAL_HOOKS = "com.breakinblocks.nocturnal.core.NocturnalHooks";

	@Transformer(transformedName = ENTITY_PLAYER)
	public void applyITaintedMobToEntityPlayer(ClassNode cn) {
		cn.interfaces.add(typeToPath(I_TAINTED_MOB));
	}

	/**
	 * @see EntityTaintSwarm#findPlayerToAttack()
	 * @see NocturnalHooks#taintedSwarmFindPlayer(Entity, EntityTaintSwarm)
	 */
	@Transformer(transformedName = ENTITY_TAINT_SWARM)
	public void taintedSwarmFindPlayerToAttackHook(ClassNode cn) {
		final MethodNode mn = getMethodNode(cn, "findPlayerToAttack", createMethodDescriptor(ENTITY));
		// We insert this right before returning so we can modify the result as we please
		final AbstractInsnNode insnAReturn = Arrays.stream(mn.instructions.toArray())
				.filter(CoreUtil::isNotLabelOrLine)
				.filter(insn -> insn.getOpcode() == Opcodes.ARETURN)
				.findFirst().orElseThrow(() -> new RuntimeException("Return instruction not found"));
		final InsnList hook = new InsnList();
		// Current stack: target
		hook.add(new VarInsnNode(ALOAD, 0)); // -> this
		hook.add(new MethodInsnNode(INVOKESTATIC,
				typeToPath(NOCTURNAL_HOOKS), "taintSwarmFindPlayer",
				createMethodDescriptor(ENTITY, ENTITY, ENTITY_TAINT_SWARM),
				false
		)); // target, this -> newTarget
		mn.instructions.insertBefore(insnAReturn, hook);
	}
}
