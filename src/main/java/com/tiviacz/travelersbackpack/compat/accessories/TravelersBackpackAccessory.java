package com.tiviacz.travelersbackpack.compat.accessories;

import com.tiviacz.travelersbackpack.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.client.AccessoriesMenu;
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

public class TravelersBackpackAccessory implements Accessory {
    public static void init() {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> AccessoriesAPI.registerAccessory(item, new TravelersBackpackAccessory()));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> AccessoriesRendererRegistry.registerRenderer(item, Renderer::new));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration) return;

        if (reference.entity() instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if (!player.getWorld().isClient) {
                ComponentUtils.getComponent(player).setContents(stack);
                ComponentUtils.getComponent(player).setWearable(stack);
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration) return;

        if (reference.entity() instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if (!player.getWorld().isClient) {
                ComponentUtils.getComponent(player).removeWearable();
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if (!TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration) return;

        if (reference.entity() instanceof PlayerEntity player) {
            if (player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler || !ComponentUtils.isWearingBackpack(player))
                return;

            //Prevent dupe bug, happens only with Accessories
            if (player.currentScreenHandler instanceof AccessoriesMenu) return;

            TravelersBackpackInventory container = ComponentUtils.getBackpackInv(player);

            if (!ItemStack.areItemsAndComponentsEqual(container.getItemStack(), stack)) {
                stack.applyChanges(container.getItemStack().getComponentChanges());
                //this.onEquip(stack, slot, entity);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, MatrixStack matrices, EntityModel<M> entityModel, VertexConsumerProvider vertexConsumers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (reference.entity() instanceof PlayerEntity player && entityModel instanceof PlayerEntityModel<?> playerEntityModel) {
                TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
                if (inv == null) return;

                TravelersBackpackFeature.renderBackpackFeature(BackpackFeatureModel.FEATURE_MODEL, playerEntityModel, matrices, vertexConsumers, light, player, inv.getItemStack());
            }
        }

        @Override
        public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, MatrixStack matrices) {

        }
    }
}