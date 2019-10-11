package com.breakinblocks.nocturnal.items;


import com.breakinblocks.nocturnal.NocturnalConfig;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;



public class ItemWarpCharm extends ItemBase implements IBauble, IRenderBauble {

	public ItemWarpCharm() {
		super("warp_charm");
		this.setMaxStackSize(1);
		this.setMaxDamage(NocturnalConfig.general.warpCharmDamage);
		this.canRepair = false;
	}

	
	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.RARE;
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.CHARM;
	}
	
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {

	}

	@Override
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
	}

	

}
