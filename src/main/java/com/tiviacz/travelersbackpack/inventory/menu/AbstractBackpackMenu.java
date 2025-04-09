package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public abstract class AbstractBackpackMenu extends AbstractContainerMenu {
    public final Player player;
    protected final Inventory inventory;
    protected final BackpackWrapper wrapper;
    public int disabledSlotIndex = -1;

    public int extendedScreenOffset = 0;

    public int BACKPACK_INV_START = 0, BACKPACK_INV_END;
    public int PLAYER_INV_START, PLAYER_HOT_END;

    protected AbstractBackpackMenu(MenuType<?> type, int windowID, Inventory inventory, BackpackWrapper wrapper) {
        super(type, windowID);
        this.inventory = inventory;
        this.player = inventory.player;
        this.wrapper = wrapper;
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public Inventory getPlayerInventory() {
        return this.inventory;
    }

    public void addBackpackStorageSlots(BackpackWrapper wrapper) {
        int slot = 0;

        for(int i = 0; i < wrapper.getRows(); i++) {
            for(int j = 0; j < wrapper.getSlotsInRow(); j++) {
                if(slot >= wrapper.getStorage().getSlots()) break;
                this.addSlot(new BackpackSlotItemHandler(wrapper.getStorage(), slot, this.extendedScreenOffset + 8 + j * 18, 18 + i * 18));
                slot++;
            }
        }
    }
}