package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;

public interface HumanoidRenderStateBackpackInject {
    public void setBackpackStack(ItemStack stack);

    public ItemStack getBackpackStack();

    public void setChestItem(ItemStack stack);
}

