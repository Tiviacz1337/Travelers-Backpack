package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    public BackpackLayerModel model;

    public TravelersBackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (TravelersBackpack.enableCurios()) return;

        if (CapabilityUtils.isWearingBackpack(clientPlayer)) {
            ItemStack stack = CapabilityUtils.getWearingBackpack(clientPlayer);
            renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, getParentModel(), poseStack, bufferIn, packedLightIn, clientPlayer, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public static void renderBackpackLayer(BackpackLayerModel model, HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        //if (!(stack.hasTag() && stack.getTag().contains(ITravelersBackpackContainer.VISIBILITY) ? stack.getTag().getBoolean(ITravelersBackpackContainer.VISIBILITY) : true)) return;

        model.setLivingEntity(livingEntity);
        model.setMultiBufferSource(bufferIn);

        if (!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        boolean translucentLayer = travelersBackpackItem == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || travelersBackpackItem == ModItems.SNOW_TRAVELERS_BACKPACK.get();
        ResourceLocation loc = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = bufferIn.getBuffer(translucentLayer ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, model, livingEntity);

        if (BackpackDyeRecipe.hasColor(stack) && travelersBackpackItem == ModItems.STANDARD_TRAVELERS_BACKPACK.get()) {
            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entitySolid(loc));
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(stack));
            model.mainBody.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        int sleepingBagColor = stack.hasTag() && stack.getTag().contains(ITravelersBackpackContainer.SLEEPING_BAG_COLOR) ? stack.getTag().getInt(ITravelersBackpackContainer.SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
        loc = BackpackBlockModel.getSleepingBagTexture(sleepingBagColor);
        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackLayerModel backpackModel, LivingEntity livingEntity) {
        if (livingEntity.isCrouching()) {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        parent.copyPropertiesTo(backpackModel);
        backpackModel.setupAngles(parent);

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        if (livingEntity.isBaby()) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = livingEntity.getScale();
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }
}