package com.tiviacz.travelersbackpackold.util;

import com.tiviacz.travelersbackpackold.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpackold.items.HoseItem;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class ItemStackUtils
{
    public static ItemStack decrStackSize(ITravelersBackpackInventory inventory, int index, int count)
    {
        return Inventories.splitStack(inventory.getFluidSlotsInventory().getStacks(), index, count);
    }

    public static boolean isSameItemSameComponents(ItemStack pStack, ItemStack pOther)
    {
        //Hose patch
        if(pStack.getItem() instanceof HoseItem && pStack.isOf(pOther.getItem())) return true;

        if (!pStack.isOf(pOther.getItem())) {
            return false;
        }
        if (pStack.isEmpty() && pOther.isEmpty()) {
            return true;
        }
        return checkComponentsIgnoreDamage(pStack.getDefaultComponents(), pOther.getDefaultComponents());
    }

    public static boolean checkComponentsIgnoreDamage(ComponentMap map, ComponentMap other)
    {
        map.getTypes().removeIf(type -> type == DataComponentTypes.DAMAGE);
        other.getTypes().removeIf(type -> type == DataComponentTypes.DAMAGE);
        return Objects.equals(map, other);
    }
}