package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackEntityLayer extends LayerRenderer<LivingEntity, BipedModel<LivingEntity>>
{
    public TravelersBackpackWearableModel model;

    public TravelersBackpackEntityLayer(IEntityRenderer<LivingEntity, BipedModel<LivingEntity>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if(TravelersBackpackConfig.disableBackpackRender) return;

        if(CapabilityUtils.isWearingBackpack(livingEntity))
        {
            if(!livingEntity.isInvisible())
            {
                renderLayer(matrixStackIn, bufferIn, packedLightIn, livingEntity);
            }
        }
    }

    private void renderLayer(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity livingEntity)
    {
        ItemStack stack = CapabilityUtils.getEntityCapability(livingEntity).resolve().get().getWearable();
        model = new TravelersBackpackWearableModel(livingEntity, bufferIn);
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

        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

        matrixStackIn.pushPose();

        if(livingEntity.isCrouching())
        {
            matrixStackIn.translate(0D, -0.155D, 0.025D);
        }

        if(livingEntity.isBaby())
        {
            matrixStackIn.translate(0.0D, 0.65D, 0.0D);
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        }

        this.getParentModel().copyPropertiesTo(model);
        model.setupAngles(this.getParentModel());

        matrixStackIn.translate(0, 0.175, 0.325);
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);

        model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        if(isCustomSleepingBag)
        {
            loc = ResourceUtils.getSleepingBagTexture(stack.getOrCreateTag().getInt( "SleepingBagColor"));
        }
        else
        {
            loc = ResourceUtils.getDefaultSleepingBagTexture();
        }

        ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutout(loc));
        model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.25F);

        matrixStackIn.popPose();
    }
}