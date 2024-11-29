package com.tiviacz.travelersbackpackold.client.renderer;

import com.tiviacz.travelersbackpackold.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpackold.client.model.BackpackBlockModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(value= EnvType.CLIENT)
public class TravelersBackpackBlockEntityRenderer implements BlockEntityRenderer<TravelersBackpackBlockEntity> {
    public TravelersBackpackBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super();
    }

    @Override
    public void render(TravelersBackpackBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        TravelersBackpackBlockEntityRenderer.render(blockEntity, matrices, vertexConsumers, light, overlay);
    }

    public static void render(TravelersBackpackBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = blockEntity.getBlockDirection();
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F * direction.getHorizontal()));
        matrices.scale((float) 14 / 18, (float) 10 / 13, (float) 7 / 9);
        matrices.translate(0.0D, 0.016D, 0.0D);
        BackpackBlockModel.BLOCK_MODEL.render(blockEntity, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }

    public static void renderByItem(RenderData inv, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F));
        matrices.scale((float) 14 / 18, (float) 10 / 13, (float) 7 / 9);
        matrices.translate(0.0D, 0.016D, 0.0D);
        BackpackBlockModel.BLOCK_MODEL.renderByItem(inv, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }
}