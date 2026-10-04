package com.tiviacz.travelersbackpack.compat.emf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.BackpackModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import traben.entity_model_features.models.animation.state.EMFBipedPose;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;

public class EntityModelFeaturesCompat {
    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackModel backpackModel, HumanoidRenderState state) {
        EMFEntityRenderState emfState = EMFEntityRenderState.from(state);
        if(emfState != null) {
            EMFBipedPose pose = emfState.getBipedPose();
            if(pose != null) {
                pose.applyTo(parent);
                parent.root().translateAndRotate(poseStack);
            }
        }
    }
}