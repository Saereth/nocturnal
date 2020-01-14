package com.breakinblocks.nocturnal.core.modules;

import com.breakinblocks.nocturnal.core.CoreUtil;
import com.breakinblocks.nocturnal.core.NocturnalCoreModuleBase;
import com.breakinblocks.nocturnal.core.Transformer;
import org.objectweb.asm.tree.ClassNode;

public class Thaumcraft extends NocturnalCoreModuleBase {
	public static final String ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";
	public static final String I_TAINTED_MOB = "thaumcraft.api.entities.ITaintedMob";

	@Transformer(transformedName = ENTITY_PLAYER)
	public void applyITaintedMobToEntityPlayer(ClassNode cn) {
		cn.interfaces.add(CoreUtil.typeToPath(I_TAINTED_MOB));
	}
}
