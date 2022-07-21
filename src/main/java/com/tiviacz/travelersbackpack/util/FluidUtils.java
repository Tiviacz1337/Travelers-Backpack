package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;

public class FluidUtils
{
    public static FluidVariant setPotionFluidVariant(ItemStack stack)
    {
        FluidVariant newVariant = FluidVariant.of(ModFluids.POTION_STILL, new NbtCompound());

        if(stack.getNbt() != null && stack.getNbt().getString("Potion") != null)
        {
            newVariant.getNbt().putString("Potion", stack.getNbt().getString("Potion"));
        }
        return newVariant;
    }

    public static Potion getPotionTypeFromFluidStack(FluidVariant variant)
    {
        return PotionUtil.getPotion(variant.getNbt());
    }

    public static ItemStack getItemStackFromFluidStack(FluidVariant variant)
    {
        return PotionUtil.setPotion(new ItemStack(Items.POTION), getPotionTypeFromFluidStack(variant));
    }

    public static ItemStack getItemStackFromPotionType(Potion potion)
    {
        return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
    }
}