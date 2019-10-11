package com.breakinblocks.nocturnal;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Constants.Mod.MODID, name = Constants.Mod.NAME, category = "")
@Mod.EventBusSubscriber(modid = Constants.Mod.MODID)
public class NocturnalConfig {
	@Config.Comment({"General Options"})
	public static ConfigGeneral    general    = new ConfigGeneral();

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		System.out.println("Syncing Nocturnal Config");
		if (event.getModID().equals(Constants.Mod.MODID))
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE);
	}

	public static class ConfigGeneral {
		@Config.Comment({"Determines the amount of damage the warp charm can take before breaking. -1 for unbreakable, otherwise 1~2147483647. Default: 50"})
		public int     warpCharmDamage = 50;
		
		@Config.Comment({"Determines the speed at which a player causes flux in a chunk, once per n ticks. Default: 600"})
		public int     fluxPollutionTicktime = 600;

		
		@Config.Comment({"Chance for charm to be damaged when preventing flux from the player. 1 out of n chance. Default: 5"})
		public int     charmDamageChance = 5;
	}

	public enum Mode {
		DISABLED,
		WHITELIST,
		BLACKLIST
	}

}