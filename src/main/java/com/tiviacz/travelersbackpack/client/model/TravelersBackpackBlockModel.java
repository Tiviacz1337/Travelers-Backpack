package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.RenderData;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Triple;

public class TravelersBackpackBlockModel
{
    public ModelPart mainBody;
    public ModelPart tankLeftTop;
    public ModelPart tankRightTop;
    public ModelPart bed;
    public ModelPart bedExtras;
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
    public ModelPart bedStrapLeftMid;
    public ModelPart bedStrapRightBottom;
    public ModelPart bedStrapLeftBottom;
    public ModelPart bedStrapRightMid;
    public ModelPart bedStrapRightTop;
    public ModelPart bedStrapLeftTop;

    public ModelPart villagerNose;
    public ModelPart wolfNose;
    public ModelPart foxNose;
    public ModelPart ocelotNose;
    public ModelPart pigNose;

    public TravelersBackpackBlockModel()
    {
        int textureWidth = 64;
        int textureHeight = 64;

        //Main Backpack

        this.mainBody = new ModelPart(textureWidth, textureHeight, 0, 9);
        this.mainBody.setPivot(0.0F, 0.0F, 0.0F);
        this.mainBody.addCuboid(-5.0F, 0.0F, -3.0F, 10, 9, 5);

        this.leftStrap = new ModelPart(textureWidth, textureHeight, 21, 24);
        this.leftStrap.setPivot(3.0F, 0.0F, -3.0F);
        this.leftStrap.addCuboid(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.leftStrap);

        this.rightStrap = new ModelPart(textureWidth, textureHeight, 26, 24);
        this.rightStrap.setPivot(-4.0F, 0.0F, -3.0F);
        this.rightStrap.addCuboid(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.rightStrap);

        this.top = new ModelPart(textureWidth, textureHeight, 0, 0);
        this.top.setPivot(0.0F, 0.0F, -3.0F);
        this.top.addCuboid(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        this.mainBody.addChild(this.top);

        this.bottom = new ModelPart(textureWidth, textureHeight, 0, 34);
        this.bottom.setPivot(-5.0F, 9.0F, -3.0F);
        this.bottom.addCuboid(0.0F, 0.0F, 0.0F, 10, 1, 4);
        this.mainBody.addChild(this.bottom);

        this.pocketFace = new ModelPart(textureWidth, textureHeight, 0, 24);
        this.pocketFace.setPivot(0.0F, 6.9F, 2.0F);
        this.pocketFace.addCuboid(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        this.mainBody.addChild(this.pocketFace);

        //Left Tank

        this.tankLeftTop = new ModelPart(textureWidth, textureHeight, 0, 40);
        this.tankLeftTop.setPivot(5.0F, 0.0F, -2.5F);
        this.tankLeftTop.addCuboid(0.0F, 0.0F, 0.0F, 4, 1, 4);

        this.tankLeftBottom = new ModelPart(textureWidth, textureHeight, 0, 46);
        this.tankLeftBottom.setPivot(0.0F, 9.0F, 0.0F);
        this.tankLeftBottom.addCuboid(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.tankLeftTop.addChild(this.tankLeftBottom);

        this.tankLeftWall1 = new ModelPart(textureWidth, textureHeight, 0, 52);
        this.tankLeftWall1.setPivot(3.0F, -8.0F, 0.0F);
        this.tankLeftWall1.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall1);

        this.tankLeftWall2 = new ModelPart(textureWidth, textureHeight, 5, 52);
        this.tankLeftWall2.setPivot(0.0F, -8.0F, 0.0F);
        this.tankLeftWall2.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall2);

        this.tankLeftWall3 = new ModelPart(textureWidth, textureHeight, 10, 52);
        this.tankLeftWall3.setPivot(0.0F, -8.0F, 3.0F);
        this.tankLeftWall3.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall3);

        this.tankLeftWall4 = new ModelPart(textureWidth, textureHeight, 15, 52);
        this.tankLeftWall4.setPivot(3.0F, -8.0F, 3.0F);
        this.tankLeftWall4.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall4);

        //Right Tank

        this.tankRightTop = new ModelPart(textureWidth, textureHeight, 17, 40);
        this.tankRightTop.setPivot(-9.0F, 0.0F, -2.5F);
        this.tankRightTop.addCuboid(0.0F, 0.0F, 0.0F, 4, 1, 4);

        this.tankRightBottom = new ModelPart(textureWidth, textureHeight, 17, 46);
        this.tankRightBottom.setPivot(0.0F, 9.0F, 0.0F);
        this.tankRightBottom.addCuboid(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.tankRightTop.addChild(this.tankRightBottom);

        this.tankRightWall1 = new ModelPart(textureWidth, textureHeight, 22, 52);
        this.tankRightWall1.setPivot(3.0F, -8.0F, 3.0F);
        this.tankRightWall1.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall1);

        this.tankRightWall2 = new ModelPart(textureWidth, textureHeight, 27, 52);
        this.tankRightWall2.setPivot(3.0F, -8.0F, 0.0F);
        this.tankRightWall2.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall2);

        this.tankRightWall3 = new ModelPart(textureWidth, textureHeight, 32, 52);
        this.tankRightWall3.setPivot(0.0F, -8.0F, 3.0F);
        this.tankRightWall3.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall3);

        this.tankRightWall4 = new ModelPart(textureWidth, textureHeight, 37, 52);
        this.tankRightWall4.setPivot(0.0F, -8.0F, 0.0F);
        this.tankRightWall4.addCuboid(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall4);

        //Bed

        this.bed = new ModelPart(textureWidth, textureHeight, 31, 0);
        this.bed.setPivot(-7.0F, 7.0F, 2.0F);
        this.bed.addCuboid(0.0F, 0.0F, 0.0F, 14, 2, 2);

        this.bedExtras = new ModelPart(textureWidth, textureHeight, 64, 64);
        this.bedExtras.setPivot(-7.0F, 7.0F, 2.0F);
        this.bedExtras.addCuboid(0.0F, 0.0F, 0.0F, 0, 0, 0);

        this.bedStrapRightTop = new ModelPart(textureWidth, textureHeight, 40, 5);
        this.bedStrapRightTop.setPivot(2.0F, -1.0F, 0.0F);
        this.bedStrapRightTop.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 3);
        this.bedExtras.addChild(this.bedStrapRightTop);

        this.bedStrapRightMid = new ModelPart(textureWidth, textureHeight, 38, 10);
        this.bedStrapRightMid.setPivot(2.0F, 0.0F, 2.0F);
        this.bedStrapRightMid.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bedExtras.addChild(this.bedStrapRightMid);

        this.bedStrapRightBottom = new ModelPart(textureWidth, textureHeight, 42, 15);
        this.bedStrapRightBottom.setPivot(2.0F, 2.0F, -1.0F);
        this.bedStrapRightBottom.addCuboid(0.0F, 0.0F, 0.0F, 2, 1, 3);
        this.bedExtras.addChild(this.bedStrapRightBottom);

        this.bedStrapLeftTop = new ModelPart(textureWidth, textureHeight, 31, 5);
        this.bedStrapLeftTop.setPivot(11.0F, -1.0F, 0.0F);
        this.bedStrapLeftTop.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 3);
        this.bedExtras.addChild(this.bedStrapLeftTop);

        this.bedStrapLeftMid = new ModelPart(textureWidth, textureHeight, 31, 10);
        this.bedStrapLeftMid.setPivot(10.0F, 0.0F, 2.0F);
        this.bedStrapLeftMid.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bedExtras.addChild(this.bedStrapLeftMid);

        this.bedStrapLeftBottom = new ModelPart(textureWidth, textureHeight, 31, 15);
        this.bedStrapLeftBottom.setPivot(10.0F, 2.0F, -1.0F);
        this.bedStrapLeftBottom.addCuboid(0.0F, 0.0F, 0.0F, 2, 1, 3);
        this.bedExtras.addChild(this.bedStrapLeftBottom);

        //Noses, Additions

        this.villagerNose = new ModelPart(textureWidth, textureHeight, 31, 20);
        this.villagerNose.setPivot(-1.0F, 4.0F, 4.0F);
        this.villagerNose.addCuboid(0.0F, 0.0F, 0.0F, 2, 4, 2);

        this.ocelotNose = new ModelPart(textureWidth, textureHeight, 42, 20);
        this.ocelotNose.setPivot(-1.0F, 3.9F, 4.0F);
        this.ocelotNose.addCuboid(0.0F, 0.0F, 0.0F, 3, 2, 1);

        this.pigNose = new ModelPart(textureWidth, textureHeight, 42, 20);
        this.pigNose.setPivot(-2.0F, 4.0F, 4.0F);
        this.pigNose.addCuboid(0.0F, 0.0F, 0.0F, 4, 3, 1);

        this.foxNose = new ModelPart(textureWidth, textureHeight, 31, 27);
        this.foxNose.setPivot(-2.0F, 4.9F, 4.0F);
        this.foxNose.addCuboid(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 3.0F);

        this.wolfNose = new ModelPart(textureWidth, textureHeight, 46, 25);
        this.wolfNose.setPivot(-1.5F, 3.9F, 4.0F);
        this.wolfNose.addCuboid(0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 3.0F);
    }

    public void render(ITravelersBackpackInventory inv, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay)
    {
        boolean isColorable = false;
        Item item = inv.getItemStack().getItem();
        Identifier id = ResourceUtils.getBackpackTexture(item);

        VertexConsumer vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));

        if(inv.hasTileEntity() ? inv.hasColor() : inv.getItemStack().getTag() != null)
        {
            if((inv.hasTileEntity() || BackpackDyeRecipe.hasColor(inv.getItemStack())) && item == ModItems.STANDARD_TRAVELERS_BACKPACK)
            {
                isColorable = true;
                id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(inv.hasTileEntity() ? inv.getColor() : BackpackDyeRecipe.getColor(inv.getItemStack()));
            vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay);
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);
            if(!inv.isSleepingBagDeployed())
            {
                this.bedExtras.render(matrices, vertexConsumer, light, overlay);

                id = ResourceUtils.getSleepingBagTexture(inv.getSleepingBagColor());
                vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
                this.bed.render(matrices, vertexConsumer, light, overlay);
            }
        }
        else
        {
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);

            if(!inv.isSleepingBagDeployed())
            {
                this.bedExtras.render(matrices, vertexConsumer, light, overlay);

                id = ResourceUtils.getSleepingBagTexture(inv.getSleepingBagColor());
                vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
                this.bed.render(matrices, vertexConsumer, light, overlay);
                id = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());
                vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
            }

            if(item == ModItems.FOX_TRAVELERS_BACKPACK)
            {
                this.foxNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(item == ModItems.OCELOT_TRAVELERS_BACKPACK)
            {
                this.ocelotNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(item == ModItems.WOLF_TRAVELERS_BACKPACK)
            {
                this.wolfNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK)
            {
                this.villagerNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(item == ModItems.PIG_TRAVELERS_BACKPACK || item == ModItems.HORSE_TRAVELERS_BACKPACK)
            {
                this.pigNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(item == ModItems.QUARTZ_TRAVELERS_BACKPACK || item == ModItems.SNOW_TRAVELERS_BACKPACK) //Do the same for Slime and Snow (Icey) Backpack
            {
                vertexConsumer = vertices.getBuffer(inv.hasTileEntity() ? RenderLayer.getEntityTranslucentCull(ResourceUtils.getBackpackTexture(item)) : RenderLayer.getItemEntityTranslucentCull(ResourceUtils.getBackpackTexture(item)));
            }

            this.mainBody.render(matrices, vertexConsumer, light, overlay);
        }
        RenderUtils.renderFluidInTank(inv.getLeftTank(), matrices, vertices, light, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(inv.getRightTank(), matrices, vertices, light, 0.23F, -0.565F, -0.24F);
    }

    public void renderByItem(RenderData renderData, MatrixStack matrices, VertexConsumerProvider consumer, int light, int overlay)
    {
        boolean isColorable = false;
        Identifier id = ResourceUtils.getBackpackTexture(renderData.getItemStack().getItem());

        VertexConsumer vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));

        if(renderData.getItemStack().getTag() != null)
        {
            if(BackpackDyeRecipe.hasColor(renderData.getItemStack()) && renderData.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
            {
                isColorable = true;
                id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(renderData.getItemStack()));
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay);
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);
            this.bedExtras.render(matrices, vertexConsumer, light, overlay);

            id = ResourceUtils.getSleepingBagTexture(renderData.getSleepingBagColor());
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.bed.render(matrices, vertexConsumer, light, overlay);
        }
        else
        {
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);
            this.bedExtras.render(matrices, vertexConsumer, light, overlay);

            id = ResourceUtils.getSleepingBagTexture(renderData.getSleepingBagColor());
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.bed.render(matrices, vertexConsumer, light, overlay);
            id = ResourceUtils.getBackpackTexture(renderData.getItemStack().getItem());
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));

            if(renderData.getItemStack().getItem() == ModItems.FOX_TRAVELERS_BACKPACK)
            {
                this.foxNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(renderData.getItemStack().getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK)
            {
                this.ocelotNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(renderData.getItemStack().getItem() == ModItems.WOLF_TRAVELERS_BACKPACK)
            {
                this.wolfNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(renderData.getItemStack().getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK || renderData.getItemStack().getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK)
            {
                this.villagerNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(renderData.getItemStack().getItem() == ModItems.PIG_TRAVELERS_BACKPACK || renderData.getItemStack().getItem() == ModItems.HORSE_TRAVELERS_BACKPACK)
            {
                this.pigNose.render(matrices, vertexConsumer, light, overlay);
            }

            if(renderData.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || renderData.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK) //Do the same for Slime and Snow (Icey) Backpack
            {
                vertexConsumer = consumer.getBuffer(RenderLayer.getItemEntityTranslucentCull(ResourceUtils.getBackpackTexture(renderData.getItemStack().getItem())));
            }

            this.mainBody.render(matrices, vertexConsumer, light, overlay);

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }
        RenderUtils.renderFluidInTank(renderData.getLeftTank(), matrices, consumer, light, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(renderData.getRightTank(), matrices, consumer, light, 0.23F, -0.565F, -0.24F);
    }
}