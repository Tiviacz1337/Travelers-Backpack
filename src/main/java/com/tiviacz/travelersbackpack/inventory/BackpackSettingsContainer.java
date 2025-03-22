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

public record BackpackSettingsContainer(ItemStack stack, Player player, int screenID,
                                        int index) implements MenuProvider, Nameable {

    @Override
    public Component getName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    public static FriendlyByteBuf saveSettingsExtraData(FriendlyByteBuf buf, int screenID, int index) {
        buf.writeBoolean(false);
        buf.writeInt(screenID);
        buf.writeBlockPos(BlockPos.ZERO); //Not used
        buf.writeInt(index);
        return buf;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if(this.screenID == Reference.WEARABLE_SCREEN_ID) {
            return new BackpackSettingsMenu(pContainerId, pPlayerInventory, CapabilityUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackSettingsMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer, pPlayer.level(), this.index));
        }
    }

    public static void openSettings(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide) {
            NetworkHooks.openScreen(serverPlayerEntity, new BackpackSettingsContainer(stack, serverPlayerEntity, screenID, index), buf -> saveSettingsExtraData(buf, screenID, index));
        }
    }
}