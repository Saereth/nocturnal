package com.breakinblocks.nocturnal.core.modules;

import com.breakinblocks.nocturnal.core.*;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import org.objectweb.asm.tree.*;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;

import java.util.Arrays;

import static com.breakinblocks.nocturnal.core.CoreUtil.*;
import static com.breakinblocks.nocturnal.core.NocturnalCore.OBFUSCATED;
import static com.breakinblocks.nocturnal.core.NocturnalCoreConstants.ENTITY_AI_TASKS;
import static com.breakinblocks.nocturnal.core.NocturnalCoreConstants.NOCTURNAL_HOOKS;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class MinecraftEntityAIFix extends NocturnalCoreModuleBase {

	/**
	 * Hook to help fix mods that remove tasks from {@link EntityAITasks#taskEntries}
	 * without using {@link EntityAITasks#removeTask(EntityAIBase)}
	 * <p>
	 * An example of this bug is {@link ChampionModTainted#resetAI(EntityCreature)}
	 *
	 * @see EntityAITasks#onUpdateTasks()
	 * @see NocturnalHooks#checkAndFixEntityAITasks(EntityAITasks)(EntityCreature)
	 */
	@Transformer(transformedName = ENTITY_AI_TASKS)
	public static void makeSureAITasksAreValid(ClassNode cn) {
		if (!NocturnalCoreConfig.MinecraftEntityAIFix.makeSureAITasksAreValid) {
			NocturnalCore.log.info("Skipping makeSureAITasksAreValid tweak");
			return;
		}
		NocturnalCore.log.info("Applying makeSureAITasksAreValid tweak...");

		final MethodNode mn = getMethodNode(cn,
				OBFUSCATED ? "func_75774_a" : "onUpdateTasks", createMethodDescriptor("V"));
		final AbstractInsnNode firstRealInstruction = Arrays.stream(mn.instructions.toArray())
				.filter(CoreUtil::isNotLabelOrLine)
				.findFirst().orElseThrow(() -> new NocturnalCoreModdingException("First instruction not found"));
		final InsnList hook = new InsnList();
		hook.add(new VarInsnNode(ALOAD, 0)); // -> this
		hook.add(new MethodInsnNode(INVOKESTATIC,
				typeToPath(NOCTURNAL_HOOKS), "checkAndFixEntityAITasks",
				createMethodDescriptor("V", ENTITY_AI_TASKS),
				false
		)); // this ->
		mn.instructions.insertBefore(firstRealInstruction, hook);
	}
}
