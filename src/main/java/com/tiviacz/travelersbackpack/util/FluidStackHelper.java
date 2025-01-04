package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackHelper {
    public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack) {
        if(stack.getTag() != null) {
            fluidStack.setTag(stack.getTag());
        }
    }

    public static Potion getPotionTypeFromFluidStack(FluidStack fluidStack) {
        return PotionUtils.getPotion(fluidStack.getTag());
    }

    public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), getPotionTypeFromFluidStack(fluidStack));
    }
}