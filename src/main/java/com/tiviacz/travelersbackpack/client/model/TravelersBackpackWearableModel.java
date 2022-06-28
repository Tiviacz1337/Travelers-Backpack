package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.renderer.FluidRenderer;
import com.tiviacz.travelersbackpack.client.renderer.StackRenderer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TravelersBackpackWearableModel<T extends LivingEntity> extends BipedModel<T>
{
    public ModelRenderer mainBody;
    public ModelRenderer tankLeftTop;
    public ModelRenderer tankRightTop;
    public ModelRenderer bed;
    public ModelRenderer leftStrap;
    public ModelRenderer rightStrap;
    public ModelRenderer top;
    public ModelRenderer bottom;
    public ModelRenderer pocketFace;
    public ModelRenderer tankLeftBottom;
    public ModelRenderer tankLeftWall4;
    public ModelRenderer tankLeftWall3;
    public ModelRenderer tankLeftWall2;
    public ModelRenderer tankLeftWall1;
    public ModelRenderer tankRightBottom;
    public ModelRenderer tankRightWall2;
    public ModelRenderer tankRightWall1;
    public ModelRenderer tankRightWall3;
    public ModelRenderer tankRightWall4;
    public ModelRenderer bedStrapLeftMid;
    public ModelRenderer bedStrapRightBottom;
    public ModelRenderer bedStrapLeftBottom;
    public ModelRenderer bedStrapRightMid;
    public ModelRenderer bedStrapRightTop;
    public ModelRenderer bedStrapLeftTop;

    public ModelRenderer villagerNose;
    public ModelRenderer wolfNose;
    public ModelRenderer foxNose;
    public ModelRenderer ocelotNose;
    public ModelRenderer pigNose;

    public StackRenderer stacks;
    public FluidRenderer fluids;

    private final PlayerEntity player;

    public TravelersBackpackWearableModel(PlayerEntity player, IRenderTypeBuffer buffer)
    {
        super(0.0F);
        this.player = player;
        this.texWidth = 64;
        this.texHeight = 64;

        //Main Backpack

        this.mainBody = new ModelRenderer(this, 0, 9);
        this.mainBody.addBox(-5.0F, 0.0F, -3.0F, 10, 9, 5);
        this.mainBody.setPos(0.0F, 0.0F, 0.0F);

        this.leftStrap = new ModelRenderer(this, 21, 24);
        this.leftStrap.setPos(3.0F, 0.0F, -3.0F);
        this.leftStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.leftStrap);

        this.rightStrap = new ModelRenderer(this, 26, 24);
        this.rightStrap.setPos(-4.0F, 0.0F, -3.0F);
        this.rightStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.rightStrap);

        this.top = new ModelRenderer(this, 0, 0);
        this.top.setPos(0.0F, 0.0F, -3.0F);
        this.top.addBox(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        this.mainBody.addChild(this.top);

        this.bottom = new ModelRenderer(this, 0, 34);
        this.bottom.setPos(-5.0F, 9.0F, -3.0F);
        this.bottom.addBox(0.0F, 0.0F, 0.0F, 10, 1, 4);
        this.mainBody.addChild(this.bottom);

        this.pocketFace = new ModelRenderer(this, 0, 24);
        this.pocketFace.setPos(0.0F, 6.9F, 2.0F);
        this.pocketFace.addBox(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        this.mainBody.addChild(this.pocketFace);

        //Left Tank

        this.tankLeftTop = new ModelRenderer(this, 0, 40);
        this.tankLeftTop.setPos(0.0F, 0.0F, 0.0F);
        this.tankLeftTop.addBox(5.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F);

        this.tankLeftBottom = new ModelRenderer(this, 0, 46);
        this.tankLeftBottom.setPos(5.0F, 9.0F, -2.5F);
        this.tankLeftBottom.addBox(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F);
        this.tankLeftTop.addChild(this.tankLeftBottom);

        this.tankLeftWall1 = new ModelRenderer(this, 0, 52);
        this.tankLeftWall1.setPos(3.0F, -8.0F, 0.0F);
        this.tankLeftWall1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall1);

        this.tankLeftWall2 = new ModelRenderer(this, 5, 52);
        this.tankLeftWall2.setPos(0.0F, -8.0F, 0.0F);
        this.tankLeftWall2.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall2);

        this.tankLeftWall3 = new ModelRenderer(this, 10, 52);
        this.tankLeftWall3.setPos(0.0F, -8.0F, 3.0F);
        this.tankLeftWall3.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall3);

        this.tankLeftWall4 = new ModelRenderer(this, 15, 52);
        this.tankLeftWall4.setPos(3.0F, -8.0F, 3.0F);
        this.tankLeftWall4.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall4);

        //Right Tank

        this.tankRightTop = new ModelRenderer(this, 17, 40);
        this.tankRightTop.setPos(0.0F, 0.0F, 0.0F);
        this.tankRightTop.addBox(-9.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F);

        this.tankRightBottom = new ModelRenderer(this, 17, 46);
        this.tankRightBottom.setPos(-9.0F, 9.0F, -2.5F);
        this.tankRightBottom.addBox(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F);
        this.tankRightTop.addChild(this.tankRightBottom);

        this.tankRightWall1 = new ModelRenderer(this, 22, 52);
        this.tankRightWall1.setPos(3.0F, -8.0F, 3.0F);
        this.tankRightWall1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall1);

        this.tankRightWall2 = new ModelRenderer(this, 27, 52);
        this.tankRightWall2.setPos(3.0F, -8.0F, 0.0F);
        this.tankRightWall2.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall2);

        this.tankRightWall3 = new ModelRenderer(this, 32, 52);
        this.tankRightWall3.setPos(0.0F, -8.0F, 3.0F);
        this.tankRightWall3.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall3);

        this.tankRightWall4 = new ModelRenderer(this, 37, 52);
        this.tankRightWall4.setPos(0.0F, -8.0F, 0.0F);
        this.tankRightWall4.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall4);

        //Bed

        this.bed = new ModelRenderer(this, 31, 0);
        this.bed.setPos(-7.0F, 7.0F, 2.0F);
        this.bed.addBox(-7.0F, 7.0F, 2.0F, 14F, 2F, 2F);

        this.bedStrapRightTop = new ModelRenderer(this, 40, 5);
        this.bedStrapRightTop.setPos(-5.0F, 6.0F, 2.0F);
        this.bedStrapRightTop.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F);
        this.bed.addChild(bedStrapRightTop);

        this.bedStrapRightMid = new ModelRenderer(this, 38, 10);
        this.bedStrapRightMid.setPos(-5.0F, 7.0F, 4.0F);
        this.bedStrapRightMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bed.addChild(this.bedStrapRightMid);

        this.bedStrapRightBottom = new ModelRenderer(this, 42, 15);
        this.bedStrapRightBottom.setPos(-5.0F, 9.0F, 1.0F);
        this.bedStrapRightBottom.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 3.0F);
        this.bed.addChild(this.bedStrapRightBottom);

        this.bedStrapLeftTop = new ModelRenderer(this, 31, 5);
        this.bedStrapLeftTop.setPos(4.0F, 6.0F, 2.0F);
        this.bedStrapLeftTop.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F);
        this.bed.addChild(this.bedStrapLeftTop);

        this.bedStrapLeftMid = new ModelRenderer(this, 31, 10);
        this.bedStrapLeftMid.setPos(3.0F, 7.0F, 4.0F);
        this.bedStrapLeftMid.addBox(0.0F, 0.0F, 0.0F, 2.0F, 3.0F, 1.0F);
        this.bed.addChild(this.bedStrapLeftMid);

        this.bedStrapLeftBottom = new ModelRenderer(this, 31, 15);
        this.bedStrapLeftBottom.setPos(3.0F, 9.0F, 1.0F);
        this.bedStrapLeftBottom.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 3.0F, 0.0F);
        this.bed.addChild(this.bedStrapLeftBottom);

        //Noses

        this.villagerNose = new ModelRenderer(this, 31, 20);
        this.villagerNose.setPos(0.0F, 0.0F, 0.0F);
        this.villagerNose.addBox(-1.0F, 4.0F, 4.0F, 2.0F, 4.0F, 2.0F);

        this.ocelotNose = new ModelRenderer(this, 42, 20);
        this.ocelotNose.setPos(0.0F, 0.0F, 0.0F);
        this.ocelotNose.addBox(-1.0F, 4.0F, 4.0F, 3.0F, 2.0F, 1.0F);

        this.pigNose = new ModelRenderer(this, 42, 20);
        this.pigNose.setPos(0.0F, 0.0F, 0.0F);
        this.pigNose.addBox(-2.0F, 4.0F, 4.0F, 4.0F, 3.0F, 1.0F);

        this.foxNose = new ModelRenderer(this, 31, 27);
        this.foxNose.setPos(0.0F, 0.0F, 0.0F);
        this.foxNose.addBox(-2.0F, 4.9F, 4.0F, 4.0F, 2.0F, 3.0F);

        this.wolfNose = new ModelRenderer(this, 46, 25);
        this.wolfNose.setPos(0.0F, 0.0F, 0.0F);
        this.wolfNose.addBox(-1.5F, 3.9F, 4.0F, 3.0F, 3.0F, 3.0F);

        //Extras

        this.stacks = new StackRenderer(this, player, buffer);
        this.fluids = new FluidRenderer(this, player, buffer);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        this.bed.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.tankLeftTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.tankRightTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.mainBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        if(this.player != null)
        {
            ItemStack stack = CapabilityUtils.getWearingBackpack(player);

            if(stack.getItem() == ModItems.FOX_TRAVELERS_BACKPACK.get())
            {
                this.foxNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }

            if(stack.getItem() == ModItems.WOLF_TRAVELERS_BACKPACK.get())
            {
                this.wolfNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
            {
                this.villagerNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }

            if(stack.getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get())
            {
                this.ocelotNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }

            if(stack.getItem() == ModItems.PIG_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.HORSE_TRAVELERS_BACKPACK.get())
            {
                this.pigNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }

            //Make nose for irongolem villager
        }

        if(TravelersBackpackConfig.CLIENT.renderTools.get())
        {
            this.stacks.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        }
        this.fluids.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    public void setupAngles(BipedModel<T> model)
    {
        //Backpack
        this.mainBody.copyFrom(model.body);
        this.bed.copyFrom(model.body);
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
    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim((T)entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    }
}