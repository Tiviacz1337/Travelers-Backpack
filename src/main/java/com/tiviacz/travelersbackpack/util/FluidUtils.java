package com.tiviacz.travelersbackpack.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
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
            soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        }

        return soundevent;
    }

    public static SoundEvent getFluidFillSound(Fluid fluid)
    {
        SoundEvent soundevent = fluid.getAttributes().getFillSound();

        if(soundevent == null)
        {
            soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
        }

        return soundevent;
    }

    public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack)
    {
        if(fluidStack.getTag() == null)
        {
            fluidStack.setTag(new CompoundNBT());
        }

        if(stack.getTag() != null && stack.getTag().getString("Potion") != null)
        {
            fluidStack.getTag().putString("Potion", stack.getTag().getString("Potion"));
        }
    }

    public static Potion getPotionTypeFromFluidStack(FluidStack fluidStack)
    {
        return PotionUtils.getPotionTypeFromNBT(fluidStack.getTag());
    }

    public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack)
    {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getItemStackFromPotionType(Potion potion)
    {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion);
    }
}
