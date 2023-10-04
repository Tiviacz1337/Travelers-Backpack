package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackUtils
{
    public static ItemStack decrStackSize(ITravelersBackpackInventory inventory, int index, int count)
    {
        return ItemStackUtils.getAndSplit(inventory.getFluidSlotsInventory(), index, count);
    }

    public static ItemStack getAndSplit(IItemHandler inventory, int index, int amount)
    {
        return index >= 0 && index < inventory.getSlots() && !inventory.getStackInSlot(index).isEmpty() && amount > 0 ? inventory.getStackInSlot(index).split(amount) : ItemStack.EMPTY;
    }

    public static boolean isSameItemSameTags(ItemStack stack1, ItemStack stack2)
    {
        return stack1.sameItemStackIgnoreDurability(stack2) && tagMatches(stack1, stack2);
    }

    public static boolean tagMatches(ItemStack stack1, ItemStack stack2)
    {
        if (stack1.isEmpty() && stack2.isEmpty()) {
            return true;
        } else if (!stack1.isEmpty() && !stack2.isEmpty()) {
            if (stack1.getTag() == null && stack2.getTag() != null) {
                return false;
            } else {

                CompoundNBT copy1 = stack1.getTag() == null ? null : stack1.getTag().copy();
                CompoundNBT copy2 = stack2.getTag() == null ? null : stack2.getTag().copy();

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

                return (stack1.getTag() == null || copy1.equals(copy2)) && stack1.areCapsCompatible(stack2);
            }
        } else {
            return false;
        }
    }

    public static boolean isEmpty(ItemStackHandler handler)
    {
        for(int i = 0; i < handler.getSlots(); i++)
        {
            if(!handler.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}