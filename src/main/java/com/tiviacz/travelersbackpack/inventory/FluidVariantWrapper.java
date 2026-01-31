package com.tiviacz.travelersbackpack.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public record FluidVariantWrapper(FluidVariant fluidVariant, long amount) {
    public static Optional<FluidVariantWrapper> parse(CompoundTag tag) {
        FluidVariant fluidVariant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        long amount = tag.getLong("amount");
        return Optional.of(new FluidVariantWrapper(fluidVariant, amount));
    }

    public static FluidVariantWrapper parseOptional(CompoundTag tag) {
        return parse(tag).isPresent() ? parse(tag).get() : blank();
    }

    public Optional<Tag> save() {
        CompoundTag tag = new CompoundTag();
        FluidVariantImpl impl = new FluidVariantImpl(this.fluidVariant.getFluid(), this.fluidVariant.getNbt());
        tag.put("fluidVariant", impl.toNbt());
        tag.putLong("amount", this.amount);
        return Optional.of(tag);
    }

    public Tag saveOptional() {
        return save().isPresent() ? save().get() : new CompoundTag();
    }

    public boolean isEmpty() {
        return fluidVariant.isBlank() || amount <= 0;
    }

    public long getAmount() {
        return amount;
    }

    public long getViewAmount() {
        return amount / 81;
    }

    public static FluidVariantWrapper blank() {
        return new FluidVariantWrapper(FluidVariant.blank(), 0);
    }

    public FluidVariantWrapper copyWithAmount(long amount) {
        if(this.isEmpty()) {
            return blank();
        } else {
            if(amount <= 0) {
                return blank();
            }
            FluidVariantWrapper fluidVariant = this.copy();
            fluidVariant.setAmount(amount);
            return fluidVariant;
        }
    }

    public FluidVariantWrapper copy() {
        if(this.isEmpty()) {
            return blank();
        } else {
            return new FluidVariantWrapper(this.fluidVariant, this.amount);
        }
    }

    public FluidVariantWrapper setAmount(long amount) {
        return new FluidVariantWrapper(this.fluidVariant, amount);
    }

    public FluidVariantWrapper grow(long addedAmount) {
        return this.setAmount(this.getAmount() + addedAmount);
    }

    public FluidVariantWrapper shrink(long removedAmount) {
        return this.grow(-removedAmount);
    }
}
