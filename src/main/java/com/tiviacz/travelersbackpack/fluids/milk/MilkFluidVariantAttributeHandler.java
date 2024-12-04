package com.tiviacz.travelersbackpack.fluids.milk;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;

public class MilkFluidVariantAttributeHandler implements FluidVariantAttributeHandler {
    @Override
    public Component getName(FluidVariant fluidVariant) {
        return Component.translatable("fluid.travelersbackpack.milk");
    }
}