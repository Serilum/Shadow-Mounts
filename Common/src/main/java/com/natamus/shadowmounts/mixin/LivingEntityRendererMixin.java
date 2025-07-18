package com.natamus.shadowmounts.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.natamus.shadowmounts.rendering.ShadowMountRenderFunctions;
import com.natamus.shadowmounts.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(value = LivingEntityRenderer.class, priority = 1001)
public abstract class LivingEntityRendererMixin {
	@Shadow protected EntityModel<LivingEntityRenderState> model;
	@Shadow protected final List<RenderLayer<LivingEntityRenderState, EntityModel<LivingEntityRenderState>>> layers = Lists.newArrayList();

	@Shadow protected abstract RenderType getRenderType(LivingEntityRenderState livingEntityRenderState, boolean bodyVisible, boolean translucent, boolean glowing);
	@Shadow protected abstract float getWhiteOverlayProgress(LivingEntityRenderState livingEntityRenderState);
	@Shadow protected abstract void setupRotations(LivingEntityRenderState livingEntityRenderState, PoseStack poseStack, float bodyRot, float scale);
	@Shadow protected abstract void scale(LivingEntityRenderState livingEntityRenderState, PoseStack poseStack);
	@Shadow protected abstract boolean shouldRenderLayers(LivingEntityRenderState livingEntityRenderState);
	@Shadow protected abstract int getModelTint(LivingEntityRenderState livingEntityRenderState);

	@Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "TAIL"))
	public void render(LivingEntityRenderState livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo ci) {
		if (!(livingEntityRenderState instanceof HorseRenderState horseRenderState)) {
			return;
		}

		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		AbstractHorse abstractHorse = Util.getAbstractHorseFromRenderState(level, horseRenderState);
		if (!Util.wearsShadowSaddle(abstractHorse)) {
			return;
		}

		Player player = null;
		List<Entity> passengers = abstractHorse.getPassengers();
		if (!passengers.isEmpty()) {
			Optional<Player> optionalPlayer = passengers.stream().filter(passenger -> passenger instanceof Player).map(passenger -> (Player) passenger).findFirst();
			if (optionalPlayer.isPresent()) {
				player = optionalPlayer.get();
			}
		}

        poseStack.pushPose();
        if (livingEntityRenderState.hasPose(Pose.SLEEPING)) {
            Direction direction = livingEntityRenderState.bedOrientation;
            if (direction != null) {
                float f = livingEntityRenderState.eyeHeight - 0.1F;
                poseStack.translate((float)(-direction.getStepX()) * f, 0.0F, (float)(-direction.getStepZ()) * f);
            }
        }

        float g = livingEntityRenderState.scale;
        poseStack.scale(g, g, g);
        this.setupRotations(livingEntityRenderState, poseStack, livingEntityRenderState.bodyRot, g);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(livingEntityRenderState, poseStack);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        this.model.setupAnim(livingEntityRenderState);
        boolean bl = !livingEntityRenderState.isInvisible;
        boolean bl2 = !bl && !livingEntityRenderState.isInvisibleToPlayer;
        RenderType renderType = this.getRenderType(livingEntityRenderState, bl, bl2, livingEntityRenderState.appearsGlowing);
        if (renderType != null) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(livingEntityRenderState, this.getWhiteOverlayProgress(livingEntityRenderState));

			ShadowMountRenderFunctions.renderDarkerToBuffer(this.model, poseStack, vertexConsumer, packedLight, overlayCoords);
        }

        if (this.shouldRenderLayers(livingEntityRenderState)) {
			for (RenderLayer<LivingEntityRenderState, EntityModel<LivingEntityRenderState>> layer : this.layers) {
				layer.render(poseStack, multiBufferSource, packedLight, livingEntityRenderState, livingEntityRenderState.yRot, livingEntityRenderState.xRot);
			}
        }

        poseStack.popPose();

		ShadowMountRenderFunctions.renderTrailingParticles(abstractHorse, player);
	}

    @SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType modifyRenderType(RenderType originalRenderType, LivingEntityRenderState livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
		if (livingEntityRenderState instanceof HorseRenderState horseRenderState) {
			Level level = Minecraft.getInstance().level;
			if (level != null) {
				if (Util.wearsShadowSaddle(level, horseRenderState)) {
					return null;
				}
			}
		}
		
        return originalRenderType;
    }
}