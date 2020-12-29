package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.client.renderer.FluidRenderer;
import com.tiviacz.travelersbackpack.client.renderer.StackRenderer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

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
    public ModelRenderer pigNose;
    public ModelRenderer ocelotNose;

    public StackRenderer stacks;
    public FluidRenderer fluids;

    private final PlayerEntity player;

    public TravelersBackpackWearableModel(PlayerEntity player, IRenderTypeBuffer buffer)
    {
        super(0.0F);
        this.player = player;
        this.textureWidth = 64;
        this.textureHeight = 64;

        //Main Backpack

        this.mainBody = new ModelRenderer(this, 0, 9);
        this.mainBody.addBox(-5.0F, 0.0F, -3.0F, 10, 9, 5);
        this.mainBody.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.leftStrap = new ModelRenderer(this, 21, 24);
        this.leftStrap.setRotationPoint(3.0F, 0.0F, -3.0F);
        this.leftStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.leftStrap);

        this.rightStrap = new ModelRenderer(this, 26, 24);
        this.rightStrap.setRotationPoint(-4.0F, 0.0F, -3.0F);
        this.rightStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.rightStrap);

        this.top = new ModelRenderer(this, 0, 0);
        this.top.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.top.addBox(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        this.mainBody.addChild(this.top);

        this.bottom = new ModelRenderer(this, 0, 34);
        this.bottom.setRotationPoint(-5.0F, 9.0F, -3.0F);
        this.bottom.addBox(0.0F, 0.0F, 0.0F, 10, 1, 4);
        this.mainBody.addChild(this.bottom);

        this.pocketFace = new ModelRenderer(this, 0, 24);
        this.pocketFace.setRotationPoint(0.0F, 6.9F, 2.0F);
        this.pocketFace.addBox(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        this.mainBody.addChild(this.pocketFace);

        //Left Tank

        this.tankLeftTop = new ModelRenderer(this, 0, 40);
        this.tankLeftTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tankLeftTop.addBox(5.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F);

        this.tankLeftBottom = new ModelRenderer(this, 0, 46);
        this.tankLeftBottom.setRotationPoint(5.0F, 9.0F, -2.5F);
        this.tankLeftBottom.addBox(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F);
        this.tankLeftTop.addChild(this.tankLeftBottom);

        this.tankLeftWall1 = new ModelRenderer(this, 0, 52);
        this.tankLeftWall1.setRotationPoint(3.0F, -8.0F, 0.0F);
        this.tankLeftWall1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall1);

        this.tankLeftWall2 = new ModelRenderer(this, 5, 52);
        this.tankLeftWall2.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.tankLeftWall2.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall2);

        this.tankLeftWall3 = new ModelRenderer(this, 10, 52);
        this.tankLeftWall3.setRotationPoint(0.0F, -8.0F, 3.0F);
        this.tankLeftWall3.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall3);

        this.tankLeftWall4 = new ModelRenderer(this, 15, 52);
        this.tankLeftWall4.setRotationPoint(3.0F, -8.0F, 3.0F);
        this.tankLeftWall4.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall4);

        //Right Tank

        this.tankRightTop = new ModelRenderer(this, 17, 40);
        this.tankRightTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tankRightTop.addBox(-9.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F);

        this.tankRightBottom = new ModelRenderer(this, 17, 46);
        this.tankRightBottom.setRotationPoint(-9.0F, 9.0F, -2.5F);
        this.tankRightBottom.addBox(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F);
        this.tankRightTop.addChild(this.tankRightBottom);

        this.tankRightWall1 = new ModelRenderer(this, 22, 52);
        this.tankRightWall1.setRotationPoint(3.0F, -8.0F, 3.0F);
        this.tankRightWall1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall1);

        this.tankRightWall2 = new ModelRenderer(this, 27, 52);
        this.tankRightWall2.setRotationPoint(3.0F, -8.0F, 0.0F);
        this.tankRightWall2.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall2);

        this.tankRightWall3 = new ModelRenderer(this, 32, 52);
        this.tankRightWall3.setRotationPoint(0.0F, -8.0F, 3.0F);
        this.tankRightWall3.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall3);

        this.tankRightWall4 = new ModelRenderer(this, 37, 52);
        this.tankRightWall4.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.tankRightWall4.addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall4);

        //Bed

        this.bed = new ModelRenderer(this, 31, 0);
        this.bed.setRotationPoint(-7.0F, 7.0F, 2.0F);
        this.bed.addBox(-7.0F, 7.0F, 2.0F, 14F, 2F, 2F);

        this.bedStrapRightTop = new ModelRenderer(this, 40, 5);
        this.bedStrapRightTop.setRotationPoint(-5.0F, 6.0F, 2.0F);
        this.bedStrapRightTop.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F);
        this.bed.addChild(bedStrapRightTop);

        this.bedStrapRightMid = new ModelRenderer(this, 38, 10);
        this.bedStrapRightMid.setRotationPoint(-5.0F, 7.0F, 4.0F);
        this.bedStrapRightMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bed.addChild(this.bedStrapRightMid);

        this.bedStrapRightBottom = new ModelRenderer(this, 42, 15);
        this.bedStrapRightBottom.setRotationPoint(-5.0F, 9.0F, 1.0F);
        this.bedStrapRightBottom.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 3.0F);
        this.bed.addChild(this.bedStrapRightBottom);

        this.bedStrapLeftTop = new ModelRenderer(this, 31, 5);
        this.bedStrapLeftTop.setRotationPoint(4.0F, 6.0F, 2.0F);
        this.bedStrapLeftTop.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F);
        this.bed.addChild(this.bedStrapLeftTop);

        this.bedStrapLeftMid = new ModelRenderer(this, 31, 10);
        this.bedStrapLeftMid.setRotationPoint(3.0F, 7.0F, 4.0F);
        this.bedStrapLeftMid.addBox(0.0F, 0.0F, 0.0F, 2.0F, 3.0F, 1.0F);
        this.bed.addChild(this.bedStrapLeftMid);

        this.bedStrapLeftBottom = new ModelRenderer(this, 31, 15);
        this.bedStrapLeftBottom.setRotationPoint(3.0F, 9.0F, 1.0F);
        this.bedStrapLeftBottom.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 3.0F, 0.0F);
        this.bed.addChild(this.bedStrapLeftBottom);

        //Noses

        this.villagerNose = new ModelRenderer(this, 32, 20);
        this.villagerNose.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.villagerNose.addBox(-1.0F, 4.0F, 4.0F, 2.0F, 4.0F, 2.0F);

        this.ocelotNose = new ModelRenderer(this, 42, 20);
        this.ocelotNose.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ocelotNose.addBox(-1.0F, 4.0F, 4.0F, 3.0F, 2.0F, 1.0F);

        this.pigNose = new ModelRenderer(this, 42, 20);
        this.pigNose.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.pigNose.addBox(-2.0F, 4.0F, 4.0F, 4.0F, 3.0F, 1.0F);

        //Extras

        this.stacks = new StackRenderer(this, player, buffer);
        this.fluids = new FluidRenderer(this, player, buffer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        this.bed.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.tankLeftTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.tankRightTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        if(this.player != null)
        {
            this.mainBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
           // String color = CapabilityUtils.getBackpackInv(player).getColor();

           /* if(color.equals("Quartz") || color.equals("Slime") || color.equals("Snow"))
            {
                RenderSystem.enableBlend();
                this.mainBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                RenderSystem.disableBlend();
            }
            else
            {
                this.mainBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            if(color.equals("IronGolem") || color.equals("Villager"))
            {
                this.villagerNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            if(color.equals("Pig") || color.equals("Horse"))
            {
                this.pigNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            if(color.equals("Ocelot"))
            {
                this.ocelotNose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            } */
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
        this.mainBody.copyModelAngles(model.bipedBody);
        this.bed.copyModelAngles(model.bipedBody);
        this.tankLeftTop.copyModelAngles(model.bipedBody);
        this.tankRightTop.copyModelAngles(model.bipedBody);

        //Noses
        this.villagerNose.copyModelAngles(model.bipedBody);
        this.pigNose.copyModelAngles(model.bipedBody);
        this.ocelotNose.copyModelAngles(model.bipedBody);

        //Extras
        this.stacks.copyModelAngles(model.bipedBody);
        this.fluids.copyModelAngles(model.bipedBody);
    }

    @Override
    public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setRotationAngles((T)entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }
}