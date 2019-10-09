package com.breakinblocks.nocturnal;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

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
			return new ItemStack(Items.REDSTONE);
			
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

}
