package com.tiviacz.travelersbackpack.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public record RenderInfo(CompoundTag compoundTag) {
    public static final RenderInfo EMPTY = new RenderInfo(new CompoundTag());

    public static final String LEFT_TANK = "LeftTank";
    public static final String RIGHT_TANK = "RightTank";
    public static final String CAPACITY = "Capacity";

    public boolean isEmpty() {
        return this.compoundTag.isEmpty();
    }

    public boolean hasTanks() {
        return this.compoundTag.contains(LEFT_TANK) || this.compoundTag.contains(RIGHT_TANK);
    }

    public FluidStack getLeftFluidStack() {
        if(this.compoundTag.contains(LEFT_TANK)) {
            return FluidStack.loadFluidStackFromNBT(this.compoundTag.getCompound(LEFT_TANK));
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getRightFluidStack() {
        if(this.compoundTag.contains(RIGHT_TANK)) {
            return FluidStack.loadFluidStackFromNBT(this.compoundTag.getCompound(RIGHT_TANK));
        }
        return FluidStack.EMPTY;
    }

    public void updateCapacity(int capacity) {
        if(this.compoundTag.contains(CAPACITY)) {
            this.compoundTag.putInt(CAPACITY, capacity);
        }
    }

    public int getCapacity() {
        if(this.compoundTag.contains(CAPACITY)) {
            return this.compoundTag.getInt(CAPACITY);
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put(LEFT_TANK, new FluidStack(Fluids.WATER, 1).writeToNBT(new CompoundTag()));
        tag.put(RIGHT_TANK, new FluidStack(Fluids.LAVA, 1).writeToNBT(new CompoundTag()));
        tag.putInt(CAPACITY, 1);
        return new RenderInfo(tag);
    }

    @Override
    public boolean equals(Object pOther) {
        if(this == pOther) {
            return true;
        } else {
            return pOther instanceof RenderInfo renderInfo && this.compoundTag.toString().equals(renderInfo.compoundTag.toString());
        }
    }
}
