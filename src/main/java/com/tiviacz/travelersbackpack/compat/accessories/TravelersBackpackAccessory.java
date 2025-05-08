package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TravelersBackpackAccessory implements Accessory {
    public static void init() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesAPI.registerAccessory(holder.get(), new TravelersBackpackAccessory()));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder.get(), Renderer::new));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get()) return;
        if(reference.entity() instanceof Player player) {
            BackpackWrapper.tick(stack, player, true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> entityModel, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(stack.getItem() instanceof TravelersBackpackItem && reference.entity() instanceof Player player && entityModel instanceof PlayerModel<?> playerModel) {
                BackpackLayer.renderBackpackLayer(playerModel, matrices, multiBufferSource, light, player, stack);
            }
        }

        @Override
        public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack matrices) {
        }
    }
}