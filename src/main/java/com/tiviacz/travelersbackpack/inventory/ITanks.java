package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface ITanks
{
    FluidTank getLeftTank();

    FluidTank getRightTank();

    void saveTanks(CompoundTag compound);

    void loadTanks(CompoundTag compound);

    boolean updateTankSlots();
}