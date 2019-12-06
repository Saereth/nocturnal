package com.breakinblocks.nocturnal.util.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.Nocturnal;
import com.breakinblocks.nocturnal.NocturnalConfig;

import baubles.api.BaublesApi;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.SoundsTC;

@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class CorruptionHandler {
	
		
		public static final Biome defiledPlainsBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "plains_defiled"));
		public static final Biome defiledDesertBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "desert_defiled"));
		public static final Biome defiledForestBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "forest_tenebra"));
		public static final Biome defiledForest2Biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "forest_vilespine"));
		public static final Biome defiledHillsBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "hills_defiled"));
		public static final Biome defiledSwampBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "swamp_defiled"));
		public static final Biome defiledIceBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("defiledlands", "ice_plains_defiled"));

		public static final IBlockState corruptedGrass = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("defiledlands", "grass_defiled")).getDefaultState();
		public static final IBlockState corruptedStone = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("defiledlands", "stone_defiled")).getDefaultState();
		public static final IBlockState corruptedGravel = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("defiledlands", "gravel_defiled")).getDefaultState();
		public static final IBlockState corruptedSand = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("defiledlands", "sand_defiled")).getDefaultState();
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
            
	            if ((ticks % NocturnalConfig.general.fluxPollutionTicktime == 0)) {
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
 

	        		for (int z = 0; z < UpdateChunks.size(); z++) {
	    				BlockPos updatePos = UpdateChunks.get(z);	        				
	        			if (AuraHelper.getFlux(event.world, updatePos) < 1000){ //Time to pollute the chunks
	        				if (NocturnalConfig.general.playersProduceFlux)
		    					AuraHelper.polluteAura(event.world, updatePos, pollutionAmount, true);   
							if (AuraHelper.getFlux(event.world, updatePos) > NocturnalConfig.general.minDefileFlux && (NocturnalConfig.general.enableDefilement)) {
		    					biome = event.world.getBiome(updatePos);
							//System.out.println("Increased Flux in biome: " + biome.getBiomeName());
						
		    					if (Nocturnal.defiledLoaded) {//only run this is defiled lands is present
		    						
		    					
									if (!isCorruptedBiome(biome)) {
										Biome corruptedBiome = getCorruptionBiome(biome);
										
										for (int x=-8; x<9;x++) {
											for (int y=-9; y<9; y++) {
												BlockPos toUpdate = new BlockPos(updatePos.getX()+x, 0, updatePos.getZ()+y);
												System.out.println("Altering Biome for position: " + toUpdate.toString() + " New biome is set to: " + corruptedBiome.getBiomeName());		
												setBiome(event.world, corruptedBiome, toUpdate);
												for (int z1 = 0; z1<255;z1++) {
													BlockPos toUpdateY = new BlockPos(updatePos.getX()+x, updatePos.getY()+z1, updatePos.getZ()+y);
													IBlockState updateBlock = event.world.getBlockState(toUpdateY);
													if (updateBlock.getBlock().getDefaultState().equals(Blocks.AIR.getDefaultState())) {
														continue;
													}
													else if (updateBlock.getBlock().getDefaultState().equals(Blocks.STONE.getDefaultState())) {
														event.world.setBlockState(toUpdateY, corruptedStone);
													}
													else if (updateBlock.getBlock().getDefaultState().equals(Blocks.GRASS.getDefaultState())) {
														event.world.setBlockState(toUpdateY, corruptedGrass);
													}
													else if (updateBlock.getBlock().getDefaultState().equals(Blocks.GRAVEL.getDefaultState())) {
														event.world.setBlockState(toUpdateY, corruptedGravel);	
													}
													else if (updateBlock.getBlock().getDefaultState().equals(Blocks.SAND.getDefaultState()))  {
														event.world.setBlockState(toUpdateY, corruptedSand);	
													}
																									
												}
											}
										}
									}
								}
							}

	    				
	    				}
	        			
	        		}
	            
	            }
	            SERVER_TICKS.put(dim, ticks + 1);

	        }

   
	    
	    }
	    
private static Biome getCorruptionBiome(Biome biome) {
	BiomeDictionary.getTypes(biome);
	
			if (biome == Biomes.BIRCH_FOREST || biome == Biomes.FOREST ||
				biome == Biomes.MUTATED_BIRCH_FOREST || biome == Biomes.MUTATED_FOREST ||	
				biome == Biomes.ROOFED_FOREST || biome == Biomes.MUTATED_ROOFED_FOREST ) {
				
			Random rand = new Random();
				if (rand.nextInt(1) == 0)
					return defiledForestBiome;
				else
					return 	defiledForest2Biome;
			}
			else if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS || biome == Biomes.MUTATED_DESERT) {
				return defiledDesertBiome;
			}
			else if (biome == Biomes.BIRCH_FOREST_HILLS || biome == Biomes.COLD_TAIGA_HILLS || 
					biome == Biomes.DESERT_HILLS || biome == Biomes.EXTREME_HILLS ||
					biome == Biomes.EXTREME_HILLS_EDGE || biome == Biomes.EXTREME_HILLS_WITH_TREES ||
					biome == Biomes.FOREST_HILLS || biome == Biomes.JUNGLE_HILLS ||
					biome == Biomes.MUTATED_BIRCH_FOREST_HILLS || biome == Biomes.MUTATED_EXTREME_HILLS ||
					biome == Biomes.MUTATED_EXTREME_HILLS_WITH_TREES || biome == Biomes.MUTATED_REDWOOD_TAIGA_HILLS ||
					biome == Biomes.TAIGA_HILLS) {
				return defiledHillsBiome; 
			}
			else if (biome == Biomes.MUTATED_SWAMPLAND || biome == Biomes.SWAMPLAND) {
				return defiledSwampBiome;
			}
			else if (biome == Biomes.ICE_MOUNTAINS || biome == Biomes.ICE_PLAINS || biome == Biomes.MUTATED_ICE_FLATS ||
					biome == Biomes.COLD_TAIGA || biome == Biomes.FROZEN_OCEAN || biome == Biomes.FROZEN_RIVER) {
				return defiledIceBiome;
			}

			return defiledPlainsBiome;
		}

static boolean isCorruptedBiome(Biome biome) {


	if (biome == defiledPlainsBiome  ||
		biome == defiledDesertBiome  ||
		biome == defiledForestBiome  ||
		biome == defiledForest2Biome ||
		biome == defiledHillsBiome   ||
		biome == defiledSwampBiome   ||
		biome == defiledIceBiome)
		return true;
	else
		return false;
	
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
