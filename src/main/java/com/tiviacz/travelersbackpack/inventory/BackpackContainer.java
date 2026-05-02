package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.component.TravelersBackpackComponent;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentProvider;

public class BackpackContainer {
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

    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(@Nullable Player target, int screenID) {
        return new ModScreenHandlerTypes.ItemScreenData(screenID, target == null ? -1 : target.getId());
    }

    public static ModScreenHandlerTypes.ItemScreenData saveExtraData(int index, int screenID) {
        return new ModScreenHandlerTypes.ItemScreenData(screenID, index);
    }

    //Component
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID) {
        if(!serverPlayerEntity.level().isClientSide) {
            serverPlayerEntity.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(null, screenID);
                }
            });
        }
    }

    //Item
    public static void openBackpack(ServerPlayer serverPlayerEntity, ItemStack stack, int screenID, int index) {
        if(!serverPlayerEntity.level().isClientSide) {
            serverPlayerEntity.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.ItemScreenData>() {
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
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(player));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level(), index));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(index, screenID);
                }
            });
        }
    }

    public static void openAnotherPlayerBackpack(ServerPlayer opener, ServerPlayer targetPlayer, ItemStack stack, int screenID) {
        if(!opener.level().isClientSide) {
            synchroniseToOpener(opener, targetPlayer);
            opener.openMenu(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.ItemScreenData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(screenID == Reference.WEARABLE_SCREEN_ID) {
                        return new BackpackItemMenu(i, inventory, ComponentUtils.getBackpackWrapper(targetPlayer));
                    } else {
                        return new BackpackItemMenu(i, inventory, new BackpackWrapper(stack, screenID, player, player.level()));
                    }
                }

                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayer player) {
                    return saveExtraData(targetPlayer, screenID);
                }
            });
        }
    }

    public static void synchroniseToOpener(ServerPlayer opener, ServerPlayer target) {
        if(opener != null) { //Sync data from target to opener
            ComponentUtils.WEARABLE.syncWith(opener, (ComponentProvider)target, (buf, rec) -> ((TravelersBackpackComponent)ComponentUtils.WEARABLE.get(target)).writeSyncPacket(ComponentUtils.getWearingBackpack(target), buf, rec, false), p -> true);
        }
    }
}