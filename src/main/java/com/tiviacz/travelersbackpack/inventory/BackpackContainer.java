package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class BackpackContainer implements MenuProvider, Nameable {
    public final ItemStack stack;
    public final Player player;
    public final int screenID;
    public final int index;

    public BackpackContainer(ItemStack stack, Player player, int screenID) {
        this(stack, player, screenID, -1);
    }

    public BackpackContainer(ItemStack stack, Player player, int screenID, int index) {
        this.stack = stack;
        this.player = player;
        this.screenID = screenID;
        this.index = index;
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
            return new BackpackItemMenu(pContainerId, pPlayerInventory, AttachmentUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer.registryAccess(), pPlayer, pPlayer.level(), this.index));
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
            serverPlayerEntity.openMenu(new BackpackContainer(stack, serverPlayerEntity, screenID), buf -> saveExtraData(buf, null, screenID));
        }
    }

    //Item
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide) {
            serverPlayerEntity.openMenu(new BackpackContainer(stack, serverPlayerEntity, screenID, index), buf -> saveExtraData(buf, index, screenID));
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, int screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);
            opener.openMenu(new BackpackContainer(stack, targetPlayer, screenID), buf -> saveExtraData(buf, targetPlayer, screenID));
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) {
            AttachmentUtils.getAttachment(target).ifPresent(cap -> PacketDistributor.sendToPlayer(opener, new ClientboundSyncAttachmentPacket(target.getId(), cap.getBackpack())));
        }
    }
}