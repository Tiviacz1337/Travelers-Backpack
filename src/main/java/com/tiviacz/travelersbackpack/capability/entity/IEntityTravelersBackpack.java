package com.tiviacz.travelersbackpack.capability.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IEntityTravelersBackpack
{
    boolean hasWearable();

    ItemStack getWearable();

    void setWearable(ItemStack stack);

    void removeWearable();

    void synchronise();

    CompoundNBT saveTag();

    void loadTag(CompoundNBT compoundTag);
}