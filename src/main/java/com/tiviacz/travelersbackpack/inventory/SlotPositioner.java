package com.tiviacz.travelersbackpack.inventory;

public class SlotPositioner {
    private final int storageSlotCount;
    private final boolean isWider;

    public SlotPositioner(int storageSlotCount) {
        this.storageSlotCount = storageSlotCount;
        this.isWider = storageSlotCount > 81;
    }

    public int getSlotsInRow() {
        if (isWider) {
            return 11;
        }
        return 9;
    }

    public int getFullRows() {
        return storageSlotCount / getSlotsInRow();
    }

    public int getLastRowSlots() {
        return this.storageSlotCount - (getSlotsInRow() * getFullRows());
    }

    public int getRows() {
        return (int) Math.ceil((double) this.storageSlotCount / getSlotsInRow());
    }

    public boolean isExtended() {
        return this.isWider;
    }
}