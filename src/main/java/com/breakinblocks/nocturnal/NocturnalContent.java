package com.breakinblocks.nocturnal;


import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.nocturnal.items.ItemWarpCharm;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class NocturnalContent {

	public static List<Item> items;
	
	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void registerItems(RegistryEvent.Register<Item> event) {
		items = new ArrayList<>();



		setupItem(new ItemWarpCharm(), "warp_charm");
		items.forEach(event.getRegistry()::register);
	}


	private static void setupItem(Item item, String name) {
		if (item.getRegistryName() == null)
			item.setRegistryName(name);
		item.setCreativeTab(Nocturnal.nocturnalTab);
		items.add(item);
	}

	

	
}
