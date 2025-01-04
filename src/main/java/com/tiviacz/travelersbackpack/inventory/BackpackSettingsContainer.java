package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
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

public class BackpackSettingsContainer implements MenuProvider, Nameable {
    public final ItemStack stack;
    public final Player player;
    public final byte screenID;

    public BackpackSettingsContainer(ItemStack stack, Player player, byte screenID) {
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

    public static FriendlyByteBuf saveSettingsExtraData(FriendlyByteBuf buf, byte screenID, ItemStack backpack) {
        buf.writeBoolean(false);
        buf.writeByte(screenID);
        //ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, backpack);
        buf.writeItem(backpack);
        buf.writeBlockPos(BlockPos.ZERO); //Not used
        return buf;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if(this.screenID == Reference.WEARABLE_SCREEN_ID) {
            return new BackpackSettingsMenu(pContainerId, pPlayerInventory, CapabilityUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackSettingsMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer, pPlayer.level()));
        }
    }

    public static void openSettings(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            NetworkHooks.openScreen(serverPlayerEntity, new BackpackSettingsContainer(stack, serverPlayerEntity, screenID), buf -> saveSettingsExtraData(buf, screenID, stack));
        }
    }
}