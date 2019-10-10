package com.breakinblocks.nocturnal.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

import com.breakinblocks.nocturnal.Constants;
import com.breakinblocks.nocturnal.Nocturnal;
import com.breakinblocks.nocturnal.NocturnalContent;

import WayofTime.bloodmagic.client.IMeshProvider;
import WayofTime.bloodmagic.client.IVariantProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		OBJLoader.INSTANCE.addDomain(Constants.Mod.MODID);
		
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void tryHandleItemModel(Item item) {
		if (item instanceof IVariantProvider) {
			Int2ObjectMap<String> variants = new Int2ObjectOpenHashMap<>();
			((IVariantProvider) item).gatherVariants(variants);
			variants.forEach((i, v) -> ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName(), v)));
		} else if (item instanceof IMeshProvider) {
			IMeshProvider mesh = (IMeshProvider) item;
			final ResourceLocation location = mesh.getCustomLocation() != null ? mesh.getCustomLocation() : item.getRegistryName();
			Set<String> variants = new HashSet<>();
			mesh.gatherVariants(variants::add);
			variants.forEach(v -> ModelLoader.registerItemVariants(item, new ModelResourceLocation(location, v)));
			ModelLoader.setCustomMeshDefinition(item, mesh.getMeshDefinition());

		}
	}

	@Override
	public void tryHandleBlockModel(Block block) {
		if (block instanceof IVariantProvider) {
			Int2ObjectMap<String> variants = new Int2ObjectOpenHashMap<>();
			((IVariantProvider) block).gatherVariants(variants);
			variants.forEach((i, v) -> ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(block.getRegistryName(), v)));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		NocturnalContent.items.forEach(Nocturnal.proxy::tryHandleItemModel);
	}
    
	@Override
	public boolean fancyGraphics() {
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
	}
}
