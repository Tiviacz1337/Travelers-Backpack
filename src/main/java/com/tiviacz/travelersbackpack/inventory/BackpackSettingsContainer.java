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

public class BackpackSettingsContainer { //implements MenuProvider, Nameable, ExtendedScreenHandlerFactory<ModScreenHandlerTypes.SettingsScreenData> {
    public final ItemStack stack;
    public final Player player;
    public final byte screenID;

    public BackpackSettingsContainer(ItemStack stack, Player player, byte screenID) {
        this.stack = stack;
        this.player = player;
        this.screenID = screenID;
    }

    // @Override
    public Component getName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    //@Override
    public Component getDisplayName() {
        return Component.translatable("screen.travelersbackpack.item");
    }

    public static ModScreenHandlerTypes.SettingsScreenData saveSettingsExtraData(byte screenID, ItemStack backpack) {
        return new ModScreenHandlerTypes.SettingsScreenData(false, screenID, BlockPos.ZERO);
    }

    @Nullable
    //@Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if(this.screenID == Reference.WEARABLE_SCREEN_ID) {
            return new BackpackSettingsMenu(pContainerId, pPlayerInventory, ComponentUtils.getBackpackWrapper(this.player));
        } else {
            return new BackpackSettingsMenu(pContainerId, pPlayerInventory, new BackpackWrapper(this.stack, this.screenID, pPlayer.registryAccess(), pPlayer, pPlayer.level()));
        }
    }

    public static void openSettings(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            //serverPlayerEntity.openMenu(new BackpackSettingsContainer(stack, serverPlayerEntity, screenID), saveSettingsExtraData(screenID, stack));

            serverPlayerEntity.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.SettingsScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackSettingsMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackSettingsMenu(i, inventory, new BackpackWrapper(stack, screenID, player.registryAccess(), player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.SettingsScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveSettingsExtraData(screenID, stack);
                }
            });
        }
    }
}