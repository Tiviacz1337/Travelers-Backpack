package com.tiviacz.travelersbackpack.util;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.items.HoseItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
            return pStack.isEmpty() && pOther.isEmpty() || checkComponentsIgnoreDamage(pStack.hasTag() ? pStack.getTag() : new CompoundTag(), pOther.hasTag() ? pOther.getTag() : new CompoundTag());
        }
    }

    public static boolean checkComponentsIgnoreDamage(CompoundTag map, CompoundTag other) {
        CompoundTag mapCopy = map.copy();
        CompoundTag otherCopy = other.copy();
        mapCopy.remove("Damage");
        otherCopy.remove("Damage");
        return mapCopy.equals(otherCopy);
    }

    public static ItemStack reduceSize(ItemStack backpack) {
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.hasTag()) {
            backpackCopy.getTag().remove(ModDataHelper.BACKPACK_CONTAINER);
        }
        //Client needs only visual representation, no need to send the whole data
        if(backpackCopy.hasTag() && backpackCopy.getTag().contains(ModDataHelper.MEMORY_SLOTS)) {
            List<Pair<Integer, Pair<ItemStack, Boolean>>> memorizedStacksHeavy = NbtHelper.get(backpackCopy, ModDataHelper.MEMORY_SLOTS);
            List<Pair<Integer, Pair<ItemStack, Boolean>>> reduced = new ArrayList<>();

            for(Pair<Integer, Pair<ItemStack, Boolean>> outerPair : memorizedStacksHeavy) {
                int index = outerPair.getFirst();
                ItemStack innerStack = outerPair.getSecond().getFirst().copy();
                boolean matchComponents = outerPair.getSecond().getSecond();
                if(matchComponents) {
                    innerStack = new ItemStack(innerStack.getItem(), innerStack.getCount());
                }
                if(innerStack.isEmpty()) {
                    continue;
                }
                reduced.add(Pair.of(index, Pair.of(innerStack, matchComponents)));
            }
            NbtHelper.set(backpack, ModDataHelper.MEMORY_SLOTS, reduced);
        }
        return backpackCopy;
    }
}