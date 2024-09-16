package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import com.tiviacz.travelersbackpack.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value = EnvType.CLIENT)
public class TravelersBackpackFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
{
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

                renderBackpackFeature(BackpackFeatureModel.FEATURE_MODEL, this.getContextModel(), matrices, vertexConsumers, light, entity, inv.getItemStack());
            }
        }
    }

    public static void renderBackpackFeature(BackpackFeatureModel model, BipedEntityModel bipedEntityModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, ItemStack stack)
    {
        model.setLivingEntity(entity);
        model.setVertexConsumerProvider(vertexConsumers);

        boolean translucentLayer = stack.getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || stack.getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

        if(!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        Identifier id = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(translucentLayer ? RenderLayer.getEntityTranslucentCull(id) : RenderLayer.getEntitySolid(id));

        matrices.push();
        alignModel(matrices, bipedEntityModel, model, entity);

        if(stack.contains(DataComponentTypes.DYED_COLOR))
        {
            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(id));
            model.mainBody.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.fullAlpha(stack.get(DataComponentTypes.DYED_COLOR).rgb()));

            id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        }

        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);

        id = BackpackBlockModel.getSleepingBagTexture(stack.getOrDefault(ModComponentTypes.SLEEPING_BAG_COLOR, DyeColor.RED.getId()));
        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        model.sleepingBag.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);

        matrices.pop();
    }

    public static void alignModel(MatrixStack matrices, BipedEntityModel parent, BackpackFeatureModel backpackModel, LivingEntity entity)
    {
        if(entity.isInSneakingPose())
        {
            matrices.translate(0D, -0.155D, 0.025D);
        }

        parent.copyBipedStateTo(backpackModel);
        backpackModel.setupAngles(parent);

        matrices.translate(0, 0.175, 0.325);
        matrices.scale(0.85F, 0.85F, 0.85F);
    }
}