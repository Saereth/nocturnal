package com.breakinblocks.nocturnal.util.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.Nocturnal;
import com.breakinblocks.nocturnal.NocturnalConfig;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.world.biome.Biome;

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
            
	            if (ticks % NocturnalConfig.general.fluxPollutionTicktime == 0) {
	            	//System.out.println("Tick occured : " + NocturnalConfig.general.fluxPollutionTicktime);
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
	        				if (rand.nextInt(NocturnalConfig.general.charmDamageChance) == 0) { //bauble randomly takes damage
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
    				Biome biome;
					int size = 7;
					int rad = size / 2;

	        		for (int z = 0; z < UpdateChunks.size(); z++) {
	    				BlockPos updatePos = UpdateChunks.get(z);	        				
	        			if (AuraHelper.getFlux(event.world, updatePos) < 1000){ //Time to pollute the chunks
	    					AuraHelper.polluteAura(event.world, updatePos, pollutionAmount, true);   
						if (AuraHelper.getFlux(event.world, updatePos) > 300) {
	    					biome = event.world.getBiome(updatePos);
						System.out.println("Increased Flux in biome: " + biome.getBiomeName());
					
						
						for (int ix = updatePos.getX() - rad; ix <= updatePos.getX() + rad; ++ix) {
							for (int iz = updatePos.getZ() - rad; iz <= updatePos.getZ() + rad; ++iz) {
								int relBlockX = ix & 15;
								int relBlockZ = iz & 15;

								
								//biomeByte = 
								/*Chunk chunk = event.world.getChunk(updatePos);
								byte[] byteArray = chunk.getBiomeArray();
								byte currentByte = byteArray[relBlockZ << 4 | relBlockX];
								if (currentByte != biomeByte) {
									byteArray[relBlockZ << 4 | relBlockX] = biomeByte;
									chunk.setBiomeArray(byteArray);
									chunk.setModified(true);
								*/
								}
							}
						
							if (biome.getBiomeName() == "Plains") {

							}
						}

	    				
	    				}
	        			
	        		}
	            
	            }
	            SERVER_TICKS.put(dim, ticks + 1);

	        }

   
	    
	    }


}
