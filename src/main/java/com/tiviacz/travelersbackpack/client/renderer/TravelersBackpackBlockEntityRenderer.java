package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackBlockModel;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class TravelersBackpackBlockEntityRenderer implements BlockEntityRenderer<TravelersBackpackBlockEntity>
{
    public static final TravelersBackpackBlockModel model = new TravelersBackpackBlockModel(createTravelersBackpack(false).createModel());

    public TravelersBackpackBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
    {
        super();
    }

    @Override
    public void render(TravelersBackpackBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        TravelersBackpackBlockEntityRenderer.render(entity, entity.getWorld(), matrices, vertexConsumers, light, overlay);
    }

    public static TexturedModelData createTravelersBackpack(boolean isWearable)
    {
        ModelData mesh;
        ModelPartData part;

        if(isWearable)
        {
            Dilation cube = Dilation.NONE;
            mesh = PlayerEntityModel.getModelData(cube, 0.0F);
            part = mesh.getRoot().getChild("body");
        }
        else
        {
            mesh = new ModelData();
            part = mesh.getRoot();
        }

        //Main Body
        ModelPartData mainBody = part.addChild("main_body", ModelPartBuilder.create().uv(0, 9).cuboid(-5.0F, 0.0F, -3.0F, 10.0F, 9.0F, 5.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        mainBody.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -3.0F, 0.0F, 10.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F, 0.0F, -3.0F));
        mainBody.addChild("bottom", ModelPartBuilder.create().uv(0, 34).cuboid(0.0F, 0.0F, 0.0F, 10.0F, 1.0F, 4.0F), ModelTransform.pivot(-5.0F, 9.0F, -3.0F));
        mainBody.addChild("pocketFace", ModelPartBuilder.create().uv(0, 24).cuboid(-4.0F, -6.0F, 0.0F, 8, 6, 2), ModelTransform.pivot(0.0F, 6.9F, 2.0F));
        mainBody.addChild("leftStrap", ModelPartBuilder.create().uv(21, 24).cuboid(0.0F, 0.0F, -1.0F, 1, 8, 1), ModelTransform.pivot(3.0F, 0.0F, -3.0F));
        mainBody.addChild("rightStrap", ModelPartBuilder.create().uv(26, 24).cuboid(0.0F, 0.0F, -1.0F, 1, 8, 1), ModelTransform.pivot(-4.0F, 0.0F, -3.0F));

        //Left Tank
        ModelPartData leftTankTop = part.addChild("tankLeftTop", ModelPartBuilder.create().uv(0, 40).cuboid(or(isWearable, 5.0F, 0.0F), 0.0F, or(isWearable, -2.5F, 0.0F), 4, 1, 4), ModelTransform.pivot(or(isWearable, 0.0F, 5.0F), 0.0F, or(isWearable, 0.0F, -2.5F)));
        ModelPartData leftTankBottom = leftTankTop.addChild("tankLeftBottom", ModelPartBuilder.create().uv(0, 46).cuboid(0.0F, 0.0F, 0.0F, 4, 1, 4), ModelTransform.pivot(or(isWearable, 5.0F, 0.0F), 9.0F, or(isWearable, -2.5F, 0.0F)));
        leftTankBottom.addChild("tankLeftWall1", ModelPartBuilder.create().uv(0, 52).cuboid(0.0F, 0.0F, 0.0F, 1, 8, 1), ModelTransform.pivot(3.0F, -8.0F, 0.0F));
        leftTankBottom.addChild("tankLeftWall2", ModelPartBuilder.create().uv(5, 52).cuboid(0.0F, 0.0F, 0.0F, 1, 8, 1), ModelTransform.pivot(0.0F, -8.0F, 0.0F));
        leftTankBottom.addChild("tankLeftWall3", ModelPartBuilder.create().uv(10, 52).cuboid(0.0F, 0.0F, 0.0F, 1, 8, 1), ModelTransform.pivot(0.0F, -8.0F, 3.0F));
        leftTankBottom.addChild("tankLeftWall4", ModelPartBuilder.create().uv(15, 52).cuboid(0.0F, 0.0F, 0.0F, 1, 8, 1), ModelTransform.pivot(3.0F, -8.0F, 3.0F));

        //Right Tank
        ModelPartData rightTankTop = part.addChild("tankRightTop", ModelPartBuilder.create().uv(17, 40).cuboid(-9.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData rightTankBottom = rightTankTop.addChild("tankRightBottom", ModelPartBuilder.create().uv(17, 46).cuboid(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F), ModelTransform.pivot(-9.0F, 9.0F, -2.5F));
        rightTankBottom.addChild("tankRightWall1", ModelPartBuilder.create().uv(22, 52).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), ModelTransform.pivot(3.0F, -8.0F, 3.0F));
        rightTankBottom.addChild("tankRightWall2", ModelPartBuilder.create().uv(27, 52).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), ModelTransform.pivot(3.0F, -8.0F, 0.0F));
        rightTankBottom.addChild("tankRightWall3", ModelPartBuilder.create().uv(32, 52).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), ModelTransform.pivot(0.0F, -8.0F, 3.0F));
        rightTankBottom.addChild("tankRightWall4", ModelPartBuilder.create().uv(37, 52).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        //Sleeping Bag
        ModelPartData sleepingBag = part.addChild("sleepingBag", ModelPartBuilder.create().uv(31, 0).cuboid(or(isWearable, -7.0F, 0.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 2.0F, 0.0F), 14, 2, 2), ModelTransform.pivot(-7.0F, 7.0F, 2.0F));
        sleepingBag.addChild("sleepingBagStrapRightTop", ModelPartBuilder.create().uv(40, 5).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 3), ModelTransform.pivot(or(isWearable, -5.0F, 2.0F), or(isWearable, 6.0F, -1.0F), or(isWearable, 2.0F, 0.0F)));
        sleepingBag.addChild("sleepingBagStrapRightMid", ModelPartBuilder.create().uv(38, 10).cuboid(0.0F, 0.0F, 0.0F, 2, 3, 1), ModelTransform.pivot(or(isWearable, -5.0F, 2.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 4.0F, 2.0F)));
        sleepingBag.addChild("sleepingBagStrapRightBottom", ModelPartBuilder.create().uv(42, 15).cuboid(0.0F, 0.0F, 0.0F, 2, 1, 3), ModelTransform.pivot(or(isWearable, -5.0F, 2.0F), or(isWearable, 9.0F, 2.0F), or(isWearable, 1.0F, -1.0F)));
        sleepingBag.addChild("sleepingBagStrapLeftTop", ModelPartBuilder.create().uv(31, 5).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 3), ModelTransform.pivot(or(isWearable, 4.0F, 11.0F), or(isWearable, 6.0F, -1.0F), or(isWearable, 2.0F, 0.0F)));
        sleepingBag.addChild("sleepingBagStrapLeftMid", ModelPartBuilder.create().uv(31, 10).cuboid(0.0F, 0.0F, 0.0F, 2, 3, 1), ModelTransform.pivot(or(isWearable, 3.0F, 10.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 4.0F, 2.0F)));
        sleepingBag.addChild("sleepingBagStrapLeftBottom", ModelPartBuilder.create().uv(31, 15).cuboid(0.0F, 0.0F, 0.0F, 2, 1, 3), ModelTransform.pivot(or(isWearable, 3.0F, 10.0F), or(isWearable, 9.0F, 2.0F), or(isWearable, 1.0F, -1.0F)));

        //Noses, Additions
        part.addChild("villagerNose", ModelPartBuilder.create().uv(31, 20).cuboid(or(isWearable, -1.0F, 0.0F), or(isWearable, 4.0F, 0.0F), or(isWearable, 4.0F, 0.0F), 2, 4, 2), ModelTransform.pivot(or(isWearable, 0.0F, -1.0F), or(isWearable, 0.0F, 4.0F), or(isWearable, 0.0F, 4.0F)));
        part.addChild("ocelotNose", ModelPartBuilder.create().uv(42, 20).cuboid(or(isWearable, -1.0F, 0.0F), or(isWearable, 4.0F, 0.0F), or(isWearable, 4.0F, 0.0F), 3, 2, 1), ModelTransform.pivot(or(isWearable, 0.0F, -1.0F), or(isWearable, 0.0F, 3.9F), or(isWearable, 0.0F, 4.0F)));
        part.addChild("pigNose", ModelPartBuilder.create().uv(42, 20).cuboid(or(isWearable, -2.0F, 0.0F), or(isWearable, 4.0F, 0.0F), or(isWearable, 4.0F, 0.0F), 4, 3, 1), ModelTransform.pivot(or(isWearable, 0.0F, -2.0F), or(isWearable, 0.0F, 4.0F), or(isWearable, 0.0F, 4.0F)));
        part.addChild("foxNose", ModelPartBuilder.create().uv(31, 27).cuboid(or(isWearable, -2.0F, 0.0F), or(isWearable, 4.9F, 0.0F), or(isWearable, 4.0F, 0.0F),4.0F, 2.0F, 3.0F), ModelTransform.pivot(or(isWearable, 0.0F, -2.0F), or(isWearable, 0.0F, 4.9F), or(isWearable, 0.0F, 4.0F)));
        part.addChild("wolfNose", ModelPartBuilder.create().uv(46, 25).cuboid(or(isWearable, -1.5F, 0.0F), or(isWearable, 3.9F, 0.0F), or(isWearable, 4.0F, 0.0F), 3.0F, 3.0F, 3.0F), ModelTransform.pivot(or(isWearable, 0.0F, -1.5F), or(isWearable, 0.0F, 3.9F), or(isWearable, 0.0F, 4.0F)));

        part.addChild("stacks", ModelPartBuilder.create(), ModelTransform.NONE);
        part.addChild("fluids", ModelPartBuilder.create(), ModelTransform.NONE);

        return TexturedModelData.of(mesh, 64, 64);
    }

    public static float or(boolean isWearable, float first, float second)
    {
        return isWearable ? first : second;
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

    public static void renderByItem(RenderData inv, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180F));

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0F));

        matrices.scale((float)14/18, (float)10/13, (float)7/9);
        matrices.translate(0.0D, 0.016D, 0.0D);
        model.renderByItem(inv, matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }
}