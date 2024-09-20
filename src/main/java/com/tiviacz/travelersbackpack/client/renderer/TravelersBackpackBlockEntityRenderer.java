package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

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

     /*   boolean flag = level != null;
        boolean isBlockEntity = inv instanceof TravelersBackpackBlockEntity;
        BlockState blockstate = flag && isBlockEntity ? ((TravelersBackpackBlockEntity)inv).getBlockState() : ModBlocks.STANDARD_TRAVELERS_BACKPACK.get().defaultBlockState();

        if(blockstate.getBlock() instanceof TravelersBackpackBlock)
        {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

            Direction direction;

            if(!flag || !isBlockEntity)
            {
                direction = Direction.SOUTH;
            }
            else
            {
                direction = ((TravelersBackpackBlockEntity)inv).getBlockDirection((TravelersBackpackBlockEntity)inv);
            }

            if(direction == Direction.NORTH)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(180F));
            }
            if(direction == Direction.EAST)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(270F));
            }
            if(direction == Direction.SOUTH)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(0F));
            }
            if(direction == Direction.WEST)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            }

            poseStack.scale((float)14/18, (float)10/13, (float)7/9);
            poseStack.translate(0.0D, 0.016D, 0.0D);
            model.render(inv, poseStack, buffer, combinedLightIn, combinedOverlayIn);

            poseStack.popPose();
        } */
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

   /* public static void renderByItem(RenderData inv, PoseStack poseStack, MultiBufferSource vertexConsumer, int combinedLightIn, int combinedOverlayIn)
    {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

        poseStack.mulPose(Axis.YP.rotationDegrees(0F));

        poseStack.scale((float)14/18, (float)10/13, (float)7/9);
        poseStack.translate(0.0D, 0.016D, 0.0D);
        model.renderByItem(inv, poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    } */
}