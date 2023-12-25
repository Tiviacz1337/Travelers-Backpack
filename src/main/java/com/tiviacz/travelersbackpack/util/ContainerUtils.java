package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ContainerUtils
{
    public static ItemStack removeItem(ITravelersBackpackContainer container, int index, int count)
    {
        return ContainerUtils.removeItem(container.getFluidSlotsHandler(), index, count);
    }

    public static ItemStack removeItem(IItemHandler handler, int slot, int amount)
    {
        return slot >= 0 && slot < handler.getSlots() && !(handler.getStackInSlot(slot)).isEmpty() && amount > 0 ? (handler.getStackInSlot(slot)).split(amount) : ItemStack.EMPTY;
    }

    public static ItemStack takeItem(IItemHandler handler, int slot)
    {
        return slot >= 0 && slot < handler.getSlots() ? handler.insertItem(slot, ItemStack.EMPTY, false) : ItemStack.EMPTY;
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