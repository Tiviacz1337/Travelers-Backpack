package com.tiviacz.travelersbackpackold.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtOps;

import java.util.Optional;

public class FluidTank extends SingleVariantStorage<FluidVariant>
{
    protected long capacity;

    public FluidTank(long capacity)
    {
        this(capacity, FluidVariant.blank(), 0);
    }

    public FluidTank(long capacity, FluidVariant variant, long amount)
    {
        this.capacity = capacity;
        this.variant = variant;
        this.amount = amount;
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

    public void setFluidVariant(FluidVariant variant, long amount)
    {
        this.variant = variant;
        this.amount = amount;
    }

    public NbtCompound writeToNbt(RegistryWrapper.WrapperLookup registryLookup, NbtCompound nbt)
    {
        nbt.put("variant", FluidVariant.CODEC.encode(this.variant, registryLookup.getOps(NbtOps.INSTANCE), nbt).result().orElse(new NbtCompound()));
        nbt.putLong("capacity", capacity);
        nbt.putLong("amount", amount);
        return nbt;
    }

    public FluidTank readNbt(RegistryWrapper.WrapperLookup registryLookup, NbtCompound nbt)
    {
        this.variant = readOptional(registryLookup, nbt.getCompound("variant"));
        this.capacity = nbt.contains("capacity", 3) ? nbt.getLong("capacity") : capacity;
        this.amount = nbt.getLong("amount");
        return this;
    }

    public static Optional<FluidVariant> read(RegistryWrapper.WrapperLookup provider, NbtElement tag)
    {
        return FluidVariant.CODEC.parse(provider.getOps(NbtOps.INSTANCE), tag).result();
    }

    public static FluidVariant readOptional(RegistryWrapper.WrapperLookup provider, NbtCompound tag)
    {
        return tag.isEmpty() ? FluidVariant.blank() : read(provider, tag).orElse(FluidVariant.blank());
    }
}