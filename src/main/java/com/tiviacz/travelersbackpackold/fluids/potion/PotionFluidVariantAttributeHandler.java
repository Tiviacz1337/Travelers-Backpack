package com.tiviacz.travelersbackpackold.fluids.potion;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potion;
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
        if(fluidVariant.hasComponents() && fluidVariant.getComponents().entrySet().stream().anyMatch(entry -> entry.getKey().equals(DataComponentTypes.POTION_CONTENTS)))
        {
            return Potion.finishTranslationKey(fluidVariant.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().potion(), "item.minecraft.potion.effect.");
        }
        return Potion.finishTranslationKey(PotionContentsComponent.DEFAULT.potion(), "item.minecraft.potion.effect.");
    }
}