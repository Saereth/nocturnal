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

	@Config.Comment({"Tainted Mob Options"})
	public static ConfigTainted tainted = new ConfigTainted();

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		System.out.println("Syncing Nocturnal Config");
		if (event.getModID().equals(Constants.Mod.MODID))
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE);
	}

	public static class ConfigGeneral {
		@Config.Comment({"Determines the amount of damage the warp charm can take before breaking. -1 for unbreakable, otherwise 1~2147483647. Default: 50"})
		public int     warpCharmDamage = 50;
		
		@Config.Comment({"Determines the speed at which a player causes flux in a chunk, once per n ticks. Default: 600. This is still nescesary for flux updates even if player flux generation is off."})
		public int     fluxPollutionTicktime = 600;

		@Config.Comment({"Setting this to false will cause players to no longer generate flux. Defilement can still occur if flux is brought above the set threshold by standard TC means however."})
		public boolean playersProduceFlux = true;

		@Config.Comment({"Setting this to false will defilement to never happen under any circumstances."})
		public boolean enableDefilement = true;
		
		@Config.Comment({"The amount of flux in a chunk before blocks and biomes start converting to defiled land. Default: 300"})
		public int     minDefileFlux = 300;
		
		@Config.Comment({"Chance for charm to be damaged when preventing flux from the player. 1 out of n chance. Default: 5"})
		public int     charmDamageChance = 5;
		
		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the regen buff default: 50"})
		public float minFluxForRegen = 50;

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the strength buff default: 50 set to -1 to disable"})
		public float minFluxForStrength = 50;
		
		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the Absorption buff default: 50 set to -1 to disable"})
		public float minFluxForAbsorption = 50;

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the Health buff default: 50 set to -1 to disable"})
		public float minFluxForHealthBoost = 50;		
		
		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the haste buff default: 50 set to -1 to disable"})
		public float minFluxForHaste = 50;		
		
		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the speed buff default: 50 set to -1 to disable"})
		public float minFluxForSpeed = 50;		
		
		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the resistance buff default: 50 set to -1 to disable"})
		public float minFluxForResistance = 50;		

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the saturation buff default: 50 set to -1 to disable"})
		public float minFluxForSaturation = 50;			

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the blindness buff default: 50 set to -1 to disable"})
		public float minFluxForBlindness = -1;			

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the slowness buff default: 50 set to -1 to disable"})
		public float minFluxForSlowness = -1;			

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the weakness buff default: 50 set to -1 to disable"})
		public float minFluxForWeakness = -1;			

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the nausea buff default: 50 set to -1 to disable"})
		public float minFluxForNausea = -1;			

		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the poison buff default: 50 set to -1 to disable"})
		public float minFluxForPoison = -1;			
		
		@Config.Comment({"Once the flux in the area exceeds this number the player will gain the wither buff default: 50 set to -1 to disable"})
		public float minFluxForWither = -1;
	}

	public static class ConfigTainted {
		@Config.Comment({"Modify AI of tainted mobs to target non-tainted mobs and forgive recently tainted mobs"})
		public boolean modifyAIOfTaintedMobs = true;

		@Config.Comment({"Treat player as tainted mob (has no effect if modifyAIOfTaintedMobs is not true)"})
		public boolean playerIsTaintedMob = true;
	}

	public enum Mode {
		DISABLED,
		WHITELIST,
		BLACKLIST
	}

}