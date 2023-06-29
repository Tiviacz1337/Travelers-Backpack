package com.tiviacz.travelersbackpack.fluids.milk;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class MilkFluidVariantAttributeHandler implements FluidVariantAttributeHandler
{
    @Override
    public Text getName(FluidVariant fluidVariant)
    {
        return new TranslatableText("fluid.travelersbackpack.milk");
    }
}