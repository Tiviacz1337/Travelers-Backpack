package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.nbt.NbtCompound;

public interface ITanks
{
    FluidTank getLeftTank();

    FluidTank getRightTank();

    void writeTanks(NbtCompound compound);

    void readTanks(NbtCompound compound);

    boolean updateTankSlots();
}