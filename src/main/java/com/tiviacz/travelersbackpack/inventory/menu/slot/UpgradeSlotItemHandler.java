package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class UpgradeSlotItemHandler<T extends UpgradeBase<?>> extends ResourceHandlerSlot implements ShiftDirectionProvider<T> {
    private final T upgradeParent;

    public UpgradeSlotItemHandler(T upgradeParent, ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        this.upgradeParent = upgradeParent;
    }

    @Override
    public T getUpgradeParent() {
        return this.upgradeParent;
    }
}