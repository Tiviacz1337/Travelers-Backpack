package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
            return new BackpackItemMenu(pContainerId, pPlayerInventory, AttachmentUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer.registryAccess(), pPlayer, pPlayer.level()));
        }
    }

    public static RegistryFriendlyByteBuf saveExtraData(RegistryFriendlyByteBuf buf, @Nullable Player target, ItemStack stack, byte screenID) {
        buf.writeByte(screenID);
        buf.writeInt(target == null ? -1 : target.getId());
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        return buf;
    }

    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            serverPlayerEntity.openMenu(new BackpackContainer(stack, serverPlayerEntity, screenID), buf -> saveExtraData(new RegistryFriendlyByteBuf(buf, serverPlayerEntity.registryAccess()), null, stack, screenID));
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, byte screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);
            opener.openMenu(new BackpackContainer(stack, targetPlayer, screenID), buf -> saveExtraData(new RegistryFriendlyByteBuf(buf, opener.registryAccess()), targetPlayer, stack, screenID));
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) {
            AttachmentUtils.getCapability(target).ifPresent(cap -> {
                //TravelersBackpack.NETWORK.send(new ClientboundSyncCapabilityPacket(target.getId(), cap.getBackpack()), PacketDistributor.PLAYER.with(opener));
                PacketDistributorHelper.sendToPlayer(opener, new ClientboundSyncCapabilityPacket(target.getId(), cap.getBackpack()));
            });
        }
    }
}