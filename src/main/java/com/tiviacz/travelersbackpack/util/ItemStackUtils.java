package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpackneo.items.HoseItem;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

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
            if(!serverDataHolder.has(type)) continue;
            mapBuilder.set(type, serverDataHolder.get(type));
        }
        return mapBuilder.build();
    }
}