package com.tiviacz.travelersbackpackold.client.renderer;

import com.tiviacz.travelersbackpackold.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class TravelersBackpackEntityFeature extends FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> {
    public TravelersBackpackEntityFeature(LivingEntityRenderer<LivingEntity, BipedEntityModel<LivingEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.getEquippedStack(EquipmentSlot.BODY).getItem() instanceof TravelersBackpackItem) {
            TravelersBackpackFeature.renderBackpackFeature(BackpackFeatureModel.FEATURE_MODEL, this.getContextModel(), matrices, vertexConsumers, light, entity, entity.getEquippedStack(EquipmentSlot.BODY));
        }
    }
}