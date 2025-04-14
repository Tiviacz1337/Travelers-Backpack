package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public static ItemStack getPotionStack(FluidVariant fluidStack, Item potionItem) {
        ItemStack potionStack = new ItemStack(potionItem);
        if(!fluidStack.copyOrCreateNbt().isEmpty()) {
            if(fluidStack.getNbt().contains(PotionUtils.TAG_POTION)) {
                potionStack.getOrCreateTag().putString(PotionUtils.TAG_POTION, fluidStack.getNbt().getString(PotionUtils.TAG_POTION));
            }
            if(fluidStack.getNbt().contains(PotionUtils.TAG_CUSTOM_POTION_EFFECTS)) {
                potionStack.getOrCreateTag().put(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, fluidStack.getNbt().getList(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, 10));
            }
            if(fluidStack.getNbt().contains(PotionUtils.TAG_CUSTOM_POTION_COLOR)) {
                potionStack.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, fluidStack.getNbt().getInt(PotionUtils.TAG_CUSTOM_POTION_COLOR));
            }
        }
        return potionStack;
    }

    public static ItemStack getItemStackFromFluidStack(FluidVariant fluidStack) {
        return getPotionStack(fluidStack, Items.POTION);
    }

    public static ItemStack getSplashItemStackFromFluidStack(FluidVariant fluidStack) {
        return getPotionStack(fluidStack, Items.SPLASH_POTION);
    }

    public static ItemStack getLingeringItemStackFromFluidStack(FluidVariant fluidStack) {
        return getPotionStack(fluidStack, Items.LINGERING_POTION);
    }
}