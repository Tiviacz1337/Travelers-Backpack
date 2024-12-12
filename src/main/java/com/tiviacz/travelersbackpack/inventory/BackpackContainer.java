package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BackpackContainer { //implements MenuProvider, Nameable {
    public final ItemStack stack;
    public final Player player;
    public final byte screenID;

    public BackpackContainer(ItemStack stack, Player player, byte screenID) {
        this.stack = stack;
        this.player = player;
        this.screenID = screenID;
    }

    //@Override
    public Component getName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    //@Override
    public Component getDisplayName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    @Nullable
    //@Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if(this.screenID == Reference.WEARABLE_SCREEN_ID) {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, ComponentUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer.registryAccess(), pPlayer, pPlayer.level()));
        }
    }

    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(@Nullable Player target, ItemStack stack, byte screenID) {
        return new ModScreenHandlerTypes.ItemScreenData(screenID, target == null ? -1 : target.getId(), stack);
        // buf.writeByte(screenID);
        // buf.writeInt(target == null ? -1 : target.getId());
        // ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        //return buf;
    }

    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            //serverPlayerEntity.openMenu(new BackpackContainer(stack, serverPlayerEntity, screenID), saveExtraData(null, stack, screenID));

            serverPlayerEntity.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player.registryAccess(), player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(null, stack, screenID);
                }
            });
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, byte screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);
            //opener.openMenu(new BackpackContainer(stack, targetPlayer, screenID), buf -> saveExtraData(targetPlayer, stack, screenID));

            opener.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player.registryAccess(), player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(targetPlayer, stack, screenID);
                }
            });
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) {
            ComponentUtils.getComponent(target).ifPresent(cap -> PacketDistributor.sendToPlayer(opener, new ClientboundSyncAttachmentPacket(target.getId(), cap.getBackpack())));
        }
    }
}