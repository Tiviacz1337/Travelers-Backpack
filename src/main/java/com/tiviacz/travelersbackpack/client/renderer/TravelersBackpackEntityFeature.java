package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class TravelersBackpackEntityFeature extends FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> {
    public TravelersBackpackEntityFeature(LivingEntityRenderer<LivingEntity, BipedEntityModel<LivingEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (TravelersBackpackConfig.getConfig().client.disableBackpackRender) return;

        if (ComponentUtils.isWearingBackpack(entity) && !entity.isInvisible()) {
            TravelersBackpackFeature.renderBackpackFeature(BackpackFeatureModel.FEATURE_MODEL, this.getContextModel(), matrices, vertexConsumers, light, entity, ComponentUtils.getComponent(entity).getWearable());
        }
    }
}