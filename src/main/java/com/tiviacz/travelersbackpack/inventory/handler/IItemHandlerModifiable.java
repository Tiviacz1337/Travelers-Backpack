package com.tiviacz.travelersbackpack.inventory.handler;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemHandlerModifiable {
    int getSlots();

    @NotNull ItemStack getStackInSlot(int var1);

    @NotNull ItemStack insertItem(int var1, @NotNull ItemStack var2, boolean var3);

    @NotNull ItemStack extractItem(int var1, int var2, boolean var3);

    int getSlotLimit(int var1);

    boolean isItemValid(int var1, @NotNull ItemStack var2);

    void setStackInSlot(int slot, ItemStack stack);
}
