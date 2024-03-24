package com.tiviacz.travelersbackpack.capability.entity;

import net.minecraft.world.item.ItemStack;

public interface IEntityTravelersBackpack
{
    boolean hasWearable();

    ItemStack getWearable();

    void setWearable(ItemStack stack);

    void removeWearable();

    void synchronise();
}