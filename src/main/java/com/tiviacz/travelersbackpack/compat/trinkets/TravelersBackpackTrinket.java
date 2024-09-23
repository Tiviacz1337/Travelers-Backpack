package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
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
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        //Keep for compat
        if(!TravelersBackpack.isAnyGraveModInstalled())
        {
            return TrinketEnums.DropRule.DEFAULT;
        }
        return TrinketEnums.DropRule.DEFAULT;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if (entity instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if (!player.getWorld().isClient) {
                ComponentUtils.getComponent(player).setWearable(stack);
                ComponentUtils.getComponent(player).setContents(stack);
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
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler || !ComponentUtils.isWearingBackpack(player)) return;

            //Patch for Accessories dupe bug
            if (TravelersBackpack.accessoriesLoaded) {
                if(AccessoriesPatch.isAccessoriesMenuOpened(player)) return;
             }

            ItemStack backpack = ComponentUtils.getWearingBackpack(player);

            if(!ItemStack.canCombine(backpack, stack))
            {
                stack.setNbt(backpack.getNbt());
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements TrinketRenderer {
        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            if (entity instanceof PlayerEntity player && contextModel instanceof PlayerEntityModel<?> playerEntityModel) {
                ItemStack backpackStack = ComponentUtils.getWearingBackpack(player);
                TravelersBackpackFeature.renderBackpackFeature(BackpackFeatureModel.FEATURE_MODEL, playerEntityModel, matrices, vertexConsumers, light, player, backpackStack);
            }
        }
    }
}