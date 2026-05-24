package com.tiviacz.travelersbackpack.components;

import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;

public record RenderInfo(CompoundTag compoundTag) {
    public static final RenderInfo EMPTY = new RenderInfo(new CompoundTag());

    public static final String LEFT_TANK = "LeftTank";
    public static final String RIGHT_TANK = "RightTank";
    public static final String CAPACITY = "Capacity";
    public static final String LANTERN = "Lantern";

    public boolean isEmpty() {
        return this.compoundTag.isEmpty();
    }

    public boolean hasTanks() {
        return this.compoundTag.contains(LEFT_TANK) || this.compoundTag.contains(RIGHT_TANK);
    }

    public boolean hasLantern() {
        return this.compoundTag.contains(LANTERN);
    }

    public FluidVariantWrapper getLeftFluidStack() {
        if(this.compoundTag.contains(LEFT_TANK)) {
            return FluidVariantWrapper.parseOptional(this.compoundTag.getCompound(LEFT_TANK));
        }
        return FluidVariantWrapper.blank();
    }

    public FluidVariantWrapper getRightFluidStack() {
        if(this.compoundTag.contains(RIGHT_TANK)) {
            return FluidVariantWrapper.parseOptional(this.compoundTag.getCompound(RIGHT_TANK));
        }
        return FluidVariantWrapper.blank();
    }

    public void updateCapacity(long capacity) {
        if(this.compoundTag.contains(CAPACITY)) {
            this.compoundTag.putLong(CAPACITY, capacity);
        }
    }

    public long getCapacity() {
        if(this.compoundTag.contains(CAPACITY)) {
            return this.compoundTag.getLong(CAPACITY);
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put(LEFT_TANK, new FluidVariantWrapper(FluidVariant.of(Fluids.WATER), 1).saveOptional());
        tag.put(RIGHT_TANK, new FluidVariantWrapper(FluidVariant.of(Fluids.LAVA), 1).saveOptional());
        tag.putLong(CAPACITY, 1);
        return new RenderInfo(tag);
    }

    @Override
    public boolean equals(Object pOther) {
        if(this == pOther) {
            return true;
        } else {
            if(pOther instanceof RenderInfo renderInfo && this.compoundTag.toString().equals(renderInfo.compoundTag.toString())) {
                return true;
            }
            return false;
        }
    }
}
