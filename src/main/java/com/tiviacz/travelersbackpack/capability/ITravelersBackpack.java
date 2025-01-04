package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ITravelersBackpack {
    boolean hasBackpack();

    ItemStack getBackpack();

    void updateBackpack(ItemStack stack);

    void applyComponents(CompoundTag compound);

    void equipBackpack(ItemStack stack);

    void removeWearable();

    void removeWrapper();

    void remove();

    BackpackWrapper getWrapper();

    void synchronise();

    void synchronise(CompoundTag compound);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);
}