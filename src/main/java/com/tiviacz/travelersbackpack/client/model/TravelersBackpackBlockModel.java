package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

public class TravelersBackpackBlockModel
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

    public TravelersBackpackBlockModel()
    {
        int textureWidth = 64;
        int textureHeight = 64;

        //Main Backpack

        this.mainBody = new ModelRenderer(textureWidth, textureHeight, 0, 9);
        this.mainBody.setPos(0.0F, 0.0F, 0.0F);
        this.mainBody.addBox(-5.0F, 0.0F, -3.0F, 10, 9, 5);

        this.leftStrap = new ModelRenderer(textureWidth, textureHeight, 21, 24);
        this.leftStrap.setPos(3.0F, 0.0F, -3.0F);
        this.leftStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.leftStrap);

        this.rightStrap = new ModelRenderer(textureWidth, textureHeight, 26, 24);
        this.rightStrap.setPos(-4.0F, 0.0F, -3.0F);
        this.rightStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.rightStrap);

        this.top = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        this.top.setPos(0.0F, 0.0F, -3.0F);
        this.top.addBox(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        this.mainBody.addChild(this.top);

        this.bottom = new ModelRenderer(textureWidth, textureHeight, 0, 34);
        this.bottom.setPos(-5.0F, 9.0F, -3.0F);
        this.bottom.addBox(0.0F, 0.0F, 0.0F, 10, 1, 4);
        this.mainBody.addChild(this.bottom);

        this.pocketFace = new ModelRenderer(textureWidth, textureHeight, 0, 24);
        this.pocketFace.setPos(0.0F, 6.9F, 2.0F);
        this.pocketFace.addBox(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        this.mainBody.addChild(this.pocketFace);

        //Left Tank

        this.tankLeftTop = new ModelRenderer(textureWidth, textureHeight, 0, 40);
        this.tankLeftTop.setPos(5.0F, 0.0F, -2.5F);
        this.tankLeftTop.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);

        this.tankLeftBottom = new ModelRenderer(textureWidth, textureHeight, 0, 46);
        this.tankLeftBottom.setPos(0.0F, 9.0F, 0.0F);
        this.tankLeftBottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.tankLeftTop.addChild(this.tankLeftBottom);

        this.tankLeftWall1 = new ModelRenderer(textureWidth, textureHeight, 0, 52);
        this.tankLeftWall1.setPos(3.0F, -8.0F, 0.0F);
        this.tankLeftWall1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall1);

        this.tankLeftWall2 = new ModelRenderer(textureWidth, textureHeight, 5, 52);
        this.tankLeftWall2.setPos(0.0F, -8.0F, 0.0F);
        this.tankLeftWall2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall2);

        this.tankLeftWall3 = new ModelRenderer(textureWidth, textureHeight, 10, 52);
        this.tankLeftWall3.setPos(0.0F, -8.0F, 3.0F);
        this.tankLeftWall3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall3);

        this.tankLeftWall4 = new ModelRenderer(textureWidth, textureHeight, 15, 52);
        this.tankLeftWall4.setPos(3.0F, -8.0F, 3.0F);
        this.tankLeftWall4.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall4);

        //Right Tank

        this.tankRightTop = new ModelRenderer(textureWidth, textureHeight, 17, 40);
        this.tankRightTop.setPos(-9.0F, 0.0F, -2.5F);
        this.tankRightTop.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);

        this.tankRightBottom = new ModelRenderer(textureWidth, textureHeight, 17, 46);
        this.tankRightBottom.setPos(0.0F, 9.0F, 0.0F);
        this.tankRightBottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.tankRightTop.addChild(this.tankRightBottom);

        this.tankRightWall1 = new ModelRenderer(textureWidth, textureHeight, 22, 52);
        this.tankRightWall1.setPos(3.0F, -8.0F, 3.0F);
        this.tankRightWall1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall1);

        this.tankRightWall2 = new ModelRenderer(textureWidth, textureHeight, 27, 52);
        this.tankRightWall2.setPos(3.0F, -8.0F, 0.0F);
        this.tankRightWall2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall2);

        this.tankRightWall3 = new ModelRenderer(textureWidth, textureHeight, 32, 52);
        this.tankRightWall3.setPos(0.0F, -8.0F, 3.0F);
        this.tankRightWall3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall3);

        this.tankRightWall4 = new ModelRenderer(textureWidth, textureHeight, 37, 52);
        this.tankRightWall4.setPos(0.0F, -8.0F, 0.0F);
        this.tankRightWall4.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall4);

        //Bed

        this.bed = new ModelRenderer(textureWidth, textureHeight, 31, 0);
        this.bed.setPos(-7.0F, 7.0F, 2.0F);
        this.bed.addBox(0.0F, 0.0F, 0.0F, 14, 2, 2);

        this.bedStrapRightTop = new ModelRenderer(textureWidth, textureHeight, 40, 5);
        this.bedStrapRightTop.setPos(2.0F, -1.0F, 0.0F);
        this.bedStrapRightTop.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        this.bed.addChild(this.bedStrapRightTop);

        this.bedStrapRightMid = new ModelRenderer(textureWidth, textureHeight, 38, 10);
        this.bedStrapRightMid.setPos(2.0F, 0.0F, 2.0F);
        this.bedStrapRightMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bed.addChild(this.bedStrapRightMid);

        this.bedStrapRightBottom = new ModelRenderer(textureWidth, textureHeight, 42, 15);
        this.bedStrapRightBottom.setPos(2.0F, 2.0F, -1.0F);
        this.bedStrapRightBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 3);
        this.bed.addChild(this.bedStrapRightBottom);

        this.bedStrapLeftTop = new ModelRenderer(textureWidth, textureHeight, 31, 5);
        this.bedStrapLeftTop.setPos(11.0F, -1.0F, 0.0F);
        this.bedStrapLeftTop.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        this.bed.addChild(this.bedStrapLeftTop);

        this.bedStrapLeftMid = new ModelRenderer(textureWidth, textureHeight, 31, 10);
        this.bedStrapLeftMid.setPos(10.0F, 0.0F, 2.0F);
        this.bedStrapLeftMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bed.addChild(this.bedStrapLeftMid);

        this.bedStrapLeftBottom = new ModelRenderer(textureWidth, textureHeight, 31, 15);
        this.bedStrapLeftBottom.setPos(10.0F, 2.0F, -1.0F);
        this.bedStrapLeftBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 3);
        this.bed.addChild(this.bedStrapLeftBottom);

        //Noses, Additions

        this.villagerNose = new ModelRenderer(textureWidth, textureHeight, 31, 20);
        this.villagerNose.setPos(-1.0F, 4.0F, 4.0F);
        this.villagerNose.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2);

        this.ocelotNose = new ModelRenderer(textureWidth, textureHeight, 42, 20);
        this.ocelotNose.setPos(-1.0F, 3.9F, 4.0F);
        this.ocelotNose.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1);

        this.pigNose = new ModelRenderer(textureWidth, textureHeight, 42, 20);
        this.pigNose.setPos(-2.0F, 4.0F, 4.0F);
        this.pigNose.addBox(0.0F, 0.0F, 0.0F, 4, 3, 1);

        this.foxNose = new ModelRenderer(textureWidth, textureHeight, 31, 27);
        this.foxNose.setPos(-2.0F, 4.9F, 4.0F);
        this.foxNose.addBox(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 3.0F);

        this.wolfNose = new ModelRenderer(textureWidth, textureHeight, 46, 25);
        this.wolfNose.setPos(-1.5F, 3.9F, 4.0F);
        this.wolfNose.addBox(0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 3.0F);
    }

    public void render(ITravelersBackpackInventory inv, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        boolean isColorable = false;
        ResourceLocation loc = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());

        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(loc));

        if(inv.hasTileEntity() ? inv.hasColor() : inv.getItemStack().getTag() != null)
        {
            if((inv.hasTileEntity() || BackpackDyeRecipe.hasColor(inv.getItemStack())) && inv.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
            {
                isColorable = true;
                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(inv.hasTileEntity() ? inv.getColor() : BackpackDyeRecipe.getColor(inv.getItemStack()));
            ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.tankLeftTop.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            if(!inv.isSleepingBagDeployed())
            {
                this.bed.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }
        }
        else
        {
            this.tankLeftTop.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);

            if(!inv.isSleepingBagDeployed())
            {
                this.bed.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(inv.getItemStack().getItem() == ModItems.FOX_TRAVELERS_BACKPACK.get())
            {
                this.foxNose.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(inv.getItemStack().getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get())
            {
                this.ocelotNose.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(inv.getItemStack().getItem() == ModItems.WOLF_TRAVELERS_BACKPACK.get())
            {
                this.wolfNose.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(inv.getItemStack().getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || inv.getItemStack().getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
            {
                this.villagerNose.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(inv.getItemStack().getItem() == ModItems.PIG_TRAVELERS_BACKPACK.get() || inv.getItemStack().getItem() == ModItems.HORSE_TRAVELERS_BACKPACK.get())
            {
                this.pigNose.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get()) //Do the same for Slime and Snow (Icey) Backpack
            {
                ivertexbuilder = bufferIn.getBuffer(inv.hasTileEntity() ? RenderType.entityTranslucentCull(ResourceUtils.getBackpackTexture(inv.getItemStack().getItem())) : RenderType.itemEntityTranslucentCull(ResourceUtils.getBackpackTexture(inv.getItemStack().getItem())));
            }

            this.mainBody.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }
        RenderUtils.renderFluidInTank(inv, inv.getLeftTank(), matrixStackIn, bufferIn, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(inv, inv.getRightTank(), matrixStackIn, bufferIn, combinedLightIn, 0.23F, -0.565F, -0.24F);
    }
}