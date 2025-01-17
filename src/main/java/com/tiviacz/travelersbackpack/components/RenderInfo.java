package com.tiviacz.travelersbackpack.components;

import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;

public record RenderInfo(CompoundTag compoundTag) {
    public static final RenderInfo EMPTY = new RenderInfo(new CompoundTag());

    public boolean isEmpty() {
        return this.compoundTag.isEmpty();
    }

    public boolean hasTanks() {
        if(this.compoundTag.contains("LeftTank") || this.compoundTag.contains("RightTank")) {
            return true;
        }
        return false;
    }

    public FluidVariantWrapper getLeftFluidStack() {
        if(this.compoundTag.contains("LeftTank")) {
            return FluidVariantWrapper.parseOptional(this.compoundTag.getCompound("LeftTank"));
        }
        return FluidVariantWrapper.blank();
    }

    public FluidVariantWrapper getRightFluidStack() {
        if(this.compoundTag.contains("RightTank")) {
            return FluidVariantWrapper.parseOptional(this.compoundTag.getCompound("RightTank"));
        }
        return FluidVariantWrapper.blank();
    }

    public void updateCapacity(long capacity) {
        if(this.compoundTag.contains("Capacity")) {
            this.compoundTag.putLong("Capacity", capacity);
        }
    }

    public long getCapacity() {
        if(this.compoundTag.contains("Capacity")) {
            return this.compoundTag.getLong("Capacity");
        }
        return 0;
    }

    public static RenderInfo createCreativeTabInfo() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", new FluidVariantWrapper(FluidVariant.of(Fluids.WATER), 1).saveOptional());
        tag.put("RightTank", new FluidVariantWrapper(FluidVariant.of(Fluids.LAVA), 1).saveOptional());
        tag.putLong("Capacity", 1);
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
