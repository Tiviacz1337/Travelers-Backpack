package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.TravelersBackpack;
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
    public ModelPart sleepingBag;
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
        this.sleepingBagStrapLeftTop = this.sleepingBag.getChild("sleepingBagStrapLeftTop");
        this.sleepingBagStrapLeftMid = this.sleepingBag.getChild("sleepingBagStrapLeftMid");
        this.sleepingBagStrapLeftBottom = this.sleepingBag.getChild("sleepingBagStrapLeftBottom");
        this.sleepingBagStrapRightTop = this.sleepingBag.getChild("sleepingBagStrapRightTop");
        this.sleepingBagStrapRightMid = this.sleepingBag.getChild("sleepingBagStrapRightMid");
        this.sleepingBagStrapRightBottom = this.sleepingBag.getChild("sleepingBagStrapRightBottom");

        //Noses, Additions

        this.villagerNose = rootPart.getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("ocelotNose");
        this.pigNose = rootPart.getChild("pigNose");
        this.foxNose = rootPart.getChild("foxNose");
        this.wolfNose = rootPart.getChild("wolfNose");
    }

    public void render(ITravelersBackpackInventory inv, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay)
    {
        boolean isColorable = false;
        Item item = inv.getItemStack().getItem();
        Identifier id = ResourceUtils.getBackpackTexture(item);

        VertexConsumer vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));

        if(inv.hasTileEntity() ? inv.hasColor() : inv.getItemStack().getNbt() != null)
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
                this.sleepingBag.render(matrices, vertexConsumer, light, overlay);
            }
        }
        else
        {
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);

            if(!inv.isSleepingBagDeployed())
            {
                this.sleepingBag.render(matrices, vertexConsumer, light, overlay);
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

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }
        RenderUtils.renderFluidInTank(inv, inv.getLeftTank(), matrices, vertices, light, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(inv, inv.getRightTank(), matrices, vertices, light, 0.23F, -0.565F, -0.24F);
    }
}
