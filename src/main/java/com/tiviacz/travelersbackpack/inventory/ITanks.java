package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public interface ITanks
{
    FluidTank getLeftTank();

    FluidTank getRightTank();

    void saveTanks(CompoundNBT compound);

    void loadTanks(CompoundNBT compound);

    boolean updateTankSlots();

    void markTankDirty();
}