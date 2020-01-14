package com.breakinblocks.nocturnal;

import net.minecraft.util.ResourceLocation;

import java.util.Locale;

@SuppressWarnings("unused")
public class Constants {

	public static class Localizations {
		public static class Text {
			//public static final String ERROR_MESSAGE         = "text.component.nocturnal.error.message";
		}
	}

	public static class Misc {
		public static final String DAMAGE_ABSOLUTE     = "nocturnal.absolute";
	}


	public static class Mod {
		public static final String MODID   = "nocturnal";
		public static final String DOMAIN  = MODID.toLowerCase(Locale.ENGLISH) + ":";
		public static final String NAME    = "Nocturnal";
		public static final String VERSION = "1.1";
		public static final String DEPEND = "required-after:thaumcraft;";
	            
	}

	public static class Deps {
		public static final String THAUMCRAFT = "thaumcraft";
	}

}