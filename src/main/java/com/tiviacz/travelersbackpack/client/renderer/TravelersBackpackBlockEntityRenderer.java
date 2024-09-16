package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class TravelersBackpackBlockEntityRenderer implements BlockEntityRenderer<TravelersBackpackBlockEntity> {
    public TravelersBackpackBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super();
    }

    @Override
    public void render(TravelersBackpackBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        TravelersBackpackBlockEntityRenderer.render(entity, entity.getWorld(), matrices, vertexConsumers, light, overlay);
    }

    public static void render(ITravelersBackpackInventory inv, World world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean flag = world != null;
        boolean isTile = inv instanceof TravelersBackpackBlockEntity;
        BlockState blockstate = flag && isTile ? ((TravelersBackpackBlockEntity) inv).getCachedState() : ModBlocks.STANDARD_TRAVELERS_BACKPACK.getDefaultState();

        if (blockstate.getBlock() instanceof TravelersBackpackBlock) {
            matrices.push();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F));

            Direction direction;

            if (!flag || !isTile) {
                direction = Direction.SOUTH;
            } else {
                direction = ((TravelersBackpackBlockEntity) inv).getBlockDirection((TravelersBackpackBlockEntity) inv);
            }

            if (direction == Direction.NORTH) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F));
            }
            if (direction == Direction.EAST) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270F));
            }
            if (direction == Direction.SOUTH) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0F));
            }
            if (direction == Direction.WEST) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90F));
            }

            matrices.scale((float) 14 / 18, (float) 10 / 13, (float) 7 / 9);
            matrices.translate(0.0D, 0.016D, 0.0D);
            BackpackBlockModel.BLOCK_MODEL.render(inv, matrices, vertexConsumers, light, overlay);

            matrices.pop();
        }
    }

    public static void renderByItem(RenderData inv, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F));

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0F));

        matrices.scale((float) 14 / 18, (float) 10 / 13, (float) 7 / 9);
        matrices.translate(0.0D, 0.016D, 0.0D);
        BackpackBlockModel.BLOCK_MODEL.renderByItem(inv, matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }
}