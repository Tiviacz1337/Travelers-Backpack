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

public class BackpackSettingsContainer {
    public final ItemStack stack;
    public final Player player;
    public final int screenID;
    public final int index;

    public BackpackSettingsContainer(ItemStack stack, Player player, int screenID, int index) {
        this.stack = stack;
        this.player = player;
        this.screenID = screenID;
        this.index = index;
    }

    public static ModScreenHandlerTypes.SettingsScreenData saveSettingsExtraData(int screenID, int index) {
        return new ModScreenHandlerTypes.SettingsScreenData(false, screenID, BlockPos.ZERO, index);
    }

    public static void openSettings(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide) {
            serverPlayerEntity.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.SettingsScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public boolean shouldCloseCurrentScreen() {
                    return false;
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackSettingsMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackSettingsMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level(), index));
                    }
                }

                @Override
                public ModScreenHandlerTypes.SettingsScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveSettingsExtraData(screenID, index);
                }
            });
        }
    }
}