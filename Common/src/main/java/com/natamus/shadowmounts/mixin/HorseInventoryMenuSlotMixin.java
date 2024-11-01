package com.natamus.shadowmounts.mixin;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/inventory/HorseInventoryMenu$1", priority = 1001)
public abstract class HorseInventoryMenuSlotMixin {
	@Shadow public abstract boolean isActive();

	@Inject(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
	public void mayPlace(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack.getItem() instanceof SaddleItem && !((Slot)(Object)this).hasItem() && this.isActive()) {
			cir.setReturnValue(true);
		}
	}
}