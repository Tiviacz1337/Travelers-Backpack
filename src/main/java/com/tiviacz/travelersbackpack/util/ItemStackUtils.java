package com.tiviacz.travelersbackpack.util;

import net.minecraft.item.ItemStack;

public class ItemStackUtils
{
    public static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.isItemEqualIgnoreDamage(stack2) && ItemStack.areTagsEqual(stack1, stack2);
    }
}