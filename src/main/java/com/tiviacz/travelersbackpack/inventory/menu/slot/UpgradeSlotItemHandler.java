package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.IMoveSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class UpgradeSlotItemHandler<T extends UpgradeBase<?>> extends SlotItemHandler {
    private final T upgradeParent;

    public UpgradeSlotItemHandler(T upgradeParent, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.upgradeParent = upgradeParent;
    }

    @Override
    public boolean isActive() {
        return getUpgradeParent().isTabOpened();
    }

    public T getUpgradeParent() {
        return this.upgradeParent;
    }

    public boolean shiftClickToBackpack() {
        if(this.upgradeParent instanceof IMoveSelector) {
            return getUpgradeParent().getDataHolderStack().getOrDefault(ModDataComponents.SHIFT_CLICK_TO_BACKPACK, false);
        }
        return true;
    }
}