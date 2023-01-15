package com.tiviacz.travelersbackpack.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class TravelersBackpackWearableModel extends HumanoidModel<AbstractClientPlayer>
{
    public ModelPart mainBody;
    public ModelPart tankLeftTop;
    public ModelPart tankRightTop;
    public ModelPart sleepingBag;
    public ModelPart sleepingBagExtras;
    public ModelPart leftStrap;
    public ModelPart rightStrap;
    public ModelPart top;
    public ModelPart bottom;
    public ModelPart pocketFace;
    public ModelPart tankLeftBottom;
    public ModelPart tankLeftWall4;
    public ModelPart tankLeftWall3;
    public ModelPart tankLeftWall2;
    public ModelPart tankLeftWall1;
    public ModelPart tankRightBottom;
    public ModelPart tankRightWall2;
    public ModelPart tankRightWall1;
    public ModelPart tankRightWall3;
    public ModelPart tankRightWall4;
    public ModelPart sleepingBagStrapLeftMid;
    public ModelPart sleepingBagStrapRightBottom;
    public ModelPart sleepingBagStrapLeftBottom;
    public ModelPart sleepingBagStrapRightMid;
    public ModelPart sleepingBagStrapRightTop;
    public ModelPart sleepingBagStrapLeftTop;

    public ModelPart villagerNose;
    public ModelPart wolfNose;
    public ModelPart foxNose;
    public ModelPart ocelotNose;
    public ModelPart pigNose;

    public StackModelPart stacks;
    public FluidModelPart fluids;

    private final Player player;
    private final MultiBufferSource buffer;

    public TravelersBackpackWearableModel(Player player, MultiBufferSource buffer, ModelPart rootPart)
    {
        super(rootPart);
        this.player = player;
        this.buffer = buffer;

        //Main Backpack

        this.mainBody = rootPart.getChild("body").getChild("main_body");
        this.top = this.mainBody.getChild("top");
        this.bottom = this.mainBody.getChild("bottom");
        this.pocketFace = this.mainBody.getChild("pocketFace");
        this.leftStrap = this.mainBody.getChild("leftStrap");
        this.rightStrap = this.mainBody.getChild("rightStrap");

        //Left Tank

        this.tankLeftTop = rootPart.getChild("body").getChild("tankLeftTop");
        this.tankLeftBottom = this.tankLeftTop.getChild("tankLeftBottom");

        this.tankLeftWall1 = this.tankLeftBottom.getChild("tankLeftWall1");
        this.tankLeftWall2 = this.tankLeftBottom.getChild("tankLeftWall2");
        this.tankLeftWall3 = this.tankLeftBottom.getChild("tankLeftWall3");
        this.tankLeftWall4 = this.tankLeftBottom.getChild("tankLeftWall4");

        //Right Tank
        this.tankRightTop = rootPart.getChild("body").getChild("tankRightTop");
        this.tankRightBottom = this.tankRightTop.getChild("tankRightBottom");

        this.tankRightWall1 = this.tankRightBottom.getChild("tankRightWall1");
        this.tankRightWall2 = this.tankRightBottom.getChild("tankRightWall2");
        this.tankRightWall3 = this.tankRightBottom.getChild("tankRightWall3");
        this.tankRightWall4 = this.tankRightBottom.getChild("tankRightWall4");

        this.sleepingBag = rootPart.getChild("body").getChild("sleepingBag");
        this.sleepingBagExtras = rootPart.getChild("body").getChild("sleepingBagExtras");
        this.sleepingBagStrapLeftTop = this.sleepingBagExtras.getChild("sleepingBagStrapLeftTop");
        this.sleepingBagStrapLeftMid = this.sleepingBagExtras.getChild("sleepingBagStrapLeftMid");
        this.sleepingBagStrapLeftBottom = this.sleepingBagExtras.getChild("sleepingBagStrapLeftBottom");
        this.sleepingBagStrapRightTop = this.sleepingBagExtras.getChild("sleepingBagStrapRightTop");
        this.sleepingBagStrapRightMid = this.sleepingBagExtras.getChild("sleepingBagStrapRightMid");
        this.sleepingBagStrapRightBottom = this.sleepingBagExtras.getChild("sleepingBagStrapRightBottom");

        //Noses, Additions

        this.villagerNose = rootPart.getChild("body").getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("body").getChild("ocelotNose");
        this.pigNose = rootPart.getChild("body").getChild("pigNose");
        this.foxNose = rootPart.getChild("body").getChild("foxNose");
        this.wolfNose = rootPart.getChild("body").getChild("wolfNose");

        //Extras

        this.stacks = new StackModelPart(rootPart.getChild("body").getChild("stacks"));
        this.fluids = new FluidModelPart(rootPart.getChild("body").getChild("fluids"));
    }

    public void setupAngles(PlayerModel model)
    {
        //Backpack
        this.mainBody.copyFrom(model.body);
        this.sleepingBag.copyFrom(model.body);
        this.sleepingBagExtras.copyFrom(model.body);
        this.tankLeftTop.copyFrom(model.body);
        this.tankRightTop.copyFrom(model.body);

        //Noses
        this.villagerNose.copyFrom(model.body);
        this.pigNose.copyFrom(model.body);
        this.ocelotNose.copyFrom(model.body);
        this.wolfNose.copyFrom(model.body);
        this.foxNose.copyFrom(model.body);

        //Extras
        this.stacks.copyFrom(model.body);
        this.fluids.copyFrom(model.body);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        if(alpha == 0.25F)
        {
            this.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, 1.0F);
        }
        else
        {
            this.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.sleepingBagExtras.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.tankLeftTop.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.tankRightTop.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.mainBody.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);

            if(this.player != null)
            {
                ItemStack stack = CapabilityUtils.getWearingBackpack(player);

                if(stack.getItem() == ModItems.FOX_TRAVELERS_BACKPACK.get())
                {
                    this.foxNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
                }

                if(stack.getItem() == ModItems.WOLF_TRAVELERS_BACKPACK.get())
                {
                    this.wolfNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
                }

                if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
                {
                    this.villagerNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
                }

                if(stack.getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get())
                {
                    this.ocelotNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
                }

                if(stack.getItem() == ModItems.PIG_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.HORSE_TRAVELERS_BACKPACK.get())
                {
                    this.pigNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
                }
            }

            if(TravelersBackpackConfig.renderTools)
            {
                this.stacks.render(poseStack, vertexConsumer, this.player, this.buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
            this.fluids.render(poseStack, vertexConsumer, this.player, this.buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts()
    {
        return ImmutableList.of(this.head);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts()
    {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
    }
}