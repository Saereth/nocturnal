package com.breakinblocks.nocturnal.util.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.Nocturnal;
import com.breakinblocks.nocturnal.NocturnalConfig;

import baubles.api.BaublesApi;
import mcp.mobius.waila.handlers.NetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;
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
    				Biome defiledPlainsBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "plains_defiled"));

	        		for (int z = 0; z < UpdateChunks.size(); z++) {
	    				BlockPos updatePos = UpdateChunks.get(z);	        				
	        			if (AuraHelper.getFlux(event.world, updatePos) < 1000){ //Time to pollute the chunks
		    					AuraHelper.polluteAura(event.world, updatePos, pollutionAmount, true);   
							if (AuraHelper.getFlux(event.world, updatePos) > 300) {
		    					biome = event.world.getBiome(updatePos);
							System.out.println("Increased Flux in biome: " + biome.getBiomeName() + " New biome is set to: " + defiledPlainsBiome.getBiomeName());
						
								if (biome.getBiomeName() == "Plains") {
		
								}

								setBiome(event.world, defiledPlainsBiome, updatePos);
							}

	    				
	    				}
	        			
	        		}
	            
	            }
	            SERVER_TICKS.put(dim, ticks + 1);

	        }

   
	    
	    }

public static void setBiome(World world, Biome biome, BlockPos pos) {
			Chunk chunk = world.getChunk(pos);

			int i = pos.getX() & 15;
			int j = pos.getZ() & 15;

			byte id = (byte) Biome.getIdForBiome(biome);

			byte b = chunk.getBiomeArray()[j << 4 | i];

			if (b == id) return;

			chunk.getBiomeArray()[j << 4 | i] = id;
			chunk.markDirty();

			if (world instanceof WorldServer) {
				PlayerChunkMap playerChunkMap = ((WorldServer) world).getPlayerChunkMap();
				int chunkX = pos.getX() >> 4;
				int chunkZ = pos.getZ() >> 4;

				PlayerChunkMapEntry entry = playerChunkMap.getEntry(chunkX, chunkZ);
				if (entry != null) {
					//packetstuff
					//entry.sendPacket(NetworkHandler. get(Side.SERVER).generatePacketFrom(new PacketBiomeChange()));
				}
			}
		}
	    

}
