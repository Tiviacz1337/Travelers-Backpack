package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.upgrades.IMoveSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class UpgradeSlotItemHandler<T extends UpgradeBase<?>> extends SlotItemHandler {
    private final T upgradeParent;

    public UpgradeSlotItemHandler(T upgradeParent, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.upgradeParent = upgradeParent;
    }

    public T getUpgradeParent() {
        return this.upgradeParent;
    }

    public boolean shiftClickToBackpack() {
        if(this.upgradeParent instanceof IMoveSelector) {
            return NbtHelper.getOrDefault(getUpgradeParent().getDataHolderStack(), ModDataHelper.SHIFT_CLICK_TO_BACKPACK, false);
        }
        return true;
    }
}