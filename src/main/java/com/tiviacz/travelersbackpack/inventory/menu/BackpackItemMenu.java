package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.SlotPositioner;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class BackpackItemMenu extends BackpackBaseMenu {
    public BackpackItemMenu(int windowID, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowID, playerInventory, createWrapper(playerInventory, data));
    }

    public BackpackItemMenu(int windowID, Inventory playerInventory, BackpackWrapper wrapper) {
        super(ModMenuTypes.BACKPACK_MENU, windowID, playerInventory, wrapper);
        this.wrapper.addUser(playerInventory.player);
    }

    private static BackpackWrapper createWrapper(Inventory inventory, FriendlyByteBuf data) {
        Objects.requireNonNull(inventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        byte screenID = data.readByte();
        int entityId = data.readInt();

        if(screenID == Reference.WEARABLE_SCREEN_ID) {
            if(entityId != -1) {
                BackpackWrapper targetWrapper = ComponentUtils.getBackpackWrapper((Player)inventory.player.level().getEntity(entityId));
                targetWrapper.addUser(inventory.player);
                return targetWrapper;
            }
            return ComponentUtils.getBackpackWrapper(inventory.player);
        } else {
            //#TODO add opening from slot in inventory
            return new BackpackWrapper(inventory.player.getItemInHand(InteractionHand.MAIN_HAND), screenID, inventory.player, inventory.player.level());
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
        SlotPositioner pos = wrapper.getSlotPositioner();
        if(pos.isExtended()) {
            modifiedOffset += 18;
        }

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, modifiedOffset + 8 + x * 18, (pos.getRows() * 18 + 7 + 25) + y * 18));
            }
        }

        for(int x = 0; x < 9; x++) {
            if(x == currentItemIndex && wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
                this.addSlot(new DisabledSlot(inventory, x, modifiedOffset + 8 + x * 18, pos.getRows() * 18 + 7 + 83));
            } else {
                this.addSlot(new Slot(inventory, x, modifiedOffset + 8 + x * 18, pos.getRows() * 18 + 7 + 83));
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
            return getWrapper().getBackpackOwner().isAlive() && ComponentUtils.isWearingBackpack(getWrapper().getBackpackOwner());
        }
        return true;
    }
}
