package com.tiviacz.travelersbackpack.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtils
{
    public static SoundEvent getFluidEmptySound(Fluid fluid)
    {
        SoundEvent soundevent = fluid.getAttributes().getEmptySound();

        if(soundevent == null)
        {
            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }

        return soundevent;
    }

    public static SoundEvent getFluidFillSound(Fluid fluid)
    {
        SoundEvent soundevent = fluid.getAttributes().getFillSound();

        if(soundevent == null)
        {
            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
        }

        return soundevent;
    }

    public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack)
    {
        if(stack.getTag() != null)
        {
            fluidStack.setTag(stack.getTag());
        }
    }

    public static Potion getPotionTypeFromFluidStack(FluidStack fluidStack)
    {
        return PotionUtils.getPotion(fluidStack.getTag());
    }

    public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack)
    {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getItemStackFromPotionType(Potion potion)
    {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }
}