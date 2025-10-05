package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

public interface IFilterSlots {
    int getFilterSlotCount();

    int getSlotsInRow();

    default int getRows() {
        return (int)Math.ceil((double)getFilterSlotCount() / (double)getSlotsInRow());
    }

    default int getSlotsInRow(int row) {
        return Math.min(getFilterSlotCount() - row * getSlotsInRow(), getSlotsInRow());
    }
}