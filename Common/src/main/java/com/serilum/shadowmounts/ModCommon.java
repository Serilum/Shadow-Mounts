package com.serilum.shadowmounts;

import com.natamus.collective.functions.CreativeModeTabFunctions;
import com.natamus.collective.services.Services;
import com.serilum.shadowmounts.config.ConfigHandler;
import com.serilum.shadowmounts.data.ShadowItems;
import com.serilum.shadowmounts.item.ShadowSaddleItem;
import com.serilum.shadowmounts.util.Reference;
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
		Services.REGISTERITEM.registerItem(modEventBusObject, new ResourceLocation(Reference.MOD_ID, "shadow_saddle"), () -> new ShadowSaddleItem(new Item.Properties()), CreativeModeTabFunctions.getCreativeModeTabResourceKey("tools_and_utilities"), true);
	}

	public static void setAssets() {
		ShadowItems.SHADOW_SADDLE = Services.REGISTERITEM.getRegisteredItem(new ResourceLocation(Reference.MOD_ID, "shadow_saddle"));
	}
}