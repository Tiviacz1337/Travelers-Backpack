package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
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
            return new BackpackItemMenu(pContainerId, pPlayerInventory, CapabilityUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackItemMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer, pPlayer.level()));
        }
    }

    public static FriendlyByteBuf saveExtraData(FriendlyByteBuf buf, @Nullable Player target, ItemStack stack, byte screenID) {
        buf.writeByte(screenID);
        buf.writeInt(target == null ? -1 : target.getId());
        //Not needed + heavy data
        ItemStack backpackCopy = stack.copy();
        if(backpackCopy.getTag() != null) {
            backpackCopy.getTag().remove(ModDataHelper.BACKPACK_CONTAINER);
            backpackCopy.getTag().remove(ModDataHelper.TOOLS_CONTAINER);
            backpackCopy.getTag().remove(ModDataHelper.UPGRADES);
        }
        buf.writeItem(backpackCopy);
        return buf;
    }

    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            NetworkHooks.openScreen(serverPlayerEntity, new BackpackContainer(stack, serverPlayerEntity, screenID), buf -> saveExtraData(buf, null, stack, screenID));
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, byte screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);
            NetworkHooks.openScreen(opener, new BackpackContainer(stack, targetPlayer, screenID), buf -> saveExtraData(buf, targetPlayer, stack, screenID));
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