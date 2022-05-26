package com.tiviacz.travelersbackpack.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;

public interface ITanks
{
    SingleVariantStorage<FluidVariant> getLeftTank();

    SingleVariantStorage<FluidVariant> getRightTank();

    void writeTanks(NbtCompound compound);

    void readTanks(NbtCompound compound);

    boolean updateTankSlots();

    void markTankDirty();
}