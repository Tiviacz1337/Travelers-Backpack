package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackBlockEntityRenderer implements BlockEntityRenderer<TravelersBackpackBlockEntity> {
    public TravelersBackpackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TravelersBackpackBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        TravelersBackpackBlockEntityRenderer.render(blockEntity, poseStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    public static void render(TravelersBackpackBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        Direction direction = blockEntity.getBlockDirection();
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * direction.get2DDataValue()));
        poseStack.scale((float)14/18, (float)10/13, (float)7/9);
        poseStack.translate(0.0D, 0.016D, 0.0D);
        BackpackBlockModel.BLOCK_MODEL.render(blockEntity, poseStack, buffer, combinedLightIn, combinedOverlayIn);
        poseStack.popPose();
    }

    public static void renderByItem(RenderData renderData, PoseStack poseStack, MultiBufferSource vertexConsumer, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale((float)14/18, (float)10/13, (float)7/9);
        poseStack.translate(0.0D, 0.016D, 0.0D);
        BackpackBlockModel.BLOCK_MODEL.renderByItem(renderData, poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        poseStack.popPose();
    }
}