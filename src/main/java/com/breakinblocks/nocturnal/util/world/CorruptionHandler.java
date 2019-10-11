package com.breakinblocks.nocturnal.util.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.Nocturnal;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.SoundsTC;

@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class CorruptionHandler {

		private static final HashMap<Integer, Integer> SERVER_TICKS = new HashMap<>();
		public static final String DATA_NAME = "NocturnalTimer";
		
	    @SubscribeEvent
	    public static void onServerWorldTick(TickEvent.WorldTickEvent event) {
	        if (event.world.isRemote)
	            return;

	        int dim = event.world.provider.getDimension();
	        if (event.phase == TickEvent.Phase.END) {
	            if (!SERVER_TICKS.containsKey(dim))
	                SERVER_TICKS.put(dim, 0);

	            int ticks = (SERVER_TICKS.get(dim));
	            Random rand = new Random();	

            
	            if (ticks % 600 == 0) {
	            	//do stuff with corruption every 30 seconds
	            	//System.out.print("Server Tick Test: " + '\n');
	            	List<EntityPlayer> Players = event.world.playerEntities;
	            	List<BlockPos> UpdateChunks = new ArrayList<BlockPos>();
	            	ItemStack bauble;
	            	int slot = 0;
	        		for (int i = 0; i < Players.size(); i++) {
        			
	        			//Damage Warp charm if worn by chance
	        			slot = BaublesApi.isBaubleEquipped(Players.get(i), Nocturnal.Items.WARP_CHARM);
	        			if (slot > 0) {
	        				IItemHandler baublesInv = BaublesApi.getBaublesHandler(Players.get(i));
	        				bauble = baublesInv.getStackInSlot(slot);
	        				
	        				if (rand.nextInt(4) == 0) { //bauble randomly takes damage
	        					bauble.damageItem(1, Players.get(i));
	        					event.world.playSound(Players.get(i), Players.get(i).getPosition(), SoundsTC.heartbeat, SoundCategory.PLAYERS, .5F, 1F);
	        				}
	        			}
	        			else {
		        			BlockPos pos = Players.get(i).getPosition();
		        			if(!UpdateChunks.contains(pos))
		        				UpdateChunks.add(pos);        			
	        			}
	        			
	                }
	        		
	        		int pollutionAmount = rand.nextInt(3);
	        		
	        		for (int z = 0; z < UpdateChunks.size(); z++) {
	        			//event.world.getChunk(UpdateChunks.get(z)).
	        				
	        			if (AuraHelper.getFlux(event.world, UpdateChunks.get(z)) < 1000){ //Time to pollute the chunks
	    					
	    					AuraHelper.polluteAura(event.world, UpdateChunks.get(z), pollutionAmount, true);   
	    					
	    				}
	        			
	        		}
	            
	            }
	            SERVER_TICKS.put(dim, ticks + 1);

	        }

   
	    
	    }


}
