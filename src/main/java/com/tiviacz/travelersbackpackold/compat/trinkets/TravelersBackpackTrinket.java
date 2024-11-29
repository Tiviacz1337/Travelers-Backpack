package com.tiviacz.travelersbackpackold.compat.trinkets;

import com.tiviacz.travelersbackpackold.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpackold.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class TravelersBackpackTrinket implements Trinket {
    public static void init() {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketsApi.registerTrinket(item, new TravelersBackpackTrinket()));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketRendererRegistry.registerRenderer(item, new Renderer()));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration;
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return TrinketEnums.DropRule.DEFAULT;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if (entity instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if (!player.getWorld().isClient) {
                ComponentUtils.getComponent(player).setContents(stack);
                ComponentUtils.getComponent(player).setWearable(stack);
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if (entity instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if (!player.getWorld().isClient) {
                ComponentUtils.getComponent(player).removeWearable();
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if (entity instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler || !ComponentUtils.isWearingBackpack(player))
                return;

            ItemStack backpack = ComponentUtils.getWearingBackpack(player);

            if (!ItemStack.areItemsAndComponentsEqual(backpack, stack)) {
                stack.applyChanges(backpack.getComponentChanges());
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements TrinketRenderer {
        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            if (entity instanceof PlayerEntity player && contextModel instanceof PlayerEntityModel<?> playerEntityModel) {

                BackpackFeatureModel<?> backpackFeatureModel = BackpackFeatureModel.FEATURE_MODEL;
                backpackFeatureModel.setBackpackStack(stack);

                TravelersBackpackFeature.renderBackpackFeature(backpackFeatureModel, playerEntityModel, matrices, vertexConsumers, light, player, stack);
            }
        }
    }
}