package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.client.renderer.FluidPart;
import com.tiviacz.travelersbackpack.client.renderer.StackPart;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class TravelersBackpackWearableModel<T extends LivingEntity> extends BipedEntityModel<T>
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

    public StackPart stacks;
    public FluidPart fluids;

    private final LivingEntity livingEntity;
    private final VertexConsumerProvider vertices;

    public TravelersBackpackWearableModel(LivingEntity livingEntity, VertexConsumerProvider vertices, ModelPart rootPart)
    {
        super(rootPart);
        this.livingEntity = livingEntity;
        this.vertices = vertices;

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

        if(this.livingEntity instanceof PlayerEntity player)
        {
            this.stacks = new StackPart(rootPart.getChild("body").getChild("stacks"), player, vertices);
            this.fluids = new FluidPart(rootPart.getChild("body").getChild("fluids"), player, vertices);
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
    {
        if(alpha == 0.25F)
        {
            this.sleepingBag.render(matrices, vertices, light, overlay, red, green, blue, 1.0F);
        }
        else
        {
            this.sleepingBag.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.sleepingBagExtras.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.tankLeftTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.tankRightTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.mainBody.render(matrices, vertices, light, overlay, red, green, blue, alpha);

            if(this.livingEntity != null)
            {
                Item item = this.livingEntity instanceof PlayerEntity ? ComponentUtils.getWearingBackpack((PlayerEntity)this.livingEntity).getItem() : ComponentUtils.getWearingBackpack(this.livingEntity).getItem();

                if(item == ModItems.FOX_TRAVELERS_BACKPACK)
                {
                    this.foxNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.WOLF_TRAVELERS_BACKPACK)
                {
                    this.wolfNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK)
                {
                    this.villagerNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.OCELOT_TRAVELERS_BACKPACK)
                {
                    this.ocelotNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.PIG_TRAVELERS_BACKPACK || item == ModItems.HORSE_TRAVELERS_BACKPACK)
                {
                    this.pigNose.render(matrices, vertices, light, overlay);
                }

                //Make nose for irongolem villager
                //Make nose for pig and horse
                //Make nose for ocelot
            }

            if(this.livingEntity instanceof PlayerEntity)
            {
                if(TravelersBackpackConfig.renderTools)
                {
                    this.stacks.render(matrices, vertices, light, overlay);
                }
                this.fluids.render(matrices, vertices, light, overlay);
            }
        }
    }

    public void setupAngles(BipedEntityModel<T> model)
    {
        //Backpack
        this.mainBody.copyTransform(model.body);
        this.sleepingBag.copyTransform(model.body);
        this.sleepingBagExtras.copyTransform(model.body);
        this.tankLeftTop.copyTransform(model.body);
        this.tankRightTop.copyTransform(model.body);

        //Noses
        this.villagerNose.copyTransform(model.body);
        this.pigNose.copyTransform(model.body);
        this.ocelotNose.copyTransform(model.body);
        this.wolfNose.copyTransform(model.body);
        this.foxNose.copyTransform(model.body);

        if(this.livingEntity instanceof PlayerEntity)
        {
            //Extras
            this.stacks.copyTransform(model.body);
            this.fluids.copyTransform(model.body);
        }
    }
}