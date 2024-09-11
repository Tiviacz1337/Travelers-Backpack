package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value = EnvType.CLIENT)
public class TravelersBackpackFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
{
    public TravelersBackpackWearableModel<AbstractClientPlayerEntity> model;

    public TravelersBackpackFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> entityRendererIn)
    {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
    {
        if(TravelersBackpackConfig.getConfig().client.disableBackpackRender || TravelersBackpack.enableIntegration()) return;

        if(ComponentUtils.isWearingBackpack(entity))
        {
            ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(entity);

            if(inv != null && !entity.isInvisible())
            {
                if(!TravelersBackpackConfig.getConfig().client.renderBackpackWithElytra && entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem) return;

                renderLayer(matrices, vertexConsumers, light, entity, inv);
            }
        }
    }

    private void renderLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, ITravelersBackpackInventory inv)
    {
        model = new TravelersBackpackWearableModel<>(entity, vertexConsumers, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).createModel());
        boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

        if(inv.getItemStack().isEmpty()) return;

        Identifier id = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());

        boolean isColorable = false;
        boolean isCustomSleepingBag = false;

        if(inv.getItemStack().contains(DataComponentTypes.DYED_COLOR) && inv.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
        {
            isColorable = true;
            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed.png");
        }

        if(inv.getItemStack().contains(ModComponentTypes.SLEEPING_BAG_COLOR))
        {
            isCustomSleepingBag = true;
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(flag ? RenderLayer.getEntityTranslucentCull(id) : RenderLayer.getEntitySolid(id));

        matrices.push();

        if(entity.isSneaking())
        {
            matrices.translate(0D, -0.155D, 0.025D);
        }

        this.getContextModel().copyBipedStateTo(model);
        model.setupAngles(this.getContextModel());

        matrices.translate(0, 0.175, 0.325);
        matrices.scale(0.85F, 0.85F, 0.85F);

        if(isColorable)
        {
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.fullAlpha(inv.getItemStack().get(DataComponentTypes.DYED_COLOR).rgb()));

            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        }
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);

        if(isCustomSleepingBag)
        {
            id = ResourceUtils.getSleepingBagTexture(inv.getSleepingBagColor());
        }
        else
        {
            id = ResourceUtils.getDefaultSleepingBagTexture();
        }

        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        model.sleepingBag.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);

        matrices.pop();
    }
}