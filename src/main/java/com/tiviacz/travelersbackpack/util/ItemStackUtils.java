package com.tiviacz.travelersbackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemStackUtils
{
    public static ItemStack getAndSplit(IItemHandler inventory, int index, int amount)
    {
        return index >= 0 && index < inventory.getSlots() && !inventory.getStackInSlot(index).isEmpty() && amount > 0 ? inventory.getStackInSlot(index).split(amount) : ItemStack.EMPTY;
    }

    public static boolean isSameItemSameTags(ItemStack stack1, ItemStack stack2)
    {
        return stack1.sameItem(stack2) && ItemStack.tagMatches(stack1, stack2);
    }
}