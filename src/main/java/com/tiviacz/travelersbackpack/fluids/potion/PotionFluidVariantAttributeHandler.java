package com.tiviacz.travelersbackpack.fluids.potion;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;

public class PotionFluidVariantAttributeHandler implements FluidVariantAttributeHandler
{
    @Override
    public Text getName(FluidVariant fluidVariant)
    {
        return Text.translatable(getTranslationKey(fluidVariant));
    }

    public String getTranslationKey(FluidVariant fluidVariant)
    {
        return PotionUtil.getPotion(fluidVariant.getNbt()).finishTranslationKey("item.minecraft.potion.effect.");
    }
}