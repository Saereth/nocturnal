package com.breakinblocks.nocturnal;

import com.breakinblocks.nocturnal.items.ItemWarpCharm;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
@ObjectHolder(Constants.Mod.MODID)
public class NocturnalContent {

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new ItemWarpCharm());
		
	}
	

	
}
