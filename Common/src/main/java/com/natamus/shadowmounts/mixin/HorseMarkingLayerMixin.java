package com.natamus.shadowmounts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natamus.shadowmounts.rendering.ShadowMountRenderFunctions;
import com.natamus.shadowmounts.util.Reference;
import com.natamus.shadowmounts.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = HorseMarkingLayer.class, priority = 1001)
public abstract class HorseMarkingLayerMixin extends RenderLayer<HorseRenderState, HorseModel> {
	public HorseMarkingLayerMixin(RenderLayerParent<HorseRenderState, HorseModel> $$0) {
		super($$0);
	}

	@Shadow private static @Final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS;

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HorseRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), cancellable = true)
	public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, HorseRenderState horseRenderState, float f, float g, CallbackInfo ci) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		if (!Util.wearsShadowSaddle(level, horseRenderState)) {
			return;
		}

		ResourceLocation resourceLocation = (ResourceLocation)LOCATION_BY_MARKINGS.get(horseRenderState.markings);
		if (resourceLocation == null) {
			return;
		}

		String resourceLocationPath = resourceLocation.getPath();
		ResourceLocation shadowResourceLocation = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, resourceLocationPath.replace("horse_", "shadow_horse_"));

		ShadowMountRenderFunctions.renderLayerDarkerToBuffer(this.getParentModel(), poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucent(shadowResourceLocation)), packedLight, LivingEntityRenderer.getOverlayCoords(horseRenderState, 0.0F));

		ci.cancel();
	}
}