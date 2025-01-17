package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.component.TravelersBackpackComponent;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BackpackContainer implements MenuProvider, Nameable {
    public final ItemStack stack;
    public final Player player;
    public final byte screenID;

    public BackpackContainer(ItemStack stack, Player player, byte screenID) {
        this.stack = stack;
        this.player = player;
        this.screenID = screenID;
    }

    @Override
    public Component getName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if(this.screenID == Reference.WEARABLE_SCREEN_ID) {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, ComponentUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer, pPlayer.level()));
        }
    }

    public static FriendlyByteBuf saveExtraData(FriendlyByteBuf buf, @Nullable Player target, ItemStack stack, byte screenID) {
        buf.writeByte(screenID);
        buf.writeInt(target == null ? -1 : target.getId());
        //Not needed + heavy data
        /*ItemStack backpackCopy = stack.copy();
        if(backpackCopy.getTag() != null) {
            backpackCopy.getTag().remove(ModDataHelper.BACKPACK_CONTAINER);
            backpackCopy.getTag().remove(ModDataHelper.TOOLS_CONTAINER);
            backpackCopy.getTag().remove(ModDataHelper.UPGRADES);
        }
        buf.writeItem(backpackCopy);*/
        return buf;
    }

    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            //NetworkHooks.openScreen(serverPlayerEntity, new BackpackContainer(stack, serverPlayerEntity, screenID), buf -> saveExtraData(buf, null, stack, screenID));
            serverPlayerEntity.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    saveExtraData(buf, null, stack, screenID);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level()));
                    }
                }
            });
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, byte screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);

            opener.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    saveExtraData(buf, targetPlayer, stack, screenID);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(targetPlayer));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level()));
                    }
                }
            });
            //NetworkHooks.openScreen(opener, new BackpackContainer(stack, targetPlayer, screenID), buf -> saveExtraData(buf, targetPlayer, stack, screenID));
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) { //Sync data from target to opener
            ComponentUtils.WEARABLE.syncWith(opener, (ComponentProvider)target, (buf, rec) -> ((TravelersBackpackComponent)ComponentUtils.WEARABLE.get(target)).writeSyncPacket(ComponentUtils.getWearingBackpack(target), buf, rec, false), p -> true);
            //ComponentUtils.getComponent(target).ifPresent(cap -> PacketDistributor.sendToPlayer(opener, new ClientboundSyncAttachmentPacket(target.getId(), cap.getBackpack())));
        }
    }
}