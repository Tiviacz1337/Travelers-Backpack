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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public TravelersBackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (TravelersBackpack.enableIntegration()) return;

        if(AttachmentUtils.isWearingBackpack(clientPlayer)) {
            ItemStack stack = AttachmentUtils.getWearingBackpack(clientPlayer);
            renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, getParentModel(), poseStack, bufferIn, packedLightIn, clientPlayer, stack);
        }
            /*ITravelersBackpackContainer inv = AttachmentUtils.getBackpackInv(clientPlayer);

            if(inv != null && !clientPlayer.isInvisible())
            {
                if(!TravelersBackpackConfig.CLIENT.renderBackpackWithElytra.get() && clientPlayer.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem) return;

                renderLayer(poseStack, bufferIn, packedLightIn, clientPlayer, inv, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        } */
    }

    public static void renderBackpackLayer(BackpackLayerModel model, HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack) {
        if (!stack.getOrDefault(ModDataComponents.VISIBILITY, true)) return;

        model.setLivingEntity(entity);
        model.setMultiBufferSource(bufferIn);

        boolean translucentType = stack.getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get();

        if (!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        ResourceLocation loc = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = bufferIn.getBuffer(translucentType ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, model, entity);

        if (stack.has(DataComponents.DYED_COLOR)) {
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entitySolid(loc));
            model.mainBody.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(stack.get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);

        loc = BackpackBlockModel.getSleepingBagTexture(stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId()));
        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

   /* private void renderLayer(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, ITravelersBackpackContainer container, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        model = new BackpackLayerModel(clientPlayer, bufferIn, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
        boolean flag = container.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || container.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get();

        if(container.getItemStack().isEmpty() || !(container.getItemStack().getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        ResourceLocation loc = travelersBackpackItem.getBackpackTexture(); //ResourceUtils.getBackpackTexture(container.getItemStack().getItem());

        boolean isColorable = false;
        boolean isCustomSleepingBag = false;

        if(container.getItemStack().has(DataComponents.DYED_COLOR) && container.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
        {
            isColorable = true;
            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
        }

        if(container.getItemStack().has(ModDataComponents.SLEEPING_BAG_COLOR))
        {
            isCustomSleepingBag = true;
        }

        VertexConsumer vertexConsumer = bufferIn.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

        poseStack.pushPose();

        if(clientPlayer.isCrouching())
        {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        this.getParentModel().copyPropertiesTo(model);
        model.setupAngles(this.getParentModel());

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        if(isColorable)
        {
            model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(container.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

            loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);

        if(isCustomSleepingBag)
        {
            loc = ResourceUtils.getSleepingBagTexture(container.getSleepingBagColor());
        }
        else
        {
            loc = ResourceUtils.getDefaultSleepingBagTexture();
        }

        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
    } */

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackLayerModel backpackModel, LivingEntity entity) {
        if (entity.isCrouching()) {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        parent.copyPropertiesTo(backpackModel);
        backpackModel.setupAngles(parent);

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        if (entity.isBaby()) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = entity.getAgeScale();
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }
}