package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.RenderData;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

public class TravelersBackpackBlockModel
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

    public TravelersBackpackBlockModel(ModelPart rootPart)
    {
        //Main Backpack

        this.mainBody = rootPart.getChild("main_body");
        this.top = this.mainBody.getChild("top");
        this.bottom = this.mainBody.getChild("bottom");
        this.pocketFace = this.mainBody.getChild("pocketFace");
        this.leftStrap = this.mainBody.getChild("leftStrap");
        this.rightStrap = this.mainBody.getChild("rightStrap");

        //Left Tank

        this.tankLeftTop = rootPart.getChild("tankLeftTop");
        this.tankLeftBottom = this.tankLeftTop.getChild("tankLeftBottom");;

        this.tankLeftWall1 = this.tankLeftBottom.getChild("tankLeftWall1");
        this.tankLeftWall2 = this.tankLeftBottom.getChild("tankLeftWall2");
        this.tankLeftWall3 = this.tankLeftBottom.getChild("tankLeftWall3");
        this.tankLeftWall4 = this.tankLeftBottom.getChild("tankLeftWall4");

        //Right Tank
        this.tankRightTop = rootPart.getChild("tankRightTop");
        this.tankRightBottom = this.tankRightTop.getChild("tankRightBottom");

        this.tankRightWall1 = this.tankRightBottom.getChild("tankRightWall1");
        this.tankRightWall2 = this.tankRightBottom.getChild("tankRightWall2");
        this.tankRightWall3 = this.tankRightBottom.getChild("tankRightWall3");
        this.tankRightWall4 = this.tankRightBottom.getChild("tankRightWall4");

        //Sleeping Bag

        this.sleepingBag = rootPart.getChild("sleepingBag");
        this.sleepingBagExtras = rootPart.getChild("sleepingBagExtras");
        this.sleepingBagStrapLeftTop = this.sleepingBagExtras.getChild("sleepingBagStrapLeftTop");
        this.sleepingBagStrapLeftMid = this.sleepingBagExtras.getChild("sleepingBagStrapLeftMid");
        this.sleepingBagStrapLeftBottom = this.sleepingBagExtras.getChild("sleepingBagStrapLeftBottom");
        this.sleepingBagStrapRightTop = this.sleepingBagExtras.getChild("sleepingBagStrapRightTop");
        this.sleepingBagStrapRightMid = this.sleepingBagExtras.getChild("sleepingBagStrapRightMid");
        this.sleepingBagStrapRightBottom = this.sleepingBagExtras.getChild("sleepingBagStrapRightBottom");

        //Noses, Additions

        this.villagerNose = rootPart.getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("ocelotNose");
        this.pigNose = rootPart.getChild("pigNose");
        this.foxNose = rootPart.getChild("foxNose");
        this.wolfNose = rootPart.getChild("wolfNose");
    }

    public void render(ITravelersBackpackContainer container, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
    {
        boolean isColorable = false;
        ResourceLocation loc = ResourceUtils.getBackpackTexture(container.getItemStack().getItem());

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

        if(container.hasBlockEntity() ? container.hasColor() : container.getItemStack().getTag() != null)
        {
            if((container.hasBlockEntity() || BackpackDyeRecipe.hasColor(container.getItemStack())) && container.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
            {
                isColorable = true;
                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(isColorable)
        {
            this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(container.hasBlockEntity() ? container.getColor() : BackpackDyeRecipe.getColor(container.getItemStack()));
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            if(!container.isSleepingBagDeployed())
            {
                this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

                loc = ResourceUtils.getSleepingBagTexture(container.getSleepingBagColor());
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
                this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }
        }
        else
        {
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            if(!container.isSleepingBagDeployed())
            {
                this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

                loc = ResourceUtils.getSleepingBagTexture(container.getSleepingBagColor());
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
                this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                loc = ResourceUtils.getBackpackTexture(container.getItemStack().getItem());
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            }

            if(container.getItemStack().getItem() == ModItems.FOX_TRAVELERS_BACKPACK.get())
            {
                this.foxNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(container.getItemStack().getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get())
            {
                this.ocelotNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(container.getItemStack().getItem() == ModItems.WOLF_TRAVELERS_BACKPACK.get())
            {
                this.wolfNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(container.getItemStack().getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || container.getItemStack().getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
            {
                this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(container.getItemStack().getItem() == ModItems.PIG_TRAVELERS_BACKPACK.get() || container.getItemStack().getItem() == ModItems.HORSE_TRAVELERS_BACKPACK.get())
            {
                this.pigNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(container.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || container.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get()) //Do the same for Slime and Snow (Icey) Backpack
            {
                vertexConsumer = buffer.getBuffer(container.hasBlockEntity() ? RenderType.entityTranslucentCull(ResourceUtils.getBackpackTexture(container.getItemStack().getItem())) : RenderType.itemEntityTranslucentCull(ResourceUtils.getBackpackTexture(container.getItemStack().getItem())));
            }

            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        RenderUtils.renderFluidInTank(container, container.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(container, container.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);

        //For iron golem and villager add villager nose
        //For pig and horse add pig nose
        //For ocelot add ocelot nose
    }

    public void renderByItem(RenderData renderData, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
    {
        boolean isColorable = false;
        ResourceLocation loc = ResourceUtils.getBackpackTexture(renderData.getItemStack().getItem());

        VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));

        if(renderData.getItemStack().getTag() != null)
        {
            if(BackpackDyeRecipe.hasColor(renderData.getItemStack()) && renderData.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
            {
                isColorable = true;
                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(renderData.getItemStack()));
            ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.tankLeftTop.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.sleepingBagExtras.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);

            loc = ResourceUtils.getSleepingBagTexture(renderData.getSleepingBagColor());
            ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        else
        {
            this.tankLeftTop.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.sleepingBagExtras.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);

            loc = ResourceUtils.getSleepingBagTexture(renderData.getSleepingBagColor());
            ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            loc = ResourceUtils.getBackpackTexture(renderData.getItemStack().getItem());
            ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));

            if(renderData.getItemStack().getItem() == ModItems.FOX_TRAVELERS_BACKPACK.get())
            {
                this.foxNose.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(renderData.getItemStack().getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get())
            {
                this.ocelotNose.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(renderData.getItemStack().getItem() == ModItems.WOLF_TRAVELERS_BACKPACK.get())
            {
                this.wolfNose.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(renderData.getItemStack().getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || renderData.getItemStack().getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
            {
                this.villagerNose.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(renderData.getItemStack().getItem() == ModItems.PIG_TRAVELERS_BACKPACK.get() || renderData.getItemStack().getItem() == ModItems.HORSE_TRAVELERS_BACKPACK.get())
            {
                this.pigNose.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            }

            if(renderData.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || renderData.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get()) //Do the same for Slime and Snow (Icey) Backpack
            {
                ivertexbuilder = buffer.getBuffer(RenderType.itemEntityTranslucentCull(ResourceUtils.getBackpackTexture(renderData.getItemStack().getItem())));
            }

            this.mainBody.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }
        RenderUtils.renderFluidInTank(null, renderData.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(null, renderData.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);
    }
}