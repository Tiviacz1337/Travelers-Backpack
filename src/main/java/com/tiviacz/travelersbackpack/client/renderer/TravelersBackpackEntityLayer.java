package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackEntityLayer extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>> {
    public TravelersBackpackEntityLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, LivingEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.getItemBySlot(EquipmentSlot.BODY).getItem() instanceof TravelersBackpackItem) {
            TravelersBackpackLayer.renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, getParentModel(), pPoseStack, pBuffer, pPackedLight, pLivingEntity, pLivingEntity.getItemBySlot(EquipmentSlot.BODY));
        }
        /*  if(TravelersBackpackConfig.CLIENT.disableBackpackRender.get()) return;

        if(AttachmentUtils.isWearingBackpack(pLivingEntity))
        {
            if(!pLivingEntity.isInvisible())
            {
                renderLayer(pPoseStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            }
        } */
    }

  /*  private void renderLayer(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ItemStack stack = AttachmentUtils.getEntityAttachment(livingEntity).get().getWearable();
        model = new BackpackLayerModel(livingEntity, bufferIn, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
        boolean flag = stack.getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get();

        boolean isCustomSleepingBag = false;

        ResourceLocation loc = ((TravelersBackpackItem)stack.getItem()).getBackpackTexture();

        if(stack.has(ModDataComponents.SLEEPING_BAG_COLOR))
        {
            isCustomSleepingBag = true;
        }

        VertexConsumer vertexConsumer = bufferIn.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

        poseStack.pushPose();

        if(livingEntity.isCrouching())
        {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        if(livingEntity.isBaby())
        {
            poseStack.translate(0.0D, 0.65D, 0.0D);
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }

        this.getParentModel().copyPropertiesTo(model);
        model.setupAngles(this.getParentModel());

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);

        if(isCustomSleepingBag)
        {
            loc = ResourceUtils.getSleepingBagTexture(stack.get(ModDataComponents.SLEEPING_BAG_COLOR));
        }
        else
        {
            loc = ResourceUtils.getDefaultSleepingBagTexture();
        }

        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.sleepingBag.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
    } */
}