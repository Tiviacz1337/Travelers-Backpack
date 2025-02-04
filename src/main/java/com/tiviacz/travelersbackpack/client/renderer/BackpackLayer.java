package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public BackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(TravelersBackpack.enableIntegration()) return;

        if(AttachmentUtils.isWearingBackpack(clientPlayer)) {
            ItemStack stack = AttachmentUtils.getWearingBackpack(clientPlayer);
            renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, getParentModel(), poseStack, bufferIn, packedLightIn, clientPlayer, stack);
        }
    }

    public static void renderBackpackLayer(BackpackLayerModel model, HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack) {
        if(!stack.getOrDefault(ModDataComponents.IS_VISIBLE.get(), true)) return;

        model.setBackpackStack(stack);
        model.setMultiBufferSource(bufferIn);

        if(!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        boolean translucentType = travelersBackpackItem == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || travelersBackpackItem == ModItems.SNOW_TRAVELERS_BACKPACK.get();
        boolean cutoutType = travelersBackpackItem == ModItems.WARDEN_TRAVELERS_BACKPACK.get();

        ResourceLocation loc = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = bufferIn.getBuffer(translucentType ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));
        if(cutoutType) vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, model, entity);

        if(stack.has(DataComponents.DYED_COLOR) && stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get()) {
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entitySolid(loc));
            model.mainBody.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(stack.get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);

        loc = BackpackBlockModel.getSleepingBagTexture(stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR.get(), DyeColor.RED.getId()));
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
            float scaleFactor = entity.getAgeScale();
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }
}