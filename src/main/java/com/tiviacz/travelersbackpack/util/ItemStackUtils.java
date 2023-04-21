package com.tiviacz.travelersbackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemStackUtils
{
    public static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.isItemEqual(stack2) && areTagsEqual(stack1, stack2);
    }

    public static boolean areTagsEqual(ItemStack stack1, ItemStack stack2)
    {
        if (stack1.isEmpty() && stack2.isEmpty()) {
            return true;
        } else if (!stack1.isEmpty() && !stack2.isEmpty()) {
            if (stack1.getNbt() == null && stack2.getNbt() != null) {
                return false;
            } else {

                NbtCompound copy1 = stack1.getNbt() == null ? null : stack1.getNbt().copy();
                NbtCompound copy2 = stack2.getNbt() == null ? null : stack2.getNbt().copy();

                if(copy1 != null)
                {
                    if(copy1.contains("Damage"))
                    {
                        copy1.remove("Damage");
                    }
                }

                if(copy2 != null)
                {
                    if(copy2.contains("Damage"))
                    {
                        copy2.remove("Damage");
                    }
                }

                return (stack1.getNbt() == null || copy1.equals(copy2));
            }
        } else {
            return false;
        }
    }
}