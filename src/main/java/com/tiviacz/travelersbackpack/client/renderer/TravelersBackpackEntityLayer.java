package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class TravelersBackpackEntityLayer extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>>
{
    public TravelersBackpackWearableModel model;

    public TravelersBackpackEntityLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, LivingEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        if(TravelersBackpackConfig.disableBackpackRender) return;

        if(CapabilityUtils.isWearingBackpack(pLivingEntity))
        {
            if(!pLivingEntity.isInvisible())
            {
                renderLayer(pPoseStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            }
        }
    }

    private void renderLayer(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ItemStack stack = CapabilityUtils.getEntityCapability(livingEntity).resolve().get().getWearable();
        model = new TravelersBackpackWearableModel(livingEntity, bufferIn, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
        boolean flag = stack.getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || stack.getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get();

        boolean isCustomSleepingBag = false;

        ResourceLocation loc = ResourceUtils.getBackpackTexture(stack.getItem());

        if(stack.getTag() != null)
        {
            if(stack.getTag().contains("SleepingBagColor"))
            {
                isCustomSleepingBag = true;
            }
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

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        if(isCustomSleepingBag)
        {
            loc = ResourceUtils.getSleepingBagTexture(stack.getOrCreateTag().getInt( "SleepingBagColor"));
        }
        else
        {
            loc = ResourceUtils.getDefaultSleepingBagTexture();
        }

        vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.25F);

        poseStack.popPose();
    }
}