package com.breakinblocks.nocturnal.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

import com.breakinblocks.nocturnal.Constants;

@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class ClientProxy extends CommonProxy {


	@Override
	public boolean fancyGraphics() {
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
	}
}
