package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public record BackpackContainer(ItemStack stack, Player player, int screenID,
                                int index) implements MenuProvider, Nameable {
    public BackpackContainer(ItemStack stack, Player player, int screenID) {
        this(stack, player, screenID, -1);
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
            return new BackpackItemMenu(pContainerId, pPlayerInventory, CapabilityUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer, pPlayer.level(), this.index));
        }
    }

    public static FriendlyByteBuf saveExtraData(FriendlyByteBuf buf, @Nullable Player target, int screenID) {
        buf.writeInt(screenID);
        buf.writeInt(target == null ? -1 : target.getId());
        return buf;
    }

    public static FriendlyByteBuf saveExtraData(FriendlyByteBuf buf, int index, int screenID) {
        buf.writeInt(screenID);
        buf.writeInt(index);
        return buf;
    }

    //Capability
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            NetworkHooks.openScreen(serverPlayerEntity, new BackpackContainer(stack, serverPlayerEntity, screenID), buf -> saveExtraData(buf, null, screenID));
        }
    }

    //Item
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide) {
            NetworkHooks.openScreen(serverPlayerEntity, new BackpackContainer(stack, serverPlayerEntity, screenID, index), buf -> saveExtraData(buf, index, screenID));
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, int screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);
            NetworkHooks.openScreen(opener, new BackpackContainer(stack, targetPlayer, screenID), buf -> saveExtraData(buf, targetPlayer, screenID));
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) {
            CapabilityUtils.getCapability(target).ifPresent(cap -> {
                PacketDistributorHelper.sendToPlayer(opener, new ClientboundSyncCapabilityPacket(target.getId(), cap.getBackpack()));
            });
        }
    }
}