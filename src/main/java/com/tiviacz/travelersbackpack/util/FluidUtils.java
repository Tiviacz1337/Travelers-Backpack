package com.tiviacz.travelersbackpack.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtils
{
    public static SoundEvent getFluidEmptySound(Fluid fluid)
    {
        SoundEvent soundevent = fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY);

        if(soundevent == null)
        {
            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }

        return soundevent;
    }

    public static SoundEvent getFluidFillSound(Fluid fluid)
    {
        SoundEvent soundevent = fluid.getFluidType().getSound(SoundActions.BUCKET_FILL);

        if(soundevent == null)
        {
            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
        }

        return soundevent;
    }

    public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack)
    {
        if(fluidStack.getTag() == null)
        {
            fluidStack.setTag(new CompoundTag());
        }

        if(stack.getTag() != null && stack.getTag().getString("Potion") != null)
        {
            fluidStack.getTag().putString("Potion", stack.getTag().getString("Potion"));
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