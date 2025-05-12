package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackModel;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final BackpackModel BACKPACK_MODEL = new BackpackModel();

    public BackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(TravelersBackpack.enableIntegration()) return;

        if(ComponentUtils.isWearingBackpack(clientPlayer)) {
            ItemStack stack = ComponentUtils.getWearingBackpack(clientPlayer);
            renderBackpackLayer(getParentModel(), poseStack, bufferIn, packedLightIn, clientPlayer, stack);
        }
    }

    public static void renderBackpackLayer(HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack) {
        if(!NbtHelper.getOrDefault(stack, ModDataHelper.IS_VISIBLE, true))
            return;

        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, BACKPACK_MODEL, entity);
        BACKPACK_MODEL.render(poseStack, packedLightIn, bufferIn, stack);

        if(entity instanceof Player player && Supporters.SUPPORTERS.contains(player.getGameProfile().getName())) {
            BACKPACK_MODEL.supporterBadgeModel.render(poseStack, packedLightIn);
        }

        poseStack.popPose();
    }

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackModel backpackModel, LivingEntity entity) {
        backpackModel.copyFrom(parent.body);
        backpackModel.supporterBadgeModel.copyFrom(parent.body);

        if(entity.isBaby()) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = entity.getScale();
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }

    /*public static void renderBackpackLayer(BackpackLayerModel model, HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack) {
        if(!NbtHelper.getOrDefault(stack, ModDataHelper.IS_VISIBLE, true))
            return;

        model.setBackpackStack(stack);
        model.setMultiBufferSource(bufferIn);

        if(!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        boolean translucentType = travelersBackpackItem == ModItems.QUARTZ_TRAVELERS_BACKPACK || travelersBackpackItem == ModItems.SNOW_TRAVELERS_BACKPACK;
        boolean cutoutType = travelersBackpackItem == ModItems.WARDEN_TRAVELERS_BACKPACK;

        ResourceLocation loc = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = bufferIn.getBuffer(translucentType ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));
        if(cutoutType) vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, model, entity);

        if(NbtHelper.has(stack, ModDataHelper.COLOR) && stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK) {
            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entitySolid(loc));
            Triple<Float, Float, Float> rgb = RenderHelper.intToRGB(BackpackDyeRecipe.getColor(stack));
            model.mainBody.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        loc = BackpackBlockModel.getSleepingBagTexture(NbtHelper.getOrDefault(stack, ModDataHelper.SLEEPING_BAG_COLOR, DyeColor.RED.getId()));
        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);

        if(entity instanceof Player player && Supporters.SUPPORTERS.contains(player.getGameProfile().getName())) {
            model.supporterBadgeModel.render(poseStack, packedLightIn);
        }

        poseStack.popPose();
    }

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackLayerModel backpackModel, LivingEntity entity) {
        if(entity.isCrouching()) {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        parent.copyPropertiesTo(backpackModel);
        backpackModel.setupAngles(parent);

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        if(entity.isBaby()) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = entity.getScale();
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    } */
}