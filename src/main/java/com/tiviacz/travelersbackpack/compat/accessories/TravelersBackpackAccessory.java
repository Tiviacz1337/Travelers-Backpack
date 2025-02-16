package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TravelersBackpackAccessory implements Accessory {
    public static void init() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> AccessoriesAPI.registerAccessory(item, new TravelersBackpackAccessory()));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        BuiltInRegistries.ITEM.stream()
                .filter(holder -> holder instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder, Renderer::new));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return TravelersBackpackConfig.getConfig().backpackSettings.backSlotIntegration;
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.backSlotIntegration) return;
        if(reference.entity() instanceof Player player) {
            BackpackWrapper.tick(stack, player, true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<S> entityModel, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
            if(stack.getItem() instanceof TravelersBackpackItem && entityModel instanceof PlayerModel playerModel && renderState instanceof HumanoidRenderState humanoidRenderState) {
                BackpackLayer.renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, playerModel, matrices, multiBufferSource, light, humanoidRenderState, stack);
            }
        }

        @Override
        public <S extends LivingEntityRenderState> void align(ItemStack itemStack, SlotReference slotReference, EntityModel<S> entityModel, S s, PoseStack poseStack) {

        }
    }
}