package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.RenderData;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;

public class BackpackBlockModel {
    public static final BackpackBlockModel BLOCK_MODEL = new BackpackBlockModel(BackpackModelData.createTravelersBackpack(false).bakeRoot());

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
        this.tankLeftTop = rootPart.getChild("tankLeftTop");
        this.tankRightTop = rootPart.getChild("tankRightTop");
        this.sleepingBag = rootPart.getChild("sleepingBag");
        this.sleepingBagExtras = rootPart.getChild("sleepingBagExtras");

        //Noses, Additions

        this.villagerNose = rootPart.getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("ocelotNose");
        this.pigNose = rootPart.getChild("pigNose");
        this.foxNose = rootPart.getChild("foxNose");
        this.wolfNose = rootPart.getChild("wolfNose");
    }

    public void render(ITravelersBackpackContainer container, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (!(container.getItemStack().getItem() instanceof TravelersBackpackItem item)) return;

        ResourceLocation loc = item.getBackpackTexture();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

        if (container.hasBlockEntity() ? container.hasColor() : container.getItemStack().has(DataComponents.DYED_COLOR)) {
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, container.hasBlockEntity() ? FastColor.ARGB32.opaque(container.getColor()) : FastColor.ARGB32.opaque(container.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            if (!container.isSleepingBagDeployed()) {
                this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

                loc = getSleepingBagTexture(container.getSleepingBagColor());
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
                this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }
        } else {
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            if (!container.isSleepingBagDeployed()) {
                this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

                loc = getSleepingBagTexture(container.getSleepingBagColor());
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
                this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                loc = item.getBackpackTexture();
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            }

            if (item == ModItems.FOX_TRAVELERS_BACKPACK.get()) {
                this.foxNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (item == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
                this.ocelotNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (item == ModItems.WOLF_TRAVELERS_BACKPACK.get()) {
                this.wolfNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (item == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (item == ModItems.PIG_TRAVELERS_BACKPACK.get() || item == ModItems.HORSE_TRAVELERS_BACKPACK.get()) {
                this.pigNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (item == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || item == ModItems.SNOW_TRAVELERS_BACKPACK.get()) { //Do the same for Slime and Snow (Icey) Backpack
                vertexConsumer = buffer.getBuffer(container.hasBlockEntity() ? RenderType.entityTranslucentCull(item.getBackpackTexture()) : RenderType.itemEntityTranslucentCull(item.getBackpackTexture()));
            }

            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }
        RenderUtils.renderFluidInTank(container, container.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(container, container.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);
    }

    public void renderByItem(RenderData renderData, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        TravelersBackpackItem item = (TravelersBackpackItem)renderData.getItemStack().getItem();

        ResourceLocation loc = item.getBackpackTexture();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

        if (renderData.getItemStack().has(DataComponents.DYED_COLOR)) {
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, FastColor.ARGB32.opaque(renderData.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            loc = getSleepingBagTexture(renderData.getSleepingBagColor());
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        } else {
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            loc = getSleepingBagTexture(renderData.getSleepingBagColor());
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            loc = item.getBackpackTexture();
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

            if (renderData.getItemStack().getItem() == ModItems.FOX_TRAVELERS_BACKPACK.get()) {
                this.foxNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (renderData.getItemStack().getItem() == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
                this.ocelotNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (renderData.getItemStack().getItem() == ModItems.WOLF_TRAVELERS_BACKPACK.get()) {
                this.wolfNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (renderData.getItemStack().getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || renderData.getItemStack().getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (renderData.getItemStack().getItem() == ModItems.PIG_TRAVELERS_BACKPACK.get() || renderData.getItemStack().getItem() == ModItems.HORSE_TRAVELERS_BACKPACK.get()) {
                this.pigNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if (renderData.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || renderData.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get()) { //Do the same for Slime and Snow (Icey) Backpack
                vertexConsumer = buffer.getBuffer(RenderType.itemEntityTranslucentCull(item.getBackpackTexture()));
            }

            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }
        RenderUtils.renderFluidInTank(null, renderData.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(null, renderData.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);
    }

/*    public void render(ITravelersBackpackContainer container, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
    {
        if(!(container.getItemStack().getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        boolean isColorable = false;
        ResourceLocation loc = travelersBackpackItem.getBackpackTexture(); //ResourceUtils.getBackpackTexture(container.getItemStack().getItem());

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

        if(container.hasBlockEntity() ? container.hasColor() : container.getItemStack().has(DataComponents.DYED_COLOR))
        {
            if((container.hasBlockEntity() || container.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get()))
            {
                isColorable = true;
                loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(isColorable)
        {
            this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            //Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(container.hasBlockEntity() ? container.getColor() : container.getItemStack().get(DataComponents.DYED_COLOR).rgb());
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, container.hasBlockEntity() ? FastColor.ARGB32.opaque(container.getColor()) : FastColor.ARGB32.opaque(container.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
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
                loc = travelersBackpackItem.getBackpackTexture();
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
                vertexConsumer = buffer.getBuffer(container.hasBlockEntity() ? RenderType.entityTranslucentCull(travelersBackpackItem.getBackpackTexture()) : RenderType.itemEntityTranslucentCull(travelersBackpackItem.getBackpackTexture()));
            }

            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        RenderUtils.renderFluidInTank(container, container.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(container, container.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);

        //For iron golem and villager add villager nose
        //For pig and horse add pig nose
        //For ocelot add ocelot nose
    } */

 /*   public void renderByItem(RenderData renderData, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
    {
        TravelersBackpackItem travelersBackpackItem = (TravelersBackpackItem)renderData.getItemStack().getItem();

        boolean isColorable = false;
        ResourceLocation loc = travelersBackpackItem.getBackpackTexture();

        VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));

        if(renderData.getItemStack().has(DataComponents.DYED_COLOR) && renderData.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
        {
            isColorable = true;
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
        }

        if(isColorable)
        {
            //Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(renderData.getItemStack().get(DataComponents.DYED_COLOR).rgb());
            ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn, FastColor.ARGB32.opaque(renderData.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
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
            loc = travelersBackpackItem.getBackpackTexture();
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
                ivertexbuilder = buffer.getBuffer(RenderType.itemEntityTranslucentCull(travelersBackpackItem.getBackpackTexture()));
            }

            this.mainBody.render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);

            //For iron golem and villager add villager nose
            //For pig and horse add pig nose
            //For ocelot add ocelot nose
        }

        RenderUtils.renderFluidInTank(null, renderData.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
        RenderUtils.renderFluidInTank(null, renderData.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);
    } */

    public static ResourceLocation getSleepingBagTexture(int color) {
        return ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/bags/" + DyeColor.byId(color).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag" + ".png");
    }
}