package com.tiviacz.travelersbackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemStackUtils
{
    public static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.isItemEqualIgnoreDamage(stack2) && areTagsEqual(stack1, stack2);
    }

    public static boolean areTagsEqual(ItemStack stack1, ItemStack stack2)
    {
        if (stack1.isEmpty() && stack2.isEmpty()) {
            return true;
        } else if (!stack1.isEmpty() && !stack2.isEmpty()) {
            if (stack1.getTag() == null && stack2.getTag() != null) {
                return false;
            } else {

                NbtCompound copy1 = stack1.getTag() == null ? null : stack1.getTag().copy();
                NbtCompound copy2 = stack2.getTag() == null ? null : stack2.getTag().copy();

                if(copy1.contains("Damage"))
                {
                    copy1.remove("Damage");
                }
                if(copy2.contains("Damage"))
                {
                    copy2.remove("Damage");
                }

                return (stack1.getTag() == null || copy1.equals(copy2));
            }
        } else {
            return false;
        }
    }
}