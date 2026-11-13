package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record BackpackContainer(ItemStack stack, Player player, OpenContext openContext, int index) {
    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(@Nullable Player target, int openContext) {
        return new ModScreenHandlerTypes.ItemScreenData(openContext, target == null ? -1 : target.getId());
    }

    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(int index, int openContext) {
        return new ModScreenHandlerTypes.ItemScreenData(openContext, index);
    }

    //Component
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, OpenContext openContext) {
        if(!serverPlayerEntity.level().isClientSide()) {
            serverPlayerEntity.openMenu(new ExtendedMenuProvider<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(openContext.isEquipped()) {
                        return new BackpackItemMenu(i, inventory, AttachmentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, openContext, player));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(null, openContext.getId());
                }
            });
        }
    }

    //Item
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, OpenContext openContext, int index) {
        if(!serverPlayerEntity.level().isClientSide()) {
            serverPlayerEntity.openMenu(new ExtendedMenuProvider<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public boolean shouldCloseCurrentScreen() {
                    return false;
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(openContext.isEquipped()) {
                        return new BackpackItemMenu(i, inventory, AttachmentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, openContext, player, index));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(index, openContext.getId());
                }
            });
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, OpenContext openContext) {
        if(!opener.level().isClientSide()) {
            synchroniseToOpener(opener, targetPlayer);
            opener.openMenu(new ExtendedMenuProvider<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(openContext.isEquipped()) {
                        return new BackpackItemMenu(i, inventory, AttachmentUtils.getBackpackWrapper(targetPlayer));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, openContext, player));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(targetPlayer, openContext.getId());
                }
            });
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) {
            AttachmentUtils.getAttachment(target).ifPresent(attachment -> PacketDistributor.sendToPlayer(opener, new ClientboundSyncAttachmentPacket(target.getId(), attachment.getBackpack())));
        }
    }
}