package com.natamus.shadowmounts.util;

import com.natamus.shadowmounts.data.ShadowItems;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

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
		
		return abstractHorse.getItemBySlot(EquipmentSlot.SADDLE).getItem().equals(ShadowItems.SHADOW_SADDLE);
	}

	public static @Nullable AbstractHorse getAbstractHorseFromRenderState(Level level, HorseRenderState hrs) {
		return level.getEntitiesOfClass(AbstractHorse.class, new AABB(hrs.x - 1, hrs.y - 1, hrs.z - 1, hrs.x + 1, hrs.y + 1, hrs.z + 1)).stream().findFirst().orElse(null);
	}

	public static Equippable equippableSaddle() {
		return Equippable.builder(EquipmentSlot.SADDLE)
		    .setEquipSound(SoundEvents.HORSE_SADDLE)
		    .setAsset(EquipmentAssets.SADDLE)
		    .setAllowedEntities(EntityType.HORSE, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE)
		    .setEquipOnInteract(true)
		    .setCanBeSheared(true)
		    .setShearingSound(SoundEvents.SADDLE_UNEQUIP)
		    .build();
	}
}
