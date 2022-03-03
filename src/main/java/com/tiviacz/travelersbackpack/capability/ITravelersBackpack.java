package com.tiviacz.travelersbackpack.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITravelersBackpack
{
    boolean hasWearable();

    ItemStack getWearable();

    void setWearable(ItemStack stack);

    void removeWearable();

    void synchronise();

    void synchroniseToOthers(Player player);

    CompoundTag saveTag();

    void loadTag(CompoundTag compoundTag);
}
