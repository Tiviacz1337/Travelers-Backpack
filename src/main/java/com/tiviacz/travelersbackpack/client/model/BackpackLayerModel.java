package com.tiviacz.travelersbackpack.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackpackLayerModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final BackpackLayerModel<?> LAYER_MODEL = new BackpackLayerModel<>(BackpackModelData.createTravelersBackpack(true).bakeRoot());

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

    public StackModelPart stacks;
    public FluidModelPart fluids;
    public SupporterBadgeModel supporterBadgeModel;

    @Nullable
    private ItemStack backpackStack;
    @Nullable
    private MultiBufferSource buffer;

    public BackpackLayerModel(ModelPart rootPart) {
        super(rootPart);

        //Main Backpack
        this.mainBody = rootPart.getChild("body").getChild("main_body");
        this.tankLeftTop = rootPart.getChild("body").getChild("tankLeftTop");
        this.tankRightTop = rootPart.getChild("body").getChild("tankRightTop");
        this.sleepingBag = rootPart.getChild("body").getChild("sleepingBag");
        this.sleepingBagExtras = rootPart.getChild("body").getChild("sleepingBagExtras");

        //Noses, Additions
        this.villagerNose = rootPart.getChild("body").getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("body").getChild("ocelotNose");
        this.pigNose = rootPart.getChild("body").getChild("pigNose");
        this.foxNose = rootPart.getChild("body").getChild("foxNose");
        this.wolfNose = rootPart.getChild("body").getChild("wolfNose");
        this.leftHorn = rootPart.getChild("body").getChild("leftHorn");
        this.rightHorn = rootPart.getChild("body").getChild("rightHorn");

        //Extras
        this.stacks = new StackModelPart(rootPart.getChild("body").getChild("stacks"));
        this.fluids = new FluidModelPart(rootPart.getChild("body").getChild("fluids"));
        this.supporterBadgeModel = new SupporterBadgeModel();
    }

    public void setMultiBufferSource(MultiBufferSource buffer) {
        this.buffer = buffer;
    }

    public void setBackpackStack(ItemStack stack) {
        this.backpackStack = stack;
    }

    public ItemStack getBackpackStack() {
        if(this.backpackStack != null && this.backpackStack.getItem() instanceof TravelersBackpackItem) {
            return this.backpackStack;
        }
        return new ItemStack(Items.AIR);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, int pColor) {
        this.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, pColor);
        this.sleepingBagExtras.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, pColor);
        if(!getBackpackStack().getOrDefault(ModDataComponents.RENDER_INFO.get(), RenderInfo.EMPTY).isEmpty()) { //Render tanks
            this.tankLeftTop.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, pColor);
            this.tankRightTop.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, pColor);
        }
        this.mainBody.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, pColor);

        Item item = getBackpackStack().getItem();

        if(item == ModItems.FOX_TRAVELERS_BACKPACK.get()) {
            this.foxNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }

        if(item == ModItems.WOLF_TRAVELERS_BACKPACK.get()) {
            this.wolfNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }

        if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK.get() || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
            this.villagerNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }

        if(item == ModItems.OCELOT_TRAVELERS_BACKPACK.get()) {
            this.ocelotNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }

        if(item == ModItems.PIG_TRAVELERS_BACKPACK.get() || item == ModItems.HORSE_TRAVELERS_BACKPACK.get()) {
            this.pigNose.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }

        if(item == ModItems.WARDEN_TRAVELERS_BACKPACK.get()) {
            this.leftHorn.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
            this.rightHorn.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }

        if(this.buffer != null) {
            if(TravelersBackpackConfig.CLIENT.renderTools.get()) {
                this.stacks.prepare(getBackpackStack(), this.buffer);
                this.stacks.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
            }
            this.fluids.prepare(getBackpackStack(), this.buffer);
            this.fluids.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }
    }

    public void setupAngles(HumanoidModel<T> model) {
        //Backpack
        this.mainBody.copyFrom(model.body);
        this.sleepingBag.copyFrom(model.body);
        this.sleepingBagExtras.copyFrom(model.body);
        this.tankLeftTop.copyFrom(model.body);
        this.tankRightTop.copyFrom(model.body);

        //Noses
        this.villagerNose.copyFrom(model.body);
        this.pigNose.copyFrom(model.body);
        this.ocelotNose.copyFrom(model.body);
        this.wolfNose.copyFrom(model.body);
        this.foxNose.copyFrom(model.body);
        this.leftHorn.copyFrom(model.body);
        this.rightHorn.copyFrom(model.body);
        this.supporterBadgeModel.copyFrom(model.body);

        //Extras
        this.stacks.copyFrom(model.body);
        this.fluids.copyFrom(model.body);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
    }
}