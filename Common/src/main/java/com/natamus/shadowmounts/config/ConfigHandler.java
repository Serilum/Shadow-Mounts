package com.natamus.shadowmounts.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.shadowmounts.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean darkenHorseTexture = true;
	@Entry public static boolean showShadowHorseParticles = true;
	@Entry public static boolean onlyShowParticlesWhenRiding = true;

	public static void initConfig() {
		configMetaData.put("darkenHorseTexture", Arrays.asList(
			"Whether the horse's texture should be darkened when equiped with a shadow saddle."
		));
		configMetaData.put("showShadowHorseParticles", Arrays.asList(
			"Whether the particles should be shown for shadow mounts."
		));
		configMetaData.put("onlyShowParticlesWhenRiding", Arrays.asList(
			"If enabled, shadow mount particles are only shown when a player is riding them."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}