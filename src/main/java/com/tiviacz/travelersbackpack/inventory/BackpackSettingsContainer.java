package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record BackpackSettingsContainer(ItemStack stack, Player player, byte screenID, int index) implements ExtendedScreenHandlerFactory<ModScreenHandlerTypes.SettingsScreenData> {
    @Override
    public ModScreenHandlerTypes.SettingsScreenData getScreenOpeningData(ServerPlayer serverPlayer) {
        return new ModScreenHandlerTypes.SettingsScreenData(false, screenID, BlockPos.ZERO, index);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if(screenID == Reference.WEARABLE_SCREEN_ID) {
            return new BackpackSettingsMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
        } else {
            return new BackpackSettingsMenu(i, inventory, new BackpackWrapper(stack, screenID, player.registryAccess(), player, player.level(), index));
        }
    }

    public static void openSettings(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide) {
            serverPlayerEntity.openMenu(new BackpackSettingsContainer(stack, serverPlayerEntity, screenID, index));
        }
    }
}