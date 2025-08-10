package com.tiviacz.travelersbackpack.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public record RenderInfo(CompoundTag compoundTag) {
    public static final RenderInfo EMPTY = new RenderInfo(new CompoundTag());

    public boolean isEmpty() {
        return this.compoundTag.isEmpty();
    }

    public boolean hasTanks() {
        return this.compoundTag.contains("LeftTank") || this.compoundTag.contains("RightTank");
    }

    public FluidStack getLeftFluidStack() {
        if(this.compoundTag.contains("LeftTank")) {
            return FluidStack.loadFluidStackFromNBT(this.compoundTag.getCompound("LeftTank"));
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getRightFluidStack() {
        if(this.compoundTag.contains("RightTank")) {
            return FluidStack.loadFluidStackFromNBT(this.compoundTag.getCompound("RightTank"));
        }
        return FluidStack.EMPTY;
    }

    public void updateCapacity(int capacity) {
        if(this.compoundTag.contains("Capacity")) {
            this.compoundTag.putInt("Capacity", capacity);
        }
    }

    public int getCapacity() {
        if(this.compoundTag.contains("Capacity")) {
            return this.compoundTag.getInt("Capacity");
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", new FluidStack(Fluids.WATER, 1).writeToNBT(new CompoundTag()));
        tag.put("RightTank", new FluidStack(Fluids.LAVA, 1).writeToNBT(new CompoundTag()));
        tag.putInt("Capacity", 1);
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
