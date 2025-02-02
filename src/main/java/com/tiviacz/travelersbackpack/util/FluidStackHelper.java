package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackHelper {
    public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack, int potionType) {
        if(stack.getTag() != null) {
            fluidStack.setTag(stack.getTag());
            if(potionType == 1) {
                fluidStack.getTag().putBoolean("Splash", true);
            }
            if(potionType == 2) {
                fluidStack.getTag().putBoolean("Lingering", true);
            }
        }
    }

    public static Potion getPotionTypeFromFluidStack(FluidStack fluidStack) {
        return PotionUtils.getPotion(fluidStack.getTag());
    }

    public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getSplashItemStackFromFluidStack(FluidStack fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getLingeringItemStackFromFluidStack(FluidStack fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), getPotionTypeFromFluidStack(fluidStack));
    }
}