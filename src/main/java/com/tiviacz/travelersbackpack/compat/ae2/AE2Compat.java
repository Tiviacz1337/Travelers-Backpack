package com.tiviacz.travelersbackpack.compat.ae2;

import appeng.items.tools.powered.PortableCellItem;
import net.minecraft.world.item.ItemStack;

public class AE2Compat {
    public static boolean isPortableCellItem(ItemStack stack) {
        if(stack.getItem() instanceof PortableCellItem) {
            return true;
        }
        return false;
    }
}
