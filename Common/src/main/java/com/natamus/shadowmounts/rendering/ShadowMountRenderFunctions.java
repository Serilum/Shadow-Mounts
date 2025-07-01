package com.natamus.shadowmounts.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.natamus.shadowmounts.config.ConfigHandler;
import com.natamus.shadowmounts.data.Constants;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ShadowMountRenderFunctions {
	private static final List<Pair<SimpleParticleType, Integer>> trailParticles = Arrays.asList(new Pair<>(ParticleTypes.ASH, 1), new Pair<>(ParticleTypes.CRIMSON_SPORE, 8), new Pair<>(ParticleTypes.SOUL_FIRE_FLAME, 32), new Pair<>(ParticleTypes.LAVA, 64));

	public static void renderDarkerToBuffer(EntityModel<?> entityModel, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int overlayCoords) {
		if (ConfigHandler.darkenHorseTexture) {
			entityModel.renderToBuffer(poseStack, vertexConsumer, packedLight / 5, overlayCoords, Constants.shadowMountColour);
			return;
		}

		entityModel.renderToBuffer(poseStack, vertexConsumer, packedLight, overlayCoords);
	}
	public static void renderLayerDarkerToBuffer(EntityModel<?> entityModel, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int overlayCoords) {
		if (ConfigHandler.darkenHorseTexture) {
			entityModel.renderToBuffer(poseStack, vertexConsumer, packedLight, overlayCoords, Constants.shadowMountMarkingsColour);
			return;
		}

		entityModel.renderToBuffer(poseStack, vertexConsumer, packedLight, overlayCoords);
	}

	public static void renderTrailingParticles(AbstractHorse abstractHorse, @Nullable Player player) {
		if (!ConfigHandler.showShadowHorseParticles) {
			return;
		}

		Level level = abstractHorse.level();
		if (!level.isClientSide) {
			return;
		}

		if (player == null && ConfigHandler.onlyShowParticlesWhenRiding) {
			return;
		}

		double particleBehindoffsetDistance = -1.0;
		float yRot = abstractHorse.getYRot() * ((float) Math.PI / 180F);
		double xBaseOffset = particleBehindoffsetDistance * -Math.sin(yRot);
		double zBaseOffset = particleBehindoffsetDistance * Math.cos(yRot);

		double xBaseParticleVelocity = Math.sin(yRot) * 0.1;
		double zBaseParticleVelocity = -Math.cos(yRot) * 0.1;

		boolean isMoving = abstractHorse.getDeltaMovement().horizontalDistanceSqr() > 0.0001;

		for (Pair<SimpleParticleType, Integer> particlePair : trailParticles) {
			int tickDelay = particlePair.getSecond();
			if (!(isMoving && tickDelay == 32)) {
				if (abstractHorse.tickCount % tickDelay != 0) {
					continue;
				}
			}

			SimpleParticleType particle = particlePair.getFirst();

			double randomXOffset = xBaseOffset + (Constants.random.nextDouble() - 0.5) * 0.2;
			double randomYOffset = 0.5 + (Constants.random.nextDouble() - 0.5) * 0.2;
			double randomZOffset = zBaseOffset + (Constants.random.nextDouble() - 0.5) * 0.2;

			double xRandomParticleVelocity = xBaseParticleVelocity + (Constants.random.nextDouble() - 0.5) * 0.02;
			double zrandomParticleVelocity = zBaseParticleVelocity + (Constants.random.nextDouble() - 0.5) * 0.02;

			level.addParticle(
					particle,
					abstractHorse.getX() + randomXOffset,
					abstractHorse.getY() + randomYOffset,
					abstractHorse.getZ() + randomZOffset,
					xRandomParticleVelocity,
					-abstractHorse.getDeltaMovement().y * 0.5,
					zrandomParticleVelocity
			);
		}
	}
}
