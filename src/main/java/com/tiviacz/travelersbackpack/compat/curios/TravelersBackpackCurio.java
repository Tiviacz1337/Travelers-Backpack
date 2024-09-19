package com.tiviacz.travelersbackpack.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public record TravelersBackpackCurio(ItemStack stack) implements ICurio {
    public static void registerCurio(RegisterCapabilitiesEvent event) {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> event.registerItem(CuriosCapability.ITEM, (stack, context) -> new TravelersBackpackCurio(stack), holder::get));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerCurioRenderer() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> CuriosRendererRegistry.register(holder.get(), Renderer::new));
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public boolean canEquip(SlotContext context) {
        return TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get();
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if (slotContext.entity() instanceof Player player) {
            if (player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if (!player.level().isClientSide) {
                AttachmentUtils.getAttachment(player).ifPresent(data -> {
                    data.setWearable(stack);
                    data.setContents(stack);

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                AttachmentUtils.getAttachment(player).ifPresent(data -> {
                    data.setWearable(stack);
                    data.setContents(stack);

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if (slotContext.entity() instanceof Player player) {
            if (player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if (!player.level().isClientSide) {
                AttachmentUtils.getAttachment(player).ifPresent(data -> {
                    data.removeWearable();

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        if (!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if (slotContext.entity() instanceof Player player) {
            if (player.containerMenu instanceof TravelersBackpackItemMenu || !AttachmentUtils.isWearingBackpack(player))
                return;

            ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);

            if (!ItemStack.isSameItemSameComponents(backpackStack, getStack())) {
                getStack().applyComponents(backpackStack.getComponentsPatch());
            }
        }
    }

    @Nonnull
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
        return DropRule.DEFAULT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements ICurioRenderer {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (slotContext.entity() instanceof Player player && renderLayerParent.getModel() instanceof PlayerModel<?> playerModel) {
                ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player);
                TravelersBackpackLayer.renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, playerModel, matrixStack, renderTypeBuffer, light, player, backpackStack);
            }
        }
    }
}