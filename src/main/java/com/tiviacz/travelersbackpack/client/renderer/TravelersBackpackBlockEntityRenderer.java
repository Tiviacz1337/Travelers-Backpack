package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackBlockModel;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TravelersBackpackBlockEntityRenderer implements BlockEntityRenderer<TravelersBackpackBlockEntity>
{
    public static final TravelersBackpackBlockModel model = new TravelersBackpackBlockModel(createTravelersBackpack(false).bakeRoot());

    public TravelersBackpackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TravelersBackpackBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
    {
        TravelersBackpackBlockEntityRenderer.render(blockEntity, blockEntity.getLevel(), poseStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    public static LayerDefinition createTravelersBackpack(boolean isWearable)
    {
        MeshDefinition mesh;
        PartDefinition part;

        if(isWearable)
        {
            CubeDeformation cube = CubeDeformation.NONE;
            mesh = HumanoidModel.createMesh(cube, 0.0F);
            part = mesh.getRoot().getChild("body");
        }
        else
        {
            mesh = new MeshDefinition();
            part = mesh.getRoot();
        }

        //Main Body
        PartDefinition mainBody = part.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 9).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 9.0F, 5.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        mainBody.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.0F, 0.0F, 10.0F, 3.0F, 5.0F), PartPose.offset(0.0F, 0.0F, -3.0F));
        mainBody.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 34).addBox(0.0F, 0.0F, 0.0F, 10.0F, 1.0F, 4.0F), PartPose.offset(-5.0F, 9.0F, -3.0F));
        mainBody.addOrReplaceChild("pocketFace", CubeListBuilder.create().texOffs(0, 24).addBox(-4.0F, -6.0F, 0.0F, 8, 6, 2), PartPose.offset(0.0F, 6.9F, 2.0F));
        mainBody.addOrReplaceChild("leftStrap", CubeListBuilder.create().texOffs(21, 24).addBox(0.0F, 0.0F, -1.0F, 1, 8, 1), PartPose.offset(3.0F, 0.0F, -3.0F));
        mainBody.addOrReplaceChild("rightStrap", CubeListBuilder.create().texOffs(26, 24).addBox(0.0F, 0.0F, -1.0F, 1, 8, 1), PartPose.offset(-4.0F, 0.0F, -3.0F));

        //Left Tank
        PartDefinition leftTankTop = part.addOrReplaceChild("tankLeftTop", CubeListBuilder.create().texOffs(0, 40).addBox(or(isWearable, 5.0F, 0.0F), 0.0F, or(isWearable, -2.5F, 0.0F), 4, 1, 4), PartPose.offset(or(isWearable, 0.0F, 5.0F), 0.0F, or(isWearable, 0.0F, -2.5F)));
        PartDefinition leftTankBottom = leftTankTop.addOrReplaceChild("tankLeftBottom", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, 0.0F, 0.0F, 4, 1, 4), PartPose.offset(or(isWearable, 5.0F, 0.0F), 9.0F, or(isWearable, -2.5F, 0.0F)));
        leftTankBottom.addOrReplaceChild("tankLeftWall1", CubeListBuilder.create().texOffs(0, 52).addBox(0.0F, 0.0F, 0.0F, 1, 8, 1), PartPose.offset(3.0F, -8.0F, 0.0F));
        leftTankBottom.addOrReplaceChild("tankLeftWall2", CubeListBuilder.create().texOffs(5, 52).addBox(0.0F, 0.0F, 0.0F, 1, 8, 1), PartPose.offset(0.0F, -8.0F, 0.0F));
        leftTankBottom.addOrReplaceChild("tankLeftWall3", CubeListBuilder.create().texOffs(10, 52).addBox(0.0F, 0.0F, 0.0F, 1, 8, 1), PartPose.offset(0.0F, -8.0F, 3.0F));
        leftTankBottom.addOrReplaceChild("tankLeftWall4", CubeListBuilder.create().texOffs(15, 52).addBox(0.0F, 0.0F, 0.0F, 1, 8, 1), PartPose.offset(3.0F, -8.0F, 3.0F));

        //Right Tank
        PartDefinition rightTankTop = part.addOrReplaceChild("tankRightTop", CubeListBuilder.create().texOffs(17, 40).addBox(-9.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightTankBottom = rightTankTop.addOrReplaceChild("tankRightBottom", CubeListBuilder.create().texOffs(17, 46).addBox(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F), PartPose.offset(-9.0F, 9.0F, -2.5F));
        rightTankBottom.addOrReplaceChild("tankRightWall1", CubeListBuilder.create().texOffs(22, 52).addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(3.0F, -8.0F, 3.0F));
        rightTankBottom.addOrReplaceChild("tankRightWall2", CubeListBuilder.create().texOffs(27, 52).addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(3.0F, -8.0F, 0.0F));
        rightTankBottom.addOrReplaceChild("tankRightWall3", CubeListBuilder.create().texOffs(32, 52).addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(0.0F, -8.0F, 3.0F));
        rightTankBottom.addOrReplaceChild("tankRightWall4", CubeListBuilder.create().texOffs(37, 52).addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(0.0F, -8.0F, 0.0F));

        //Sleeping Bag
        PartDefinition sleepingBag = part.addOrReplaceChild("sleepingBag", CubeListBuilder.create().texOffs(31, 0).addBox(or(isWearable, -7.0F, 0.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 2.0F, 0.0F), 14, 2, 2), PartPose.offset(-7.0F, 7.0F, 2.0F));

        //Sleeping Bag Extras
        PartDefinition sleepingBagExtras = part.addOrReplaceChild("sleepingBagExtras", CubeListBuilder.create().texOffs(64, 64).addBox(or(isWearable, -7.0F, 0.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 2.0F, 0.0F), 0, 0, 0), PartPose.offset(-7.0F, 7.0F, 2.0F));
        sleepingBagExtras.addOrReplaceChild("sleepingBagStrapRightTop", CubeListBuilder.create().texOffs(40, 5).addBox(0.0F, 0.0F, 0.0F, 1, 1, 3), PartPose.offset(or(isWearable, -5.0F, 2.0F), or(isWearable, 6.0F, -1.0F), or(isWearable, 2.0F, 0.0F)));
        sleepingBagExtras.addOrReplaceChild("sleepingBagStrapRightMid", CubeListBuilder.create().texOffs(38, 10).addBox(0.0F, 0.0F, 0.0F, 2, 3, 1), PartPose.offset(or(isWearable, -5.0F, 2.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 4.0F, 2.0F)));
        sleepingBagExtras.addOrReplaceChild("sleepingBagStrapRightBottom", CubeListBuilder.create().texOffs(42, 15).addBox(0.0F, 0.0F, 0.0F, 2, 1, 3), PartPose.offset(or(isWearable, -5.0F, 2.0F), or(isWearable, 9.0F, 2.0F), or(isWearable, 1.0F, -1.0F)));
        sleepingBagExtras.addOrReplaceChild("sleepingBagStrapLeftTop", CubeListBuilder.create().texOffs(31, 5).addBox(0.0F, 0.0F, 0.0F, 1, 1, 3), PartPose.offset(or(isWearable, 4.0F, 11.0F), or(isWearable, 6.0F, -1.0F), or(isWearable, 2.0F, 0.0F)));
        sleepingBagExtras.addOrReplaceChild("sleepingBagStrapLeftMid", CubeListBuilder.create().texOffs(31, 10).addBox(0.0F, 0.0F, 0.0F, 2, 3, 1), PartPose.offset(or(isWearable, 3.0F, 10.0F), or(isWearable, 7.0F, 0.0F), or(isWearable, 4.0F, 2.0F)));
        sleepingBagExtras.addOrReplaceChild("sleepingBagStrapLeftBottom", CubeListBuilder.create().texOffs(31, 15).addBox(0.0F, 0.0F, 0.0F, 2, 1, 3), PartPose.offset(or(isWearable, 3.0F, 10.0F), or(isWearable, 9.0F, 2.0F), or(isWearable, 1.0F, -1.0F)));

        //Noses, Additions
        part.addOrReplaceChild("villagerNose", CubeListBuilder.create().texOffs(31, 20).addBox(or(isWearable, -1.0F, 0.0F), or(isWearable, 4.0F, 0.0F), or(isWearable, 4.0F, 0.0F), 2, 4, 2), PartPose.offset(or(isWearable, 0.0F, -1.0F), or(isWearable, 0.0F, 4.0F), or(isWearable, 0.0F, 4.0F)));
        part.addOrReplaceChild("ocelotNose", CubeListBuilder.create().texOffs(42, 20).addBox(or(isWearable, -1.0F, 0.0F), or(isWearable, 4.0F, 0.0F), or(isWearable, 4.0F, 0.0F), 3, 2, 1), PartPose.offset(or(isWearable, 0.0F, -1.0F), or(isWearable, 0.0F, 3.9F), or(isWearable, 0.0F, 4.0F)));
        part.addOrReplaceChild("pigNose", CubeListBuilder.create().texOffs(42, 20).addBox(or(isWearable, -2.0F, 0.0F), or(isWearable, 4.0F, 0.0F), or(isWearable, 4.0F, 0.0F), 4, 3, 1), PartPose.offset(or(isWearable, 0.0F, -2.0F), or(isWearable, 0.0F, 4.0F), or(isWearable, 0.0F, 4.0F)));
        part.addOrReplaceChild("foxNose", CubeListBuilder.create().texOffs(31, 27).addBox(or(isWearable, -2.0F, 0.0F), or(isWearable, 4.9F, 0.0F), or(isWearable, 4.0F, 0.0F),4.0F, 2.0F, 3.0F), PartPose.offset(or(isWearable, 0.0F, -2.0F), or(isWearable, 0.0F, 4.9F), or(isWearable, 0.0F, 4.0F)));
        part.addOrReplaceChild("wolfNose", CubeListBuilder.create().texOffs(46, 25).addBox(or(isWearable, -1.5F, 0.0F), or(isWearable, 3.9F, 0.0F), or(isWearable, 4.0F, 0.0F), 3.0F, 3.0F, 3.0F), PartPose.offset(or(isWearable, 0.0F, -1.5F), or(isWearable, 0.0F, 3.9F), or(isWearable, 0.0F, 4.0F)));

        part.addOrReplaceChild("stacks", CubeListBuilder.create(), PartPose.ZERO);
        part.addOrReplaceChild("fluids", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    public static float or(boolean isWearable, float first, float second)
    {
        return isWearable ? first : second;
    }

    public static void render(ITravelersBackpackContainer inv, Level level, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
    {
        boolean flag = level != null;
        boolean isBlockEntity = inv instanceof TravelersBackpackBlockEntity;
        BlockState blockstate = flag && isBlockEntity ? ((TravelersBackpackBlockEntity)inv).getBlockState() : ModBlocks.STANDARD_TRAVELERS_BACKPACK.get().defaultBlockState();

        if(blockstate.getBlock() instanceof TravelersBackpackBlock)
        {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

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
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            }
            if(direction == Direction.EAST)
            {
                poseStack.mulPose(Vector3f.YP.rotationDegrees(270F));
            }
            if(direction == Direction.SOUTH)
            {
                poseStack.mulPose(Vector3f.YP.rotationDegrees(0F));
            }
            if(direction == Direction.WEST)
            {
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90F));
            }

            poseStack.scale((float)14/18, (float)10/13, (float)7/9);
            poseStack.translate(0.0D, 0.016D, 0.0D);
            model.render(inv, poseStack, buffer, combinedLightIn, combinedOverlayIn);

            poseStack.popPose();
        }
    }

    public static void renderByItem(RenderData inv, PoseStack poseStack, MultiBufferSource vertexConsumer, int combinedLightIn, int combinedOverlayIn)
    {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

        poseStack.mulPose(Vector3f.YP.rotationDegrees(0F));

        poseStack.scale((float)14/18, (float)10/13, (float)7/9);
        poseStack.translate(0.0D, 0.016D, 0.0D);
        model.renderByItem(inv, poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    }
}