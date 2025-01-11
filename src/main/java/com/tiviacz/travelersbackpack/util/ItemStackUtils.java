package com.tiviacz.travelersbackpack.util;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.item.HoseItem;
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
            return pStack.isEmpty() && pOther.isEmpty() ? true : checkComponentsIgnoreDamage(pStack.getPrototype(), pOther.getPrototype());
        }
    }

    public static boolean checkComponentsIgnoreDamage(DataComponentMap map, DataComponentMap other) {
        map.keySet().removeIf(type -> type == DataComponents.DAMAGE);
        other.keySet().removeIf(type -> type == DataComponents.DAMAGE);
        return Objects.equals(map, other);
    }

    public static DataComponentMap createDataComponentMap(ItemStack serverDataHolder, DataComponentType... dataComponentTypes) {
        DataComponentMap.Builder mapBuilder = DataComponentMap.builder();
        for(DataComponentType type : dataComponentTypes) {
            serverDataHolder = reduceSize(serverDataHolder);
            if(!serverDataHolder.has(type)) continue;
            mapBuilder.set(type, serverDataHolder.get(type));
        }
        return mapBuilder.build();
    }

    public static ItemStack reduceSize(ItemStack backpack) {
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER)) {
            backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
        }
        if(backpackCopy.has(ModDataComponents.SLOTS)) {
            Slots slots = backpackCopy.get(ModDataComponents.SLOTS);
            List<Pair<Integer, Pair<ItemStack, Boolean>>> smallerMemory = new ArrayList<>();
            List<Pair<Integer, Pair<ItemStack, Boolean>>> memory = slots.memory();
            for(Pair<Integer, Pair<ItemStack, Boolean>> pair : memory) {
                Integer slot = pair.getFirst();
                Pair<ItemStack, Boolean> pairInner = pair.getSecond();
                ItemStack smallerStack = pairInner.getFirst().getItem().getDefaultInstance();
                smallerMemory.add(Pair.of(slot, Pair.of(smallerStack, pairInner.getSecond())));
            }
            Slots smallerSlots = new Slots(slots.unsortables(), smallerMemory);
            backpackCopy.set(ModDataComponents.SLOTS, smallerSlots);
        }
        return backpackCopy;
    }
}