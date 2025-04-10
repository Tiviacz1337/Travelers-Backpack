package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public interface IMoveSelector {
    default boolean shiftClickToBackpack(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.SHIFT_CLICK_TO_BACKPACK, false);
    }
}