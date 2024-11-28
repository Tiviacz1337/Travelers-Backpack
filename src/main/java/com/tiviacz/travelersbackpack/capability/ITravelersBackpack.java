package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ITravelersBackpack {
    boolean hasBackpack();

    ItemStack getBackpack();

    void updateBackpack(ItemStack stack);

    void applyComponents(DataComponentMap map);

    void equipBackpack(ItemStack stack);

    void removeWearable();

    void removeWrapper();

    void remove();

    BackpackWrapper getWrapper();

    void synchronise();

    void synchronise(DataComponentMap map);

    CompoundTag serializeNBT(HolderLookup.Provider registryAccess);

    void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt);
}