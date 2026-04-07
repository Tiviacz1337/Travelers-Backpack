package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record BackpackContainer(ItemStack stack, Player player, int screenID, int index) {
    public BackpackContainer(ItemStack stack, Player player, int screenID) {
        this(stack, player, screenID, -1);
    }

    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(@Nullable Player target, int screenID) {
        return new ModScreenHandlerTypes.ItemScreenData(screenID, target == null ? -1 : target.getId());
    }

    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(int index, int screenID) {
        return new ModScreenHandlerTypes.ItemScreenData(screenID, index);
    }

    //Component
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID) {
        if(!serverPlayerEntity.level().isClientSide()) {
            serverPlayerEntity.openMenu(new ExtendedMenuProvider<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, AttachmentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(null, screenID);
                }
            });
        }
    }

    //Item
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide()) {
            serverPlayerEntity.openMenu(new ExtendedMenuProvider<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, AttachmentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level(), index));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(index, screenID);
                }
            });
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, int screenID) {
        if(!opener.level().isClientSide()) {
            synchroniseToOpener(opener, targetPlayer);
            opener.openMenu(new ExtendedMenuProvider<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, AttachmentUtils.getBackpackWrapper(targetPlayer));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(targetPlayer, screenID);
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