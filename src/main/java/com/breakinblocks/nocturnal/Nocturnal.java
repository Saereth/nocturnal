package com.breakinblocks.nocturnal;


import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import com.breakinblocks.nocturnal.proxy.CommonProxy;

@Mod(modid = Constants.Mod.MODID, name = Constants.Mod.NAME, version = Constants.Mod.VERSION, dependencies = Constants.Mod.DEPEND)
public class Nocturnal
{
	@Mod.Instance(Constants.Mod.MODID)
	public static Nocturnal instance;
	@SidedProxy(clientSide = "com.breakinblocks.nocturnal.proxy.ClientProxy", serverSide = "com.breakinblocks.nocturnal.proxy.ServerProxy")
	public static CommonProxy proxy;

	private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
