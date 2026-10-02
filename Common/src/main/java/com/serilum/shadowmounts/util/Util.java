package com.serilum.shadowmounts.util;

import com.natamus.collective.functions.EntityFunctions;
import com.serilum.shadowmounts.data.Constants;
import com.serilum.shadowmounts.data.ShadowItems;
import com.serilum.shadowmounts.mixin.AbstractHorseAccessor;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class Util {
	public static boolean wearsShadowSaddle(AbstractHorse abstractHorse) {
		SimpleContainer inventory = ((AbstractHorseAccessor)abstractHorse).getInventory();
		if (inventory.getItem(AbstractHorse.INV_SLOT_SADDLE).getItem().equals(ShadowItems.SHADOW_SADDLE)) {
			return true;
		}

		return EntityFunctions.getAbstractHorseEntityFlagResult(abstractHorse, Constants.shadowSaddleFlag);
	}
}
