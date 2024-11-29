package com.tiviacz.travelersbackpack.inventory;

import dev.architectury.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

public class FluidTank extends SingleVariantStorage<FluidVariant> {
    protected Predicate<FluidVariantWrapper> validator;
    protected FluidVariantWrapper fluidVariant = FluidVariantWrapper.blank();
    protected long capacity;

    public FluidTank(long capacity) {
        this(capacity, e -> true);
    }

    public FluidTank(long capacity, Predicate<FluidVariantWrapper> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidTank setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidVariantWrapper> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidVariantWrapper stack) {
        return validator.test(stack);
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return this.capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    public FluidVariantWrapper getFluid() {
        return fluidVariant;
    }

    public long getFluidAmount() {
        return fluidVariant.getAmount();
    }

    public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        fluidVariant = FluidVariantWrapper.parseOptional(lookupProvider, nbt.getCompound("Fluid"));
        return this;
    }

    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if (!fluidVariant.fluidVariant().isBlank()) {
            nbt.put("Fluid", fluidVariant.saveOptional(lookupProvider));
        }

        return nbt;
    }

    public int getTanks() {
        return 1;
    }

    public FluidVariantWrapper getFluidInTank(int tank) {
        return getFluid();
    }

    public long getTankCapacity(int tank) {
        return getCapacity();
    }

    public boolean isFluidValid(int tank, FluidVariantWrapper stack) {
        return isFluidValid(stack);
    }

    public long fill(FluidVariantWrapper resource, boolean simulate) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (simulate) {
            if (fluidVariant.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluidVariant.fluidVariant().isOf(resource.fluidVariant().getFluid())) { //#matches components
                return 0;
            }
            return Math.min(capacity - fluidVariant.getAmount(), resource.getAmount());
        }
        if (fluidVariant.isEmpty()) {
            fluidVariant = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluidVariant.getAmount();
        }
        if (!fluidVariant.fluidVariant().isOf(resource.fluidVariant().getFluid())) {//#matches components
            return 0;
        }
        long filled = capacity - fluidVariant.getAmount();

        if (resource.getAmount() < filled) {
            fluidVariant = fluidVariant.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluidVariant = fluidVariant.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    public FluidVariantWrapper drain(FluidVariantWrapper resource, boolean simulate) {
        if (resource.isEmpty() || !resource.fluidVariant().isOf(fluidVariant.fluidVariant().getFluid())) { //#matches components
            return FluidVariantWrapper.blank();
        }
        return drain(resource.getAmount(), simulate);
    }

    public FluidVariantWrapper drain(long maxDrain, boolean simulate) {
        long drained = maxDrain;
        if (fluidVariant.getAmount() < drained) {
            drained = fluidVariant.getAmount();
        }
        FluidVariantWrapper stack = fluidVariant.copyWithAmount(drained);
        if (!simulate && drained > 0) {
            fluidVariant = fluidVariant.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    protected void onContentsChanged() {}

    public void setFluid(FluidVariantWrapper stack) {
        this.fluidVariant = stack;
    }

    public boolean isEmpty() {
        return fluidVariant.isEmpty();
    }

    public long getSpace() {
        return Math.max(0, capacity - fluidVariant.getAmount());
    }
}

