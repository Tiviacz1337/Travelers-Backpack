package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;

public class UpgradeSlotItemHandler<T extends UpgradeBase<?>> extends SlotItemHandler implements ShiftDirectionProvider<T>{
    private final T upgradeParent;

    public UpgradeSlotItemHandler(T upgradeParent, ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.upgradeParent = upgradeParent;
    }

    @Override
    public T getUpgradeParent() {
        return this.upgradeParent;
    }
}