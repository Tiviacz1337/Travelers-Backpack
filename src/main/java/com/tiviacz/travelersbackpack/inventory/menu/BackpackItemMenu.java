package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class BackpackItemMenu extends BackpackBaseMenu {
    public BackpackItemMenu(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(windowID, playerInventory, createWrapper(playerInventory, data));
    }

    public BackpackItemMenu(int windowID, Inventory playerInventory, BackpackWrapper wrapper) {
        super(ModMenuTypes.BACKPACK_MENU.get(), windowID, playerInventory, wrapper);
        this.wrapper.addUser(playerInventory.player);
    }

    private static BackpackWrapper createWrapper(Inventory inventory, RegistryFriendlyByteBuf data) {
        Objects.requireNonNull(inventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        int screenID = data.readInt();
        int entityId = data.readInt();

        if(screenID == Reference.WEARABLE_SCREEN_ID) {
            if(entityId != -1) {
                BackpackWrapper targetWrapper = AttachmentUtils.getBackpackWrapper((Player)inventory.player.level().getEntity(entityId));
                targetWrapper.addUser(inventory.player);
                return targetWrapper;
            }
            return AttachmentUtils.getBackpackWrapper(inventory.player);
        } else {
            ItemStack backpackStack = entityId == -1 ? inventory.player.getItemInHand(InteractionHand.MAIN_HAND) : inventory.items.get(entityId);
            return new BackpackWrapper(backpackStack, screenID, data.registryAccess(), inventory.player, inventory.player.level(), entityId);
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if(getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && clickType == ClickType.SWAP) {
            ItemStack stack = player.getInventory().getItem(dragType);
            ItemStack currentItem = player.getInventory().getSelected();

            if(!currentItem.isEmpty() && stack == currentItem) {
                return;
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex) {
        int modifiedOffset = this.extendedScreenOffset;
        if(this.wrapper.isExtended()) {
            modifiedOffset += 18;
        }

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                if(x + y * 9 + 9 == currentItemIndex) {
                    this.addSlot(new DisabledSlot(inventory, x + y * 9 + 9, modifiedOffset + 8 + x * 18, (this.wrapper.getRows() * 18 + 7 + 25) + y * 18));
                    this.disabledSlotIndex = this.slots.size() - 1;
                } else {
                    this.addSlot(new Slot(inventory, x + y * 9 + 9, modifiedOffset + 8 + x * 18, (this.wrapper.getRows() * 18 + 7 + 25) + y * 18));
                }
            }
        }

        for(int x = 0; x < 9; x++) {
            if(x == currentItemIndex && wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                this.addSlot(new DisabledSlot(inventory, x, modifiedOffset + 8 + x * 18, this.wrapper.getRows() * 18 + 7 + 83));
                this.disabledSlotIndex = this.slots.size() - 1;
            } else {
                this.addSlot(new Slot(inventory, x, modifiedOffset + 8 + x * 18, this.wrapper.getRows() * 18 + 7 + 83));
            }
        }
    }

    @Override
    public void removed(Player player) {
        if(player.containerMenu instanceof BackpackBaseMenu && player.level().isClientSide) {
            return;
        }
        this.wrapper.playersUsing.remove(player);
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player) {
        if(getWrapper().getBackpackOwner() != null) {
            return getWrapper().getBackpackOwner().isAlive() && AttachmentUtils.isWearingBackpack(getWrapper().getBackpackOwner());
        }
        if(getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID) {
            ItemStack backpackStack = getWrapper().getBackpackSlotIndex() == -1 ? player.getItemInHand(InteractionHand.MAIN_HAND) : inventory.items.get(getWrapper().getBackpackSlotIndex());
            return backpackStack.getItem() instanceof TravelersBackpackItem;
        }
        return true;
    }
}
