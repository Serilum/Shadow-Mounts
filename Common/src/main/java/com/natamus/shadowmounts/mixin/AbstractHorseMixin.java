package com.natamus.shadowmounts.mixin;

import com.natamus.collective.functions.EntityFunctions;
import com.natamus.shadowmounts.data.Constants;
import com.natamus.shadowmounts.data.ShadowItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractHorse.class, priority = 1001)
public abstract class AbstractHorseMixin {
	@Shadow protected SimpleContainer inventory;
	@Shadow protected abstract void setFlag(int i, boolean b);

	@Inject(method = "containerChanged(Lnet/minecraft/world/Container;)V", at = @At(value = "TAIL"))
	public void containerChanged(Container container, CallbackInfo ci) {
		this.setFlag(Constants.shadowSaddleFlag, this.inventory.getItem(0).getItem().equals(ShadowItems.SHADOW_SADDLE));
	}

	@Inject(method = "createInventory()V", at = @At(value = "TAIL"))
	protected void createInventory(CallbackInfo ci) {
		if (EntityFunctions.getAbstractHorseEntityFlagResult((AbstractHorse)(Object)this, Constants.shadowSaddleFlag)) {
			this.inventory.setItem(0, new ItemStack(ShadowItems.SHADOW_SADDLE));
		}
	}

	@Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "TAIL"))
	public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		boolean wearsShadowSaddle = this.inventory.getItem(0).getItem().equals(ShadowItems.SHADOW_SADDLE) || EntityFunctions.getAbstractHorseEntityFlagResult((AbstractHorse)(Object)this, Constants.shadowSaddleFlag);

		compoundTag.putBoolean("WearsShadowSaddle", wearsShadowSaddle);
	}

	@Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;updateContainerEquipment()V"))
	public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		if (compoundTag.contains("WearsShadowSaddle")) {
			if (compoundTag.getBoolean("WearsShadowSaddle")) {
				this.inventory.setItem(0, new ItemStack(ShadowItems.SHADOW_SADDLE));
				this.setFlag(Constants.shadowSaddleFlag, true);
			}
		}
	}
}