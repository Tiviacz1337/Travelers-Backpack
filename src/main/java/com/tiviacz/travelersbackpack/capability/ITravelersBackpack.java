package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITravelersBackpack
{
    boolean hasWearable();

    ItemStack getWearable();

    void setWearable(ItemStack stack);

    void removeWearable();

    TravelersBackpackContainer getContainer();

    void setContents(ItemStack stack);

    void synchronise();

    void synchroniseToOthers(Player player);

    CompoundTag saveTag();

    void loadTag(CompoundTag compoundTag);
}