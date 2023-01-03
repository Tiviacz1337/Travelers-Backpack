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

    public StackPart stacks;
    public FluidPart fluids;

    private final PlayerEntity player;

    public TravelersBackpackWearableModel(PlayerEntity player, VertexConsumerProvider provider)
    {
        super(0.0F);
        this.player = player;
        this.textureWidth = 64;
        this.textureHeight = 64;

        //Main Backpack

        this.mainBody = new ModelPart(this, 0, 9);
        this.mainBody.addCuboid(-5.0F, 0.0F, -3.0F, 10, 9, 5);
        this.mainBody.setPivot(0.0F, 0.0F, 0.0F);

        this.leftStrap = new ModelPart(this, 21, 24);
        this.leftStrap.setPivot(3.0F, 0.0F, -3.0F);
        this.leftStrap.addCuboid(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.leftStrap);

        this.rightStrap = new ModelPart(this, 26, 24);
        this.rightStrap.setPivot(-4.0F, 0.0F, -3.0F);
        this.rightStrap.addCuboid(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.rightStrap);

        this.top = new ModelPart(this, 0, 0);
        this.top.setPivot(0.0F, 0.0F, -3.0F);
        this.top.addCuboid(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        this.mainBody.addChild(this.top);

        this.bottom = new ModelPart(this, 0, 34);
        this.bottom.setPivot(-5.0F, 9.0F, -3.0F);
        this.bottom.addCuboid(0.0F, 0.0F, 0.0F, 10, 1, 4);
        this.mainBody.addChild(this.bottom);

        this.pocketFace = new ModelPart(this, 0, 24);
        this.pocketFace.setPivot(0.0F, 6.9F, 2.0F);
        this.pocketFace.addCuboid(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        this.mainBody.addChild(this.pocketFace);

        //Left Tank

        this.tankLeftTop = new ModelPart(this, 0, 40);
        this.tankLeftTop.setPivot(0.0F, 0.0F, 0.0F);
        this.tankLeftTop.addCuboid(5.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F);

        this.tankLeftBottom = new ModelPart(this, 0, 46);
        this.tankLeftBottom.setPivot(5.0F, 9.0F, -2.5F);
        this.tankLeftBottom.addCuboid(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F);
        this.tankLeftTop.addChild(this.tankLeftBottom);

        this.tankLeftWall1 = new ModelPart(this, 0, 52);
        this.tankLeftWall1.setPivot(3.0F, -8.0F, 0.0F);
        this.tankLeftWall1.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall1);

        this.tankLeftWall2 = new ModelPart(this, 5, 52);
        this.tankLeftWall2.setPivot(0.0F, -8.0F, 0.0F);
        this.tankLeftWall2.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall2);

        this.tankLeftWall3 = new ModelPart(this, 10, 52);
        this.tankLeftWall3.setPivot(0.0F, -8.0F, 3.0F);
        this.tankLeftWall3.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall3);

        this.tankLeftWall4 = new ModelPart(this, 15, 52);
        this.tankLeftWall4.setPivot(3.0F, -8.0F, 3.0F);
        this.tankLeftWall4.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankLeftBottom.addChild(this.tankLeftWall4);

        //Right Tank

        this.tankRightTop = new ModelPart(this, 17, 40);
        this.tankRightTop.setPivot(0.0F, 0.0F, 0.0F);
        this.tankRightTop.addCuboid(-9.0F, 0.0F, -2.5F, 4.0F, 1.0F, 4.0F);

        this.tankRightBottom = new ModelPart(this, 17, 46);
        this.tankRightBottom.setPivot(-9.0F, 9.0F, -2.5F);
        this.tankRightBottom.addCuboid(0.0F, 0.0F, 0.0F, 4.0F, 1.0F, 4.0F);
        this.tankRightTop.addChild(this.tankRightBottom);

        this.tankRightWall1 = new ModelPart(this, 22, 52);
        this.tankRightWall1.setPivot(3.0F, -8.0F, 3.0F);
        this.tankRightWall1.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall1);

        this.tankRightWall2 = new ModelPart(this, 27, 52);
        this.tankRightWall2.setPivot(3.0F, -8.0F, 0.0F);
        this.tankRightWall2.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall2);

        this.tankRightWall3 = new ModelPart(this, 32, 52);
        this.tankRightWall3.setPivot(0.0F, -8.0F, 3.0F);
        this.tankRightWall3.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall3);

        this.tankRightWall4 = new ModelPart(this, 37, 52);
        this.tankRightWall4.setPivot(0.0F, -8.0F, 0.0F);
        this.tankRightWall4.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F);
        this.tankRightBottom.addChild(this.tankRightWall4);

        //Bed

        this.bed = new ModelPart(this, 31, 0);
        this.bed.setPivot(-7.0F, 7.0F, 2.0F);
        this.bed.addCuboid(-7.0F, 7.0F, 2.0F, 14F, 2F, 2F);

        this.bedExtras = new ModelPart(this, 64, 64);
        this.bedExtras.setPivot(-7.0F, 7.0F, 2.0F);
        this.bedExtras.addCuboid(-7.0F, 7.0F, 2.0F, 0, 0, 0);

        this.bedStrapRightTop = new ModelPart(this, 40, 5);
        this.bedStrapRightTop.setPivot(-5.0F, 6.0F, 2.0F);
        this.bedStrapRightTop.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F);
        this.bedExtras.addChild(bedStrapRightTop);

        this.bedStrapRightMid = new ModelPart(this, 38, 10);
        this.bedStrapRightMid.setPivot(-5.0F, 7.0F, 4.0F);
        this.bedStrapRightMid.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bedExtras.addChild(this.bedStrapRightMid);

        this.bedStrapRightBottom = new ModelPart(this, 42, 15);
        this.bedStrapRightBottom.setPivot(-5.0F, 9.0F, 1.0F);
        this.bedStrapRightBottom.addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 3.0F);
        this.bedExtras.addChild(this.bedStrapRightBottom);

        this.bedStrapLeftTop = new ModelPart(this, 31, 5);
        this.bedStrapLeftTop.setPivot(4.0F, 6.0F, 2.0F);
        this.bedStrapLeftTop.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F);
        this.bedExtras.addChild(this.bedStrapLeftTop);

        this.bedStrapLeftMid = new ModelPart(this, 31, 10);
        this.bedStrapLeftMid.setPivot(3.0F, 7.0F, 4.0F);
        this.bedStrapLeftMid.addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 3.0F, 1.0F);
        this.bedExtras.addChild(this.bedStrapLeftMid);

        this.bedStrapLeftBottom = new ModelPart(this, 31, 15);
        this.bedStrapLeftBottom.setPivot(3.0F, 9.0F, 1.0F);
        this.bedStrapLeftBottom.addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 3.0F, 0.0F);
        this.bedExtras.addChild(this.bedStrapLeftBottom);

        //Noses

        this.villagerNose = new ModelPart(this, 31, 20);
        this.villagerNose.setPivot(0.0F, 0.0F, 0.0F);
        this.villagerNose.addCuboid(-1.0F, 4.0F, 4.0F, 2.0F, 4.0F, 2.0F);

        this.ocelotNose = new ModelPart(this, 42, 20);
        this.ocelotNose.setPivot(0.0F, 0.0F, 0.0F);
        this.ocelotNose.addCuboid(-1.0F, 4.0F, 4.0F, 3.0F, 2.0F, 1.0F);

        this.pigNose = new ModelPart(this, 42, 20);
        this.pigNose.setPivot(0.0F, 0.0F, 0.0F);
        this.pigNose.addCuboid(-2.0F, 4.0F, 4.0F, 4.0F, 3.0F, 1.0F);

        this.foxNose = new ModelPart(this, 31, 27);
        this.foxNose.setPivot(0.0F, 0.0F, 0.0F);
        this.foxNose.addCuboid(-2.0F, 4.9F, 4.0F, 4.0F, 2.0F, 3.0F);

        this.wolfNose = new ModelPart(this, 46, 25);
        this.wolfNose.setPivot(0.0F, 0.0F, 0.0F);
        this.wolfNose.addCuboid(-1.5F, 3.9F, 4.0F, 3.0F, 3.0F, 3.0F);

        //Extras

        this.stacks = new StackPart(this, player, provider);
        this.fluids = new FluidPart(this, player, provider);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
    {
        if(alpha == 0.25F)
        {
            this.bed.render(matrices, vertices, light, overlay, red, green, blue, 1.0F);
        }
        else
        {
            this.bed.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.bedExtras.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.tankLeftTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.tankRightTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.mainBody.render(matrices, vertices, light, overlay, red, green, blue, alpha);

            if(this.player != null)
            {
                Item item = ComponentUtils.getWearingBackpack(player).getItem();

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

            if(TravelersBackpackConfig.renderTools)
            {
                this.stacks.render(matrices, vertices, light, overlay);
            }
            this.fluids.render(matrices, vertices, light, overlay);
        }
    }

    public void setupAngles(BipedEntityModel<T> model)
    {
        //Backpack
        this.mainBody.copyTransform(model.body);
        this.bed.copyTransform(model.body);
        this.bedExtras.copyTransform(model.body);
        this.tankLeftTop.copyTransform(model.body);
        this.tankRightTop.copyTransform(model.body);

        //Noses
        this.villagerNose.copyTransform(model.body);
        this.pigNose.copyTransform(model.body);
        this.ocelotNose.copyTransform(model.body);
        this.wolfNose.copyTransform(model.body);
        this.foxNose.copyTransform(model.body);

        //Extras
        this.stacks.copyTransform(model.body);
        this.fluids.copyTransform(model.body);
    }
}