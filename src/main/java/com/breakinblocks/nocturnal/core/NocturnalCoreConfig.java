package com.breakinblocks.nocturnal.core;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Configs must be in an inner class marked with @Category, with each of the configs marked with @Config
 * Only supports static fields
 */
public class NocturnalCoreConfig {

	/**
	 * Custom config handler since the main mod's configs may not be available
	 */
	public static void load() {
		// Load Config
		final File configFile = new File(new File(NocturnalCore.MCDIR, "config"), "nocturnalcore.cfg");
		final Configuration config = new Configuration(configFile);
		config.load();
		// For each inner classes that are marked with @Category
		for (Class<?> clazz : NocturnalCoreConfig.class.getDeclaredClasses()) {
			if (!clazz.isAnnotationPresent(Category.class)) continue;
			final String categoryName = clazz.getSimpleName();
			final ConfigCategory category = config.getCategory(categoryName);
			final String categoryComment = clazz.getAnnotation(Category.class).value();
			if (categoryComment.length() > 0)
				category.setComment(categoryComment);
			// For each field inside the category marked with @Config
			for (Field field : clazz.getDeclaredFields()) {
				if (!field.isAnnotationPresent(Config.class)) continue;
				final String configName = field.getName();
				final String configComment = field.getAnnotation(Config.class).value();
				final Class<?> configType = field.getType();
				try {
					if (configType == boolean.class) {
						field.setAccessible(true);
						field.set(null, config.getBoolean(configName, categoryName,
								(boolean) field.get(null), configComment.isEmpty() ? null : configComment
						));
					} else {
						throw new RuntimeException("Config of type '" + configType + "' not supported.");
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		config.save();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Category {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Config {
		String value() default "";
	}

	@Category
	public static class MinecraftEntityAIFix {
		@Config("Hook to help fix mods that remove tasks from {@link EntityAITasks#taskEntries} without " +
				"using {@link EntityAITasks#removeTask(EntityAIBase)}")
		public static boolean makeSureAITasksAreValid = true;
	}

	@Category
	public static class ThaumcraftTaintedPlayerMob {
		@Config("Modify Tainted Swarm targeting to target non-tained mobs rather than players only")
		public static boolean taintedSwarmFindPlayerToAttackHook = true;

		@Config("Modify PotionFluxTaint to better check for tainted mob status")
		public static boolean hookPotionFluxTaint = true;
	}
}
