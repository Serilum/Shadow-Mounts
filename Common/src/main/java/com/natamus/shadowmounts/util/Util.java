package com.natamus.shadowmounts.util;

import com.natamus.collective.functions.EntityFunctions;
import com.natamus.shadowmounts.data.Constants;
import com.natamus.shadowmounts.data.ShadowItems;
import com.natamus.shadowmounts.mixin.AbstractHorseAccessor;
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
