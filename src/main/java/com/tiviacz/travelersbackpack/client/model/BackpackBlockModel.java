package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.renderer.BackpackRenderInfo;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

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
    public ModelPart leftHorn;
    public ModelPart rightHorn;

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
        this.leftHorn = rootPart.getChild("leftHorn");
        this.rightHorn = rootPart.getChild("rightHorn");
    }

    public void render(BackpackBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        BackpackWrapper wrapper = blockEntity.getWrapper();
        if(!(wrapper.getBackpackStack().getItem() instanceof TravelersBackpackItem item)) return;

        ResourceLocation loc = item.getBackpackTexture();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
        boolean mainBodyRendered = false;

        if(item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && NbtHelper.has(wrapper.getBackpackStack(), ModDataHelper.COLOR)) {
            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            Triple<Float, Float, Float> rgb = RenderHelper.intToRGB(BackpackDyeRecipe.getColor(wrapper.getBackpackStack()));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            mainBodyRendered = true;
        }

        if(wrapper.tanksVisible()) {
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        if(!blockEntity.isSleepingBagDeployed()) {
            this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            loc = getSleepingBagTexture(wrapper.getSleepingBagColor());
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            loc = item.getBackpackTexture();
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
        }

        if(item == ModItems.FOX_TRAVELERS_BACKPACK.get()) {
            this.foxNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        if(item == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
            this.ocelotNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        if(item == ModItems.WOLF_TRAVELERS_BACKPACK.get()) {
            this.wolfNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
            this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        if(item == ModItems.PIG_TRAVELERS_BACKPACK.get() || item == ModItems.HORSE_TRAVELERS_BACKPACK.get()) {
            this.pigNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        if(item == ModItems.WARDEN_TRAVELERS_BACKPACK.get()) {
            vertexConsumer = buffer.getBuffer(RenderType.entityCutout(loc));
            this.leftHorn.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.rightHorn.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
        }

        if(item == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || item == ModItems.SNOW_TRAVELERS_BACKPACK.get()) { //Do the same for Slime and Snow (Icey) Backpack
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucentCull(item.getBackpackTexture()));
        }

        if(!mainBodyRendered) {
            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        }

        wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).ifPresent(tanks -> {
            RenderHelper.renderFluidInTank(tanks.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
            RenderHelper.renderFluidInTank(tanks.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);
        });
    }

    public void renderByItem(ItemStack backpack, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        TravelersBackpackItem item = (TravelersBackpackItem)backpack.getItem();
        ResourceLocation loc = item.getBackpackTexture();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
        boolean renderDefault = !NbtHelper.has(backpack, ModDataHelper.RENDER_INFO);

        if(renderDefault) {
            //Render Default model -> Tanks + Red Sleeping Bag
            this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            loc = getSleepingBagTexture(DyeColor.RED.getId());
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            loc = item.getBackpackTexture();
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

            if(item == ModItems.FOX_TRAVELERS_BACKPACK.get()) {
                this.foxNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
                this.ocelotNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.WOLF_TRAVELERS_BACKPACK.get()) {
                this.wolfNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.PIG_TRAVELERS_BACKPACK.get() || item == ModItems.HORSE_TRAVELERS_BACKPACK.get()) {
                this.pigNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.WARDEN_TRAVELERS_BACKPACK.get()) {
                vertexConsumer = buffer.getBuffer(RenderType.entityCutout(loc));
                this.leftHorn.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                this.rightHorn.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            }

            if(item == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || item == ModItems.SNOW_TRAVELERS_BACKPACK.get()) { //Do the same for Slime and Snow (Icey) Backpack
                vertexConsumer = buffer.getBuffer(RenderType.itemEntityTranslucentCull(item.getBackpackTexture()));
            }

            this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        } else {
            BackpackRenderInfo renderInfo = new BackpackRenderInfo(backpack, NbtHelper.get(backpack, ModDataHelper.RENDER_INFO));
            boolean backpackRendered = false;

            if(!renderInfo.isEmpty()) {
                this.tankLeftTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                this.tankRightTop.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                if(!renderInfo.getLeftTank().isEmpty()) {
                    RenderHelper.renderFluidInTank(renderInfo.getLeftTank(), poseStack, buffer, combinedLightIn, -0.65F, -0.565F, -0.24F);
                }
                if(!renderInfo.getRightTank().isEmpty()) {
                    RenderHelper.renderFluidInTank(renderInfo.getRightTank(), poseStack, buffer, combinedLightIn, 0.23F, -0.565F, -0.24F);
                }
            }

            loc = item.getBackpackTexture();
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBagExtras.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            loc = getSleepingBagTexture(renderInfo.getSleepingBagColor());
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            this.sleepingBag.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);

            if(renderInfo.isDyed()) {
                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
                Triple<Float, Float, Float> rgb = RenderHelper.intToRGB(BackpackDyeRecipe.getColor(renderInfo.getBackpack()));
                this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
                this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                backpackRendered = true;
            }

            loc = item.getBackpackTexture();
            vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));

            if(item == ModItems.FOX_TRAVELERS_BACKPACK.get()) {
                this.foxNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
                this.ocelotNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.WOLF_TRAVELERS_BACKPACK.get()) {
                this.wolfNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                this.villagerNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.PIG_TRAVELERS_BACKPACK.get() || item == ModItems.HORSE_TRAVELERS_BACKPACK.get()) {
                this.pigNose.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }

            if(item == ModItems.WARDEN_TRAVELERS_BACKPACK.get()) {
                vertexConsumer = buffer.getBuffer(RenderType.entityCutout(loc));
                this.leftHorn.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                this.rightHorn.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
                vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(loc));
            }

            if(item == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || item == ModItems.SNOW_TRAVELERS_BACKPACK.get()) { //Do the same for Slime and Snow (Icey) Backpack
                vertexConsumer = buffer.getBuffer(RenderType.itemEntityTranslucentCull(item.getBackpackTexture()));
            }

            if(!backpackRendered) {
                this.mainBody.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
            }
        }
    }

    public static ResourceLocation getSleepingBagTexture(int color) {
        return new ResourceLocation(TravelersBackpack.MODID, "textures/model/bags/" + DyeColor.byId(color).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag" + ".png");
    }
}