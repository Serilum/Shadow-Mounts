package com.natamus.shadowmounts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natamus.shadowmounts.rendering.ShadowMountRenderFunctions;
import com.natamus.shadowmounts.util.Reference;
import com.natamus.shadowmounts.util.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = HorseMarkingLayer.class, priority = 1001)
public abstract class HorseMarkingLayerMixin extends RenderLayer<Horse, HorseModel<Horse>> {
	public HorseMarkingLayerMixin(RenderLayerParent<Horse, HorseModel<Horse>> renderLayerParent) {
		super(renderLayerParent);
	}

	@Shadow private static @Final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS;

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), cancellable = true)
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Horse horse, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		if (!(horse instanceof AbstractHorse abstractHorse)) {
			return;
		}

		if (!Util.wearsShadowSaddle(abstractHorse)) {
			return;
		}

		ResourceLocation resourceLocation = (ResourceLocation)LOCATION_BY_MARKINGS.get(horse.getMarkings());
		if (resourceLocation == null) {
			return;
		}

		String resourceLocationPath = resourceLocation.getPath();
		ResourceLocation shadowResourceLocation = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, resourceLocationPath.replace("horse_", "shadow_horse_"));

		ShadowMountRenderFunctions.renderLayerDarkerToBuffer(this.getParentModel(), poseStack, buffer.getBuffer(RenderType.entityTranslucent(shadowResourceLocation)), packedLight, LivingEntityRenderer.getOverlayCoords(horse, 0.0F));

		ci.cancel();
	}
}