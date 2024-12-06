package com.natamus.shadowmounts.util;

import com.natamus.collective.functions.EntityFunctions;
import com.natamus.shadowmounts.data.Constants;
import com.natamus.shadowmounts.data.ShadowItems;
import com.natamus.shadowmounts.mixin.AbstractHorseAccessor;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class Util {
	public static boolean wearsShadowSaddle(Level level, HorseRenderState horseRenderState) {
		AbstractHorse abstractHorse = getAbstractHorseFromRenderState(level, horseRenderState);
		if (abstractHorse == null) {
			return false;
		}

		return wearsShadowSaddle(abstractHorse);
	}
	public static boolean wearsShadowSaddle(AbstractHorse abstractHorse) {
		if (abstractHorse == null) {
			return false;
		}

		SimpleContainer inventory = ((AbstractHorseAccessor)abstractHorse).getInventory();
		if (inventory.getItem(AbstractHorse.INV_SLOT_SADDLE).getItem().equals(ShadowItems.SHADOW_SADDLE)) {
			return true;
		}

		return EntityFunctions.getAbstractHorseEntityFlagResult(abstractHorse, Constants.shadowSaddleFlag);
	}

	public static @Nullable AbstractHorse getAbstractHorseFromRenderState(Level level, HorseRenderState hrs) {
		return level.getEntitiesOfClass(AbstractHorse.class, new AABB(hrs.x - 1, hrs.y - 1, hrs.z - 1, hrs.x + 1, hrs.y + 1, hrs.z + 1)).stream().findFirst().orElse(null);
	}
}
