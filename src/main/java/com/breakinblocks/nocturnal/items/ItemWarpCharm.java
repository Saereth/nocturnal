package com.breakinblocks.nocturnal.items;

import org.apache.logging.log4j.Logger;


import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aura.AuraHelper;


public class ItemWarpCharm extends ItemBase implements IBauble, IRenderBauble {

	public ItemWarpCharm() {
		super("warp_charm");
		this.setMaxStackSize(1);
		this.setMaxDamage(50);
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

		if ((entity instanceof EntityPlayer) && !entity.world.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.ticksExisted % 100 == 0) {
				if (AuraHelper.getFlux(player.world, player.getPosition()) > 1){ //chunk contains flux lets remove some
					stack.damageItem(1, player);
					System.out.println("Flux reduced.");
					AuraHelper.drainFlux(player.world, player.getPosition(), 1, false);
				}
			}
		}
	}




	@Override
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
		// TODO Auto-generated method stub
		
	}

}
