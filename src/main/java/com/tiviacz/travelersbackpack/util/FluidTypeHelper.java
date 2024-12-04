package com.tiviacz.travelersbackpack.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;

public class FluidTypeHelper {

    public static Component getFluidVariantName(FluidVariant variant) {
        return FluidVariantAttributes.getName(variant);
    }
}
