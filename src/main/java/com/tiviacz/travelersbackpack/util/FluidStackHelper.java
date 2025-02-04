package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class FluidStackHelper {
    public static FluidVariant setFluidStackNBT(ItemStack stack, int potionType) {
        FluidVariant newVariant;

        if(stack.getTag() != null) {
            newVariant = FluidVariant.of(ModFluids.POTION_STILL, stack.getTag());
            if(potionType == 1) {
                newVariant.getNbt().putBoolean("Splash", true);
            }
            if(potionType == 2) {
                newVariant.getNbt().putBoolean("Lingering", true);
            }
        } else {
            newVariant = FluidVariant.of(ModFluids.POTION_STILL);
        }
        return newVariant;
    }

    public static Potion getPotionTypeFromFluidStack(FluidVariant fluidStack) {
        return PotionUtils.getPotion(fluidStack.getNbt());
    }

    public static ItemStack getItemStackFromFluidStack(FluidVariant fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getSplashItemStackFromFluidStack(FluidVariant fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getLingeringItemStackFromFluidStack(FluidVariant fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), getPotionTypeFromFluidStack(fluidStack));
    }
}