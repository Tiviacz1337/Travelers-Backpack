package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.renderers.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.core.Accessory;
import io.wispforest.accessories.api.slot.SlotPath;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
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
    public boolean canEquipFromUse(ItemStack stack, SlotReference reference) {
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
        public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices, EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
            if(stack.getItem() instanceof TravelersBackpackItem && model instanceof PlayerModel playerModel && renderState instanceof PlayerRenderState playerRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, matrices, multiBufferSource, light, playerRenderState, stack);
            }
        }

        @Override
        public <S extends LivingEntityRenderState> void align(ItemStack itemStack, SlotPath slotPath, EntityModel<S> entityModel, S s, PoseStack poseStack) {

        }
    }
}