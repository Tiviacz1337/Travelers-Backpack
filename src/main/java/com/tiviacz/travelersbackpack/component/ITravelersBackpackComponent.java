package com.tiviacz.travelersbackpack.component;

import net.minecraft.world.item.ItemStack;

public interface ITravelersBackpackComponent {
    /**
     * Compatibility only methods
     *
     * @return
     */

    ItemStack getWearable();

    void removeWearable();
}
