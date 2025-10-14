package com.tiviacz.travelersbackpack.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TravelersBackpackTrinket implements Trinket {
    public static void init() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketsApi.registerTrinket(item, new TravelersBackpackTrinket()));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketRendererRegistry.registerRenderer(item, new Renderer()));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return TravelersBackpackConfig.getConfig().backpackSettings.backSlotIntegration;
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.backSlotIntegration) return;
        if(entity instanceof Player player) {
            BackpackWrapper.tick(stack, player, true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements TrinketRenderer {
        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntityRenderState> contextModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, LivingEntityRenderState livingEntityRenderState, float headYaw, float headPitch) {
            if(stack.getItem() instanceof TravelersBackpackItem && contextModel instanceof PlayerModel playerModel && livingEntityRenderState instanceof HumanoidRenderState humanoidRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, poseStack, multiBufferSource, light, humanoidRenderState, stack);
            }
        }
    }
}