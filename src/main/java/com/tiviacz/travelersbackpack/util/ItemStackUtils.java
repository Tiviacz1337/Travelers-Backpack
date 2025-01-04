package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.items.HoseItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackUtils {
    public static boolean isSameItemSameTags(ItemStack stack1, ItemStack stack2) {
        //Hose patch
        if(stack1.getItem() instanceof HoseItem && stack1.is(stack2.getItem())) return true;

        return isSameItemSameComponents(stack1, stack2);
    }

    public static boolean isSameItemSameComponents(ItemStack pStack, ItemStack pOther) {
        if(!pStack.is(pOther.getItem())) {
            return false;
        } else {
            return pStack.isEmpty() && pOther.isEmpty() ? true : checkComponentsIgnoreDamage(pStack.hasTag() ? pStack.getTag() : new CompoundTag(), pOther.hasTag() ? pOther.getTag() : new CompoundTag());
        }
    }

    public static boolean checkComponentsIgnoreDamage(CompoundTag map, CompoundTag other) {
        CompoundTag mapCopy = map.copy();
        CompoundTag otherCopy = other.copy();
        mapCopy.remove("Damage");
        otherCopy.remove("Damage");
        return mapCopy.equals(otherCopy);
    }
}