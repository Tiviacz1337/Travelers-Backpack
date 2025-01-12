package com.tiviacz.travelersbackpack.util;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.items.HoseItem;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            return pStack.isEmpty() && pOther.isEmpty() ? true : checkComponentsIgnoreDamage(pStack.getComponents(), pOther.getComponents());
        }
    }

    public static boolean checkComponentsIgnoreDamage(DataComponentMap map, DataComponentMap other) {
        map.keySet().removeIf(type -> type == DataComponents.DAMAGE);
        other.keySet().removeIf(type -> type == DataComponents.DAMAGE);
        return Objects.equals(map, other);
    }

    public static DataComponentMap createDataComponentMap(ItemStack serverDataHolder, DataComponentType... dataComponentTypes) {
        ItemStack serverDataHolderCopy = serverDataHolder.copy();
        serverDataHolderCopy = reduceSize(serverDataHolderCopy);
        DataComponentMap.Builder mapBuilder = DataComponentMap.builder();
        for(DataComponentType type : dataComponentTypes) {
            if(!serverDataHolderCopy.has(type)) continue;
            mapBuilder.set(type, serverDataHolderCopy.get(type));
        }
        return mapBuilder.build();
    }

    public static ItemStack reduceSize(ItemStack backpack) {
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER)) {
            backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
        }
        //Client needs only visual representation, no need to send the whole data
        if(backpackCopy.has(ModDataComponents.SLOTS)) {
            Slots slots = backpackCopy.get(ModDataComponents.SLOTS);
            List<Pair<Integer, Pair<ItemStack, Boolean>>> memorizedStacksHeavy = slots.memory();
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
            backpackCopy.set(ModDataComponents.SLOTS, new Slots(slots.unsortables(), reduced));
        }
        return backpackCopy;
    }
}