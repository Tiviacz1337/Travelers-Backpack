package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.world.item.ItemStack;

public interface IMoveSelector {
    default boolean shiftClickToBackpack(ItemStack stack) {
        return NbtHelper.getOrDefault(stack, ModDataHelper.SHIFT_CLICK_TO_BACKPACK, false);
    }
}
