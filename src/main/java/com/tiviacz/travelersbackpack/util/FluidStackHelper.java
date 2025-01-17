package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class FluidStackHelper {
    public static FluidVariant setFluidStackNBT(ItemStack stack) {
        FluidVariant newVariant;

        if(stack.getTag() != null) {
            newVariant = FluidVariant.of(ModFluids.POTION_STILL, stack.getTag());
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
}