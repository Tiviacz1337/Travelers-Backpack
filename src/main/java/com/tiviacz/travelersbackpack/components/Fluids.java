package com.tiviacz.travelersbackpack.components;

import net.minecraftforge.fluids.FluidStack;

public record Fluids(FluidStack leftFluidStack, FluidStack rightFluidStack) {
    public static Fluids empty() {
        return new Fluids(FluidStack.EMPTY, FluidStack.EMPTY);
    }
}
