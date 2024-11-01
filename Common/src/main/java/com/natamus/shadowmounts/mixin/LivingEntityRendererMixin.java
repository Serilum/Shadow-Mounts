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
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
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
	@Shadow protected EntityModel<LivingEntity> model;
	@Shadow protected final List<RenderLayer<LivingEntity, EntityModel<LivingEntity>>> layers = Lists.newArrayList();

	@Shadow protected abstract RenderType getRenderType(LivingEntity livingEntity, boolean bodyVisible, boolean translucent, boolean glowing);
	@Shadow protected abstract float getWhiteOverlayProgress(LivingEntity livingEntity, float partialTicks);
	@Shadow protected abstract void setupRotations(LivingEntity livingEntity, PoseStack poseStack, float f, float g, float h);
	@Shadow protected abstract void scale(LivingEntity livingEntity, PoseStack poseStack, float partialTickTime);

	@Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "TAIL"))
	public void render(LivingEntity livingEntity, float livingEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo ci) {
		if (!(livingEntity instanceof AbstractHorse abstractHorse)) {
			return;
		}

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
        this.model.attackTime = livingEntity.getAttackAnim(partialTicks);
        this.model.riding = livingEntity.isPassenger();
        this.model.young = livingEntity.isBaby();
        float f = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
        float g = Mth.rotLerp(partialTicks, livingEntity.yHeadRotO, livingEntity.yHeadRot);
        float h = g - f;
        float i;
        if (livingEntity.isPassenger()) {
            Entity var11 = livingEntity.getVehicle();
            if (var11 instanceof LivingEntity) {
                LivingEntity passengerlivingEntity = (LivingEntity)var11;
                f = Mth.rotLerp(partialTicks, passengerlivingEntity.yBodyRotO, passengerlivingEntity.yBodyRot);
                h = g - f;
                i = Mth.wrapDegrees(h);
                if (i < -85.0F) {
                    i = -85.0F;
                }

                if (i >= 85.0F) {
                    i = 85.0F;
                }

                f = g - i;
                if (i * i > 2500.0F) {
                    f += i * 0.2F;
                }

                h = g - f;
            }
        }

        float j = Mth.lerp(partialTicks, livingEntity.xRotO, livingEntity.getXRot());
        if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
            j *= -1.0F;
            h *= -1.0F;
        }

        h = Mth.wrapDegrees(h);
        float k;
        if (livingEntity.hasPose(Pose.SLEEPING)) {
            Direction direction = livingEntity.getBedOrientation();
            if (direction != null) {
                k = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                poseStack.translate((float)(-direction.getStepX()) * k, 0.0F, (float)(-direction.getStepZ()) * k);
            }
        }

        i = livingEntity.getScale();
        poseStack.scale(i, i, i);
        k = (float)livingEntity.tickCount + partialTicks;
		this.setupRotations(livingEntity, poseStack, k, f, partialTicks);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(livingEntity, poseStack, partialTicks);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        float l = 0.0F;
        float m = 0.0F;
        if (!livingEntity.isPassenger() && livingEntity.isAlive()) {
            l = livingEntity.walkAnimation.speed(partialTicks);
            m = livingEntity.walkAnimation.position(partialTicks);
            if (livingEntity.isBaby()) {
                m *= 3.0F;
            }

            if (l > 1.0F) {
                l = 1.0F;
            }
        }

        this.model.prepareMobModel(livingEntity, m, l, partialTicks);
        this.model.setupAnim(livingEntity, m, l, k, h, j);
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = !livingEntity.isInvisible();
        boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraft.player);
        boolean bl3 = minecraft.shouldEntityAppearGlowing(livingEntity);
        RenderType renderType = this.getRenderType(livingEntity, bl, bl2, bl3);
        if (renderType != null) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(livingEntity, this.getWhiteOverlayProgress(livingEntity, partialTicks));

			ShadowMountRenderFunctions.renderDarkerToBuffer(this.model, poseStack, vertexConsumer, packedLight, overlayCoords, bl2);
        }

        if (!livingEntity.isSpectator()) {
			for (RenderLayer<LivingEntity, EntityModel<LivingEntity>> layer : this.layers) {
				layer.render(poseStack, multiBufferSource, packedLight, livingEntity, m, l, partialTicks, k, h, j);
			}
        }

        poseStack.popPose();

		ShadowMountRenderFunctions.renderTrailingParticles(abstractHorse, player);
	}

    @SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType modifyRenderType(RenderType originalRenderType, LivingEntity livingEntity, float livingEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        if (livingEntity instanceof AbstractHorse abstractHorse && Util.wearsShadowSaddle(abstractHorse)) {
            return null;
        }

        return originalRenderType;
    }
}