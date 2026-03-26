package com.natamus.shadowmounts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natamus.shadowmounts.rendering.ShadowMountRenderFunctions;
import com.natamus.shadowmounts.util.Reference;
import com.natamus.shadowmounts.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Map;

@Mixin(value = HorseMarkingLayer.class, priority = 1001)
public abstract class HorseMarkingLayerMixin extends RenderLayer<HorseRenderState, HorseModel> {

    @Shadow private static @Final Identifier INVISIBLE_TEXTURE;

    @Shadow private static @Final Map<?, ?> LOCATION_BY_MARKINGS;

    @Unique
    private static Method adultMethod;
    @Unique
    private static Method babyMethod;

    public HorseMarkingLayerMixin(RenderLayerParent<HorseRenderState, HorseModel> renderer) {
        super(renderer);
    }

    @Unique
    private static Identifier getTextureFromVariant(Object variant, boolean isBaby) {
        try {
            if (adultMethod == null) {
                adultMethod = variant.getClass().getMethod("adult");
                babyMethod = variant.getClass().getMethod("baby");
            }

            return (Identifier) (isBaby ? babyMethod : adultMethod).invoke(variant);
        } catch (Exception e) {
            return null;
        }
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HorseRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, HorseRenderState horseRenderState, float f, float g, CallbackInfo ci) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        if (!Util.wearsShadowSaddle(level, horseRenderState)) return;

        Object variant = LOCATION_BY_MARKINGS.get(horseRenderState.markings);
        if (variant == null) return;

        Identifier identifier = getTextureFromVariant(variant, horseRenderState.isBaby);
        if (identifier == null) return;

        if (identifier.equals(INVISIBLE_TEXTURE)) return;

        String identifierPath = identifier.getPath();
        Identifier shadowIdentifier = Identifier.fromNamespaceAndPath(Reference.MOD_ID, identifierPath.replace("horse_", "shadow_horse_"));

        RenderType renderType = RenderTypes.entityTranslucent(shadowIdentifier);
        int overlayCoords = LivingEntityRenderer.getOverlayCoords(horseRenderState, 0.0F);

        ShadowMountRenderFunctions.renderLayerDarkerToBuffer(this.getParentModel(), poseStack, submitNodeCollector, packedLight, overlayCoords, renderType, horseRenderState);

        ci.cancel();
    }
}