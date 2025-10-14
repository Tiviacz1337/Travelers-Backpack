package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;

public interface HumanoidRenderStateBackpackInject {
    void setBackpackStack(ItemStack stack);

    ItemStack getBackpackStack();

    void setChestItem(ItemStack stack);
}

