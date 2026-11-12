package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
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

public record BackpackContainer(ItemStack stack, Player player, OpenContext openContext,
                                int index,
                                boolean triggerClientSideContainerClosing) implements MenuProvider, Nameable {
    public BackpackContainer(ItemStack stack, Player player, OpenContext openContext, int index) {
        this(stack, player, openContext, index, true);
    }

    public BackpackContainer(ItemStack stack, Player player, OpenContext openContext) {
        this(stack, player, openContext, -1, true);
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
        if(this.openContext.isEquipped()) {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, AttachmentUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.openContext, pPlayer, this.index));
        }
    }

    @Override
    public boolean shouldTriggerClientSideContainerClosingOnOpen() {
        return this.triggerClientSideContainerClosing;
    }

    public static FriendlyByteBuf saveExtraData(FriendlyByteBuf buf, @Nullable Player target, OpenContext openContext) {
        buf.writeInt(openContext.getId());
        buf.writeInt(target == null ? -1 : target.getId());
        return buf;
    }

    public static FriendlyByteBuf saveExtraData(FriendlyByteBuf buf, int index, OpenContext openContext) {
        buf.writeInt(openContext.getId());
        buf.writeInt(index);
        return buf;
    }

    //Capability
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, OpenContext openContext) {
        if(!serverPlayerEntity.level().isClientSide()) {
            serverPlayerEntity.openMenu(new BackpackContainer(stack, serverPlayerEntity, openContext), buf -> saveExtraData(buf, null, openContext));
        }
    }

    //Item
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, OpenContext openContext, int index) {
        if(!serverPlayerEntity.level().isClientSide()) {
            serverPlayerEntity.openMenu(new BackpackContainer(stack, serverPlayerEntity, openContext, index, false), buf -> saveExtraData(buf, index, openContext));
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, OpenContext openContext) {
        if(!opener.level().isClientSide()) {
            synchroniseToOpener(opener, targetPlayer);
            opener.openMenu(new BackpackContainer(stack, targetPlayer, openContext), buf -> saveExtraData(buf, targetPlayer, openContext));
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) {
            AttachmentUtils.getAttachment(target).ifPresent(cap -> PacketDistributor.sendToPlayer(opener, new ClientboundSyncAttachmentPacket(target.getId(), cap.getBackpack())));
        }
    }
}