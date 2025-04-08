package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

public class CommonFluid {
    public static FluidStack empty() {
        return FluidStack.EMPTY;
    }

    public static Component getFluidName(FluidStack fluidStack) {
        return Component.translatable(fluidStack.getDescriptionId());
    }
}