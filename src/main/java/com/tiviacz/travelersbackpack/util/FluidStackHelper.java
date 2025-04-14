package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public static ItemStack getPotionStack(FluidStack fluidStack, Item potionItem) {
        ItemStack potionStack = new ItemStack(potionItem);
        if(fluidStack.hasTag()) {
            if(fluidStack.getTag().contains(PotionUtils.TAG_POTION)) {
                potionStack.getOrCreateTag().putString(PotionUtils.TAG_POTION, fluidStack.getTag().getString(PotionUtils.TAG_POTION));
            }
            if(fluidStack.getTag().contains(PotionUtils.TAG_CUSTOM_POTION_EFFECTS)) {
                potionStack.getOrCreateTag().put(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, fluidStack.getTag().getList(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, 10));
            }
            if(fluidStack.getTag().contains(PotionUtils.TAG_CUSTOM_POTION_COLOR)) {
                potionStack.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, fluidStack.getTag().getInt(PotionUtils.TAG_CUSTOM_POTION_COLOR));
            }
        }
        return potionStack;
    }

    public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack) {
        return getPotionStack(fluidStack, Items.POTION);
    }

    public static ItemStack getSplashItemStackFromFluidStack(FluidStack fluidStack) {
        return getPotionStack(fluidStack, Items.SPLASH_POTION);
    }

    public static ItemStack getLingeringItemStackFromFluidStack(FluidStack fluidStack) {
        return getPotionStack(fluidStack, Items.LINGERING_POTION);
    }
}