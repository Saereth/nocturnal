package com.breakinblocks.nocturnal;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabNocturnal extends CreativeTabs {

	public static final CreativeTabs instance = new CreativeTabNocturnal(CreativeTabs.getNextID(), "nocturnal");

	public CreativeTabNocturnal(int index, String label) {
		super(index, label);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(Items.REDSTONE);
		
	}
}