package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.menu.variants.AccessoriesMenuBase;
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
        return TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get();
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get()) return;

        if (reference.entity() instanceof Player player) {
            if (player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if (!player.level().isClientSide) {
                AttachmentUtils.getAttachment(player).ifPresent(cap -> {
                    cap.setContents(stack);
                    cap.setWearable(stack);
                });
            }
            AttachmentUtils.synchronise(player);
            AttachmentUtils.synchroniseToOthers(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get()) return;

        if (reference.entity() instanceof Player player) {
            if (player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if (!player.level().isClientSide) {
                AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);
            }
            AttachmentUtils.synchronise(player);
            AttachmentUtils.synchroniseToOthers(player);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get()) return;

        if (reference.entity() instanceof Player player) {
            if (player.containerMenu instanceof TravelersBackpackItemMenu || !AttachmentUtils.isWearingBackpack(player))
                return;

            //Prevent dupe bug, happens only with Accessories
            if (player.containerMenu instanceof AccessoriesMenuBase) return;

            ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);

            if (!ItemStack.isSameItemSameComponents(backpackStack, stack)) {
                stack.applyComponents(backpackStack.getComponentsPatch());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> entityModel, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (reference.entity() instanceof Player player && entityModel instanceof PlayerModel<?> playerModel) {
                BackpackLayerModel<?> backpackFeatureModel = BackpackLayerModel.LAYER_MODEL;
                backpackFeatureModel.setBackpackStack(stack);

                TravelersBackpackLayer.renderBackpackLayer(backpackFeatureModel, playerModel, matrices, multiBufferSource, light, player, stack);
            }
        }

        @Override
        public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack matrices) {}
    }
}