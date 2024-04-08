package com.tiviacz.travelersbackpack.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.nbt.NbtCompound;

public class FluidTank extends SingleVariantStorage<FluidVariant>
{
    protected long capacity;

    public FluidTank(long capacity)
    {
        this.capacity = capacity;
    }

    @Override
    protected FluidVariant getBlankVariant()
    {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant)
    {
        return capacity;
    }

    public void setCapacity(long capacity)
    {
        this.capacity = capacity;
    }

    public NbtCompound writeToNbt(NbtCompound nbt)
    {
        nbt.put("variant", variant.toNbt());
        nbt.putLong("capacity", capacity);
        nbt.putLong("amount", amount);
        return nbt;
    }

    public FluidTank readNbt(NbtCompound nbt)
    {
        this.variant = FluidVariantImpl.fromNbt(nbt.getCompound("variant"));
        this.capacity = nbt.contains("capacity", 3) ? nbt.getLong("capacity") : capacity;
        this.amount = nbt.getLong("amount");
        return this;
    }

    public FluidTank readOldNbt(NbtCompound nbt, boolean leftTank)
    {
        this.capacity = nbt.contains("capacity", 3) ? nbt.getLong("capacity") : capacity;
        this.variant = FluidVariantImpl.fromNbt(nbt.getCompound(leftTank ? ITravelersBackpackInventory.LEFT_TANK : ITravelersBackpackInventory.RIGHT_TANK));
        this.amount = nbt.getLong(leftTank ? ITravelersBackpackInventory.LEFT_TANK_AMOUNT : ITravelersBackpackInventory.RIGHT_TANK_AMOUNT);
        //nbt.remove(leftTank ? ITravelersBackpackInventory.LEFT_TANK_AMOUNT : ITravelersBackpackInventory.RIGHT_TANK_AMOUNT);
        //nbt.remove(leftTank ? ITravelersBackpackInventory.LEFT_TANK : ITravelersBackpackInventory.RIGHT_TANK);
        return this;
    }
}