package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackModel;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

public class BackpackLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    public static final ContextKey<ItemStack> BACKPACK_KEY = new ContextKey<ItemStack>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"));
    public static final ContextKey<String> NAME_KEY = new ContextKey<String>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "name"));
    private static final BackpackModel BACKPACK_MODEL = new BackpackModel();

    public BackpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector sumbitNodeCollector, int packedLightIn, AvatarRenderState state, float limbSwing, float limbSwingAmount) {
        if(TravelersBackpack.enableIntegration()) return;

        ItemStack backpack = state.getRenderData(BACKPACK_KEY);

        if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem) {
            renderBackpackLayer(getParentModel(), poseStack, sumbitNodeCollector, packedLightIn, state, backpack);
        }
    }

    /*@Override
    public void render(PoseStack poseStack, SubmitNodeCollector collector, int packedLightIn, AvatarRenderState state, float limbSwing, float limbSwingAmount) {
        if(TravelersBackpack.enableIntegration()) return;

        ItemStack backpack = state.getRenderData(BACKPACK_KEY);

        if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem) {
            renderBackpackLayer(getParentModel(), poseStack, collector, packedLightIn, state, backpack);
        }
    }*/

    public static void renderBackpackLayer(HumanoidModel humanoidModel, PoseStack poseStack, SubmitNodeCollector collector, int packedLightIn, HumanoidRenderState state, ItemStack stack) {
        if(!stack.getOrDefault(ModDataComponents.IS_VISIBLE, true))
            return;

        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, BACKPACK_MODEL, state);
        BACKPACK_MODEL.render(poseStack, packedLightIn, collector, stack);

        if(state instanceof AvatarRenderState && Supporters.SUPPORTERS.contains(state.getRenderData(NAME_KEY))) {
            BACKPACK_MODEL.supporterBadgeModel.render(poseStack, packedLightIn);
        }

        poseStack.popPose();
    }

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackModel backpackModel, HumanoidRenderState state) {
        backpackModel.copyFrom(parent.body);
        backpackModel.supporterBadgeModel.copyFrom(parent.body);

        if(state.isBaby) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = state.scale;
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }
}