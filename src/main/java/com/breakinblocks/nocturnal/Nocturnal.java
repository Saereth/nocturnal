package com.breakinblocks.nocturnal;


import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import org.apache.logging.log4j.Logger;

import com.breakinblocks.nocturnal.items.ItemWarpCharm;
import com.breakinblocks.nocturnal.proxy.CommonProxy;

@Mod(modid = Constants.Mod.MODID, name = Constants.Mod.NAME, version = Constants.Mod.VERSION, dependencies = Constants.Mod.DEPEND)

public class Nocturnal
{
	@Mod.Instance(Constants.Mod.MODID)

	public static Nocturnal instance;
	@SidedProxy(clientSide = "com.breakinblocks.nocturnal.proxy.ClientProxy", serverSide = "com.breakinblocks.nocturnal.proxy.ServerProxy")
	public static CommonProxy proxy;

	public static CreativeTabs nocturnalTab = new CreativeTabs("nocturnal") {

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(Items.WARP_CHARM);
			
		}
	};	
	private static Logger logger;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("Nocturnal Init Started");
        proxy.init(event);
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@GameRegistry.ObjectHolder(Constants.Mod.MODID)
	public static class Blocks {
	}

	@GameRegistry.ObjectHolder(Constants.Mod.MODID)
	public static class Items {
		public static final Item WARP_CHARM = net.minecraft.init.Items.AIR;
	}	
	

	@Mod.EventBusSubscriber
	public static class ObjectRegistryHandler {
		/**
		 * Listen for the register event for creating custom items
		 */
		@SubscribeEvent
		public static void addItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().register(new ItemWarpCharm());
		}

		/**
		 * Listen for the register event for creating custom blocks
		 */
		@SubscribeEvent
		public static void addBlocks(RegistryEvent.Register<Block> event) {
			//TODO: Add blocks
		}


		@SuppressWarnings("unused")
		private static void registerBlockModel(Block block) {
			ResourceLocation resourceLocation = Block.REGISTRY.getNameForObject(block);
			registerBlockModel(block, 0, resourceLocation.toString());
		}

		private static void registerBlockModel(Block block, int meta, String modelName) {
			registerItemModel(Item.getItemFromBlock(block), meta, modelName);
		}

		private static void registerItemModel(Item item, int meta, String resourcePath) {
			ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourcePath, "inventory");
			net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, meta, modelResourceLocation);
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public static void registerRenderers(ModelRegistryEvent event) {
			registerItemModel(Items.WARP_CHARM,0,Items.WARP_CHARM.getRegistryName().toString());
		}
	}
	
}
