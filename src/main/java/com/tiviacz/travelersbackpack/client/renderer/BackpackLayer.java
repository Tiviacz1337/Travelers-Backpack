package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.HumanoidRenderStateBackpackInject;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, PlayerRenderState state, float limbSwing, float limbSwingAmount) {
        if(TravelersBackpack.enableIntegration()) return;

        ItemStack backpack = ((HumanoidRenderStateBackpackInject)state).getBackpackStack();

        if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem) {
            renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, getParentModel(), poseStack, bufferIn, packedLightIn, state, backpack);
        }
    }

    public static void renderBackpackLayer(BackpackLayerModel model, HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, HumanoidRenderState renderState, ItemStack stack) {
        if(!stack.getOrDefault(ModDataComponents.IS_VISIBLE, true)) return;

        model.setBackpackStack(stack);
        model.setMultiBufferSource(bufferIn);

        if(!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        boolean translucentType = travelersBackpackItem == ModItems.QUARTZ_TRAVELERS_BACKPACK || travelersBackpackItem == ModItems.SNOW_TRAVELERS_BACKPACK;
        boolean cutoutType = travelersBackpackItem == ModItems.WARDEN_TRAVELERS_BACKPACK;

        ResourceLocation loc = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = bufferIn.getBuffer(translucentType ? RenderType.entityTranslucent(loc) : RenderType.entitySolid(loc));
        if(cutoutType) vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, model, renderState);

        if(stack.has(DataComponents.DYED_COLOR) && stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK) {
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entitySolid(loc));
            model.mainBody.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, ARGB.opaque(stack.get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);

        loc = BackpackBlockModel.getSleepingBagTexture(stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId()));
        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);

        if(renderState instanceof PlayerRenderState playerRenderState && Supporters.SUPPORTERS.contains(playerRenderState.name)) {
            model.supporterBadgeModel.render(poseStack, packedLightIn);
        }

        poseStack.popPose();
    }

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackLayerModel backpackModel, HumanoidRenderState renderState) {
        if(renderState.isCrouching) {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        parent.copyPropertiesTo(backpackModel);
        backpackModel.setupAngles(parent);

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        if(renderState.isBaby) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = renderState.ageScale;
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }
}