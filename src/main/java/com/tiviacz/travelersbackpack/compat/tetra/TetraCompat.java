package com.tiviacz.travelersbackpack.compat.tetra;

import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;

public class TetraCompat {
    public static boolean isTetraTool(ItemStack stack) {
        return stack.getItem() instanceof ModularItem;
    }
}