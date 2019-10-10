package com.breakinblocks.nocturnal.proxy;




import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	
	public void tryHandleItemModel(Item item) {
	}

	public void tryHandleBlockModel(Block block) {
	}
	
		public boolean fancyGraphics(){
		return true;
	}
}