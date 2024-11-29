package com.tiviacz.travelersbackpackold.util;

import com.tiviacz.travelersbackpackneo.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

public class FluidUtils
{
    public static FluidVariant setPotionFluidVariant(ItemStack stack)
    {
        FluidVariant newVariant;

        if(stack.contains(DataComponentTypes.POTION_CONTENTS))
        {
            newVariant = FluidVariant.of(ModFluids.POTION_STILL, stack.getComponentChanges());
        }
        else
        {
            newVariant = FluidVariant.of(ModFluids.POTION_STILL);
        }
        return newVariant;
    }

    public static RegistryEntry<Potion> getPotionTypeFromFluidVariant(FluidVariant variant)
    {
        return variant.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().potion().get();
    }

    public static ItemStack getItemStackFromFluidStack(FluidVariant variant)
    {
        return PotionContentsComponent.createStack(Items.POTION, getPotionTypeFromFluidVariant(variant));
    }
}