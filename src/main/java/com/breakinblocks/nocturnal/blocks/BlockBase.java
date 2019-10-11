package com.breakinblocks.nocturnal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockBase extends Block {

	public BlockBase(Material material, String name)
	{
		super(material);
		setTranslationKey(name);
		setResistance(2.0f);
		setHardness(1.5f);
	}
	
	public BlockBase(Material mat, String name, SoundType st)
	{
		this(mat, name);
		setSoundType(st);
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	
}
