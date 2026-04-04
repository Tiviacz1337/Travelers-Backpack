package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.IMoveSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class UpgradeSlotItemHandler<T extends UpgradeBase<?>> extends ResourceHandlerSlot {
    private final T upgradeParent;

    public UpgradeSlotItemHandler(T upgradeParent, ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        this.upgradeParent = upgradeParent;
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