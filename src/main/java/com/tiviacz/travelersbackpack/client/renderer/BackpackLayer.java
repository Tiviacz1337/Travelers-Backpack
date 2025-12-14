package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackModel;
import com.tiviacz.travelersbackpack.client.model.StackModelPart;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.HumanoidRenderStateBackpackInject;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BackpackLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final BackpackModel BACKPACK_MODEL = new BackpackModel();

    //RenderStates
    private final StackModelPart tools;
    private final ItemStackRenderState backpackRenderState;

    public BackpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
        this.tools = new StackModelPart();
        this.backpackRenderState = new ItemStackRenderState();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector sumbitNodeCollector, int packedLightIn, AvatarRenderState state, float limbSwing, float limbSwingAmount) {
        if(TravelersBackpack.enableIntegration()) return;

        ItemStack backpack = ((HumanoidRenderStateBackpackInject)state).getBackpackStack();

        if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem) {
            renderBackpackLayer(getParentModel(), poseStack, sumbitNodeCollector, packedLightIn, state, this.backpackRenderState, this.tools, backpack);
        }
    }

    public static void renderBackpackLayer(HumanoidModel humanoidModel, PoseStack poseStack, SubmitNodeCollector collector, int packedLightIn, HumanoidRenderState state, ItemStackRenderState backpackRenderState, StackModelPart tools, ItemStack stack) {
        if(!stack.getOrDefault(ModDataComponents.IS_VISIBLE, true))
            return;

        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, BACKPACK_MODEL, state);
        BACKPACK_MODEL.render(poseStack, packedLightIn, collector, backpackRenderState, tools, stack);

        String name = ((HumanoidRenderStateBackpackInject)state).getName();
        if(name != null && Supporters.SUPPORTERS.contains(name)) {
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