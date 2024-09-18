package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.RenderData;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.Locale;

public class BackpackBlockModel {
    public static final BackpackBlockModel BLOCK_MODEL = new BackpackBlockModel(BackpackModelData.createTravelersBackpack(false).createModel());

    public ModelPart mainBody;
    public ModelPart tankLeftTop;
    public ModelPart tankRightTop;
    public ModelPart sleepingBag;
    public ModelPart sleepingBagExtras;

    public ModelPart villagerNose;
    public ModelPart wolfNose;
    public ModelPart foxNose;
    public ModelPart ocelotNose;
    public ModelPart pigNose;

    public BackpackBlockModel(ModelPart rootPart) {
        //Main Backpack

        this.mainBody = rootPart.getChild("main_body");

        //Left Tank

        this.tankLeftTop = rootPart.getChild("tankLeftTop");

        //Right Tank
        this.tankRightTop = rootPart.getChild("tankRightTop");

        //Sleeping Bag

        this.sleepingBag = rootPart.getChild("sleepingBag");
        this.sleepingBagExtras = rootPart.getChild("sleepingBagExtras");

        //Noses, Additions

        this.villagerNose = rootPart.getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("ocelotNose");
        this.pigNose = rootPart.getChild("pigNose");
        this.foxNose = rootPart.getChild("foxNose");
        this.wolfNose = rootPart.getChild("wolfNose");
    }

    public void render(ITravelersBackpackInventory inv, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay) {
        if (!(inv.getItemStack().getItem() instanceof TravelersBackpackItem item)) return;

        Identifier id = item.getBackpackTexture();
        VertexConsumer vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));

        if (inv.hasTileEntity() ? inv.hasColor() : inv.getItemStack().contains(DataComponentTypes.DYED_COLOR)) {
            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay, inv.hasTileEntity() ? ColorHelper.Argb.fullAlpha(inv.getColor()) : ColorHelper.Argb.fullAlpha(inv.getItemStack().get(DataComponentTypes.DYED_COLOR).rgb()));

            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay);
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);

            if (!inv.isSleepingBagDeployed()) {
                this.sleepingBagExtras.render(matrices, vertexConsumer, light, overlay);

                id = getSleepingBagTexture(inv.getSleepingBagColor());
                vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
                this.sleepingBag.render(matrices, vertexConsumer, light, overlay);
            }
        } else {
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);

            if (!inv.isSleepingBagDeployed()) {
                this.sleepingBagExtras.render(matrices, vertexConsumer, light, overlay);

                id = getSleepingBagTexture(inv.getSleepingBagColor());
                vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
                this.sleepingBag.render(matrices, vertexConsumer, light, overlay);
                id = item.getBackpackTexture();
                vertexConsumer = vertices.getBuffer(RenderLayer.getEntityTranslucent(id));
            }

            if (item == ModItems.FOX_TRAVELERS_BACKPACK) {
                this.foxNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (item == ModItems.OCELOT_TRAVELERS_BACKPACK) {
                this.ocelotNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (item == ModItems.WOLF_TRAVELERS_BACKPACK) {
                this.wolfNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (item == ModItems.VILLAGER_TRAVELERS_BACKPACK || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK) {
                this.villagerNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (item == ModItems.PIG_TRAVELERS_BACKPACK || item == ModItems.HORSE_TRAVELERS_BACKPACK) {
                this.pigNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (item == ModItems.QUARTZ_TRAVELERS_BACKPACK || item == ModItems.SNOW_TRAVELERS_BACKPACK) { //Do the same for Slime and Snow (Icey) Backpack
                vertexConsumer = vertices.getBuffer(inv.hasTileEntity() ? RenderLayer.getEntityTranslucentCull(item.getBackpackTexture()) : RenderLayer.getItemEntityTranslucentCull(item.getBackpackTexture()));
            }

            this.mainBody.render(matrices, vertexConsumer, light, overlay);
        }
        RenderUtils.renderFluidInTank(inv.getLeftTank(), matrices, vertices, light, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(inv.getRightTank(), matrices, vertices, light, 0.23F, -0.565F, -0.24F);
    }

    public void renderByItem(RenderData renderData, MatrixStack matrices, VertexConsumerProvider consumer, int light, int overlay) {
        TravelersBackpackItem item = (TravelersBackpackItem)renderData.getItemStack().getItem();

        Identifier id = item.getBackpackTexture();
        VertexConsumer vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));

        if (renderData.getItemStack().contains(DataComponentTypes.DYED_COLOR)) {
            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay, ColorHelper.Argb.fullAlpha(renderData.getItemStack().get(DataComponentTypes.DYED_COLOR).rgb()));

            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.mainBody.render(matrices, vertexConsumer, light, overlay);
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);
            this.sleepingBagExtras.render(matrices, vertexConsumer, light, overlay);

            id = getSleepingBagTexture(renderData.getSleepingBagColor());
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.sleepingBag.render(matrices, vertexConsumer, light, overlay);
        } else {
            this.tankLeftTop.render(matrices, vertexConsumer, light, overlay);
            this.tankRightTop.render(matrices, vertexConsumer, light, overlay);
            this.sleepingBagExtras.render(matrices, vertexConsumer, light, overlay);

            id = getSleepingBagTexture(renderData.getSleepingBagColor());
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));
            this.sleepingBag.render(matrices, vertexConsumer, light, overlay);
            id = item.getBackpackTexture();
            vertexConsumer = consumer.getBuffer(RenderLayer.getEntityTranslucent(id));

            if (renderData.getItemStack().getItem() == ModItems.FOX_TRAVELERS_BACKPACK) {
                this.foxNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (renderData.getItemStack().getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK) {
                this.ocelotNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (renderData.getItemStack().getItem() == ModItems.WOLF_TRAVELERS_BACKPACK) {
                this.wolfNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (renderData.getItemStack().getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK || renderData.getItemStack().getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK) {
                this.villagerNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (renderData.getItemStack().getItem() == ModItems.PIG_TRAVELERS_BACKPACK || renderData.getItemStack().getItem() == ModItems.HORSE_TRAVELERS_BACKPACK) {
                this.pigNose.render(matrices, vertexConsumer, light, overlay);
            }

            if (renderData.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || renderData.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK) { //Do the same for Slime and Snow (Icey) Backpack
                vertexConsumer = consumer.getBuffer(RenderLayer.getItemEntityTranslucentCull(item.getBackpackTexture()));
            }

            this.mainBody.render(matrices, vertexConsumer, light, overlay);

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }
        RenderUtils.renderFluidInTank(renderData.getLeftTank(), matrices, consumer, light, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(renderData.getRightTank(), matrices, consumer, light, 0.23F, -0.565F, -0.24F);
    }

    public static Identifier getSleepingBagTexture(int color) {
        return Identifier.of(TravelersBackpack.MODID, "textures/model/bags/" + DyeColor.byId(color).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag" + ".png");
    }
}