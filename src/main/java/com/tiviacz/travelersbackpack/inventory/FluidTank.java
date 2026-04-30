package com.tiviacz.travelersbackpack.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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
        if(validator != null) {
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

    public long getViewCapacity() {
        return capacity / 81;
    }

    public FluidVariantWrapper getFluid() {
        return fluidVariant;
    }

    public long getFluidAmount() {
        return fluidVariant.getAmount();
    }

    public FluidTank readFromNBT(CompoundTag nbt) {
        fluidVariant = FluidVariantWrapper.parseOptional(nbt.getCompound("Fluid"));
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        if(!fluidVariant.fluidVariant().isBlank()) {
            nbt.put("Fluid", fluidVariant.saveOptional());
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
        return fill(resource, simulate, null);
    }

    //Transaction variant for proper saving
    public long fill(FluidVariantWrapper resource, boolean simulate, @Nullable TransactionContext transaction) {
        if(resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if(simulate) {
            if(fluidVariant.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if(!fluidVariant.fluidVariant().isOf(resource.fluidVariant().getFluid())) { //#matches components
                return 0;
            }
            return Math.min(capacity - fluidVariant.getAmount(), resource.getAmount());
        }
        if(fluidVariant.isEmpty()) {
            fluidVariant = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
            if(transaction == null) {
                onContentsChanged();
            } else {
                transaction.addOuterCloseCallback(result -> {
                    if(result.wasCommitted()) {
                        onContentsChanged();
                    }
                });
            }
            return fluidVariant.getAmount();
        }
        if(!fluidVariant.fluidVariant().isOf(resource.fluidVariant().getFluid())) {//#matches components
            return 0;
        }
        long filled = capacity - fluidVariant.getAmount();

        if(resource.getAmount() < filled) {
            fluidVariant = fluidVariant.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluidVariant = fluidVariant.setAmount(capacity);
        }
        if(filled > 0)
            if(transaction == null) {
                onContentsChanged();
            } else {
                transaction.addOuterCloseCallback(result -> {
                    if(result.wasCommitted()) {
                        onContentsChanged();
                    }
                });
            }
        return filled;
    }

    public FluidVariantWrapper drain(FluidVariantWrapper resource, boolean simulate) {
        if(resource.isEmpty() || !resource.fluidVariant().isOf(fluidVariant.fluidVariant().getFluid())) { //#matches components
            return FluidVariantWrapper.blank();
        }
        return drain(resource.getAmount(), simulate);
    }

    public FluidVariantWrapper drain(long maxDrain, boolean simulate) {
        return drain(maxDrain,  simulate, null);
    }

    //Transaction variant for proper saving
    public FluidVariantWrapper drain(long maxDrain, boolean simulate, @Nullable TransactionContext transaction) {
        long drained = maxDrain;
        if(fluidVariant.getAmount() < drained) {
            drained = fluidVariant.getAmount();
        }
        FluidVariantWrapper stack = fluidVariant.copyWithAmount(drained);
        if(!simulate && drained > 0) {
            fluidVariant = fluidVariant.shrink(drained);
            if(fluidVariant.amount() <= 0) {
                fluidVariant = FluidVariantWrapper.blank();
            }
            if(transaction == null) {
                onContentsChanged();
            } else {
                transaction.addOuterCloseCallback(result -> {
                    if(result.wasCommitted()) {
                        onContentsChanged();
                    }
                });
            }
        }
        return stack;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

        if((insertedVariant.equals(fluidVariant.fluidVariant()) || fluidVariant.fluidVariant().isBlank()) && canInsert(insertedVariant)) {
            long insertedAmount = Math.min(maxAmount, getCapacity(insertedVariant) - fluidVariant.amount());

            if(insertedAmount > 0) {
                updateSnapshots(transaction);
                fill(new FluidVariantWrapper(insertedVariant, insertedAmount), false, transaction);
                return insertedAmount;
            }
        }

        return 0;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);

        if(extractedVariant.equals(fluidVariant.fluidVariant()) && canExtract(extractedVariant)) {
            long extractedAmount = Math.min(maxAmount, fluidVariant.amount());

            if(extractedAmount > 0) {
                updateSnapshots(transaction);
                drain(extractedAmount, false, transaction);
                return extractedAmount;
            }
        }
        return 0;
    }

    protected void onContentsChanged() {
    }

    public void setFluid(FluidVariantWrapper stack) {
        this.fluidVariant = stack;
    }

    public boolean isEmpty() {
        return fluidVariant.isEmpty();
    }

    public long getSpace() {
        return Math.max(0, capacity - fluidVariant.getAmount());
    }

    @Override
    public boolean isResourceBlank() {
        return fluidVariant.fluidVariant().isBlank();
    }

    @Override
    public FluidVariant getResource() {
        return fluidVariant.fluidVariant();
    }

    @Override
    public long getAmount() {
        return fluidVariant.amount();
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot() {
        return new ResourceAmount<>(fluidVariant.fluidVariant(), fluidVariant.amount());
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
        fluidVariant = new FluidVariantWrapper(snapshot.resource(), snapshot.amount());
    }

    @Override
    public String toString() {
        return "SingleVariantStorage[%d %s]".formatted(fluidVariant.amount(), fluidVariant.fluidVariant());
    }

    //#TODO to be removed - only data transfer

    public FluidTank readNbtOld(CompoundTag nbt) {
        this.variant = readOptional(nbt.getCompound("variant"));
        this.capacity = nbt.contains("capacity", 3) ? nbt.getLong("capacity") : capacity;
        this.amount = nbt.getLong("amount");
        return this;
    }

    public static Optional<FluidVariant> read(CompoundTag tag) {
        return Optional.of(FluidVariantWrapper.parseOptional(tag).fluidVariant());
    }

    public static FluidVariant readOptional(CompoundTag tag) {
        return tag.isEmpty() ? FluidVariant.blank() : read(tag).orElse(FluidVariant.blank());
    }
}