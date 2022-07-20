package com.tiviacz.travelersbackpack.fluids;

import com.tiviacz.travelersbackpack.util.FluidUtils;
import net.minecraft.fluid.Fluid;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class PotionAttributes extends FluidAttributes
{
    protected PotionAttributes(Builder builder, Fluid fluid)
    {
        super(builder, fluid);
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return PotionUtils.getColor(FluidUtils.getItemStackFromFluidStack(stack)) | 0xFF000000;
    }

    public static class PotionBuilder extends Builder
    {
        public PotionBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture)
        {
            super(stillTexture, flowingTexture, PotionAttributes::new);
        }
    }
}