package com.tiviacz.travelersbackpack.components;

import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;

public record Fluids(FluidVariantWrapper leftFluidStack, FluidVariantWrapper rightFluidStack) {
    public static Fluids empty() {
        return new Fluids(FluidVariantWrapper.blank(), FluidVariantWrapper.blank());
    }
}
