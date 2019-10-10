package com.breakinblocks.nocturnal.items;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.Nocturnal;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemBase extends Item {

	public ItemBase(String name) {
		this.setRegistryName(new ResourceLocation(Constants.Mod.MODID, name));	
		this.setCreativeTab(Nocturnal.nocturnalTab);
		this.setTranslationKey(name);
		System.out.println("New item constructed. Registry name: " + getRegistryName());
	}

}
