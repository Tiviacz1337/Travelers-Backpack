package com.tiviacz.travelersbackpack.inventory.handler;

import net.minecraft.world.item.ItemStack;

public interface IItemHandlerModifiable {
    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);

    boolean isItemValid(int slot, ItemStack stack);

    void setStackInSlot(int slot, ItemStack stack);
}
