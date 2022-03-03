package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ContainerUtils
{
    public static ItemStack removeItem(IItemHandler handler, int slot, int amount)
    {
        return slot >= 0 && slot < handler.getSlots() && !(handler.getStackInSlot(slot)).isEmpty() && amount > 0 ? (handler.getStackInSlot(slot)).split(amount) : ItemStack.EMPTY;
    }

    public static ItemStack takeItem(IItemHandler handler, int slot)
    {
        return slot >= 0 && slot < handler.getSlots() ? handler.insertItem(slot, ItemStack.EMPTY, false) : ItemStack.EMPTY;
    }
}
