package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackBlockModel;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class TravelersBackpackBlockEntityRenderer extends BlockEntityRenderer<TravelersBackpackBlockEntity>
{
    public static final TravelersBackpackBlockModel model = new TravelersBackpackBlockModel();

    public TravelersBackpackBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(TravelersBackpackBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        TravelersBackpackBlockEntityRenderer.render(entity, entity.getWorld(), matrices, vertexConsumers, light, overlay);
    }

    public static void render(ITravelersBackpackInventory inv, World world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        boolean flag = world != null;
        boolean isTile = inv instanceof TravelersBackpackBlockEntity;
        BlockState blockstate = flag && isTile ? ((TravelersBackpackBlockEntity)inv).getCachedState() : ModBlocks.STANDARD_TRAVELERS_BACKPACK.getDefaultState();

        if(blockstate.getBlock() instanceof TravelersBackpackBlock)
        {
            matrices.push();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180F));

            Direction direction;

            if(!flag || !isTile)
            {
                direction = Direction.SOUTH;
            }
            else
            {
                direction = ((TravelersBackpackBlockEntity)inv).getBlockDirection((TravelersBackpackBlockEntity)inv);
            }

            if(direction == Direction.NORTH)
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F));
            }
            if(direction == Direction.EAST)
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270F));
            }
            if(direction == Direction.SOUTH)
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0F));
            }
            if(direction == Direction.WEST)
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90F));
            }

            matrices.scale((float)14/18, (float)10/13, (float)7/9);
            matrices.translate(0.0D, 0.016D, 0.0D);
            model.render(inv, matrices, vertexConsumers, light, overlay);

            matrices.pop();
        }
    }
}
