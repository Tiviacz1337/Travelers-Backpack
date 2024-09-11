package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class TravelersBackpackRenderer implements TrinketRenderer
{
    public static void init()
    {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketRendererRegistry.registerRenderer(item, new TravelersBackpackRenderer()));
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
    {
        if(entity instanceof PlayerEntity player && contextModel instanceof PlayerEntityModel playerEntityModel)
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
            if(inv == null) return;
            TravelersBackpackWearableModel<AbstractClientPlayerEntity> model = new TravelersBackpackWearableModel<>(player, vertexConsumers, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).createModel());
            boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

            if(inv.getItemStack().isEmpty() || !(inv.getItemStack().getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

            Identifier id = travelersBackpackItem.getBackpackTexture();

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

            playerEntityModel.copyBipedStateTo(model);
            model.setupAngles(playerEntityModel);

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
}