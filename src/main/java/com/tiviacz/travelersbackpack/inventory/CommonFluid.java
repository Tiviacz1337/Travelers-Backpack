package com.tiviacz.travelersbackpack.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;

public class CommonFluid {
    public static FluidVariantWrapper empty() {
        return FluidVariantWrapper.blank();
    }

    public static Component getFluidName(FluidVariantWrapper fluidStack) {
        return FluidVariantAttributes.getName(fluidStack.fluidVariant());
    }
}