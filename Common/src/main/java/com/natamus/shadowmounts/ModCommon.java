package com.natamus.shadowmounts;

import com.natamus.collective.functions.CreativeModeTabFunctions;
import com.natamus.collective.services.Services;
import com.natamus.shadowmounts.config.ConfigHandler;
import com.natamus.shadowmounts.data.ShadowItems;
import com.natamus.shadowmounts.item.ShadowSaddleItem;
import com.natamus.shadowmounts.util.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModCommon {

	public static void init() {
		ConfigHandler.initConfig();
		load();
	}

	private static void load() {
		
	}

	public static void registerAssets(Object modEventBusObject) {
		Services.REGISTERITEM.registerItem(modEventBusObject, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "shadow_saddle"), (properties) -> new ShadowSaddleItem(properties), new Item.Properties(), CreativeModeTabFunctions.getCreativeModeTabResourceKey("tools_and_utilities"), true);
	}

	public static void setAssets() {
		ShadowItems.SHADOW_SADDLE = Services.REGISTERITEM.getRegisteredItem(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "shadow_saddle"));
	}
}