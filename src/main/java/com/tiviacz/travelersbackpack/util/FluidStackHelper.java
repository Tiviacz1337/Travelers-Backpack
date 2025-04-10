package com.tiviacz.travelersbackpack.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidStackHelper {
    public static SoundEvent getFluidEmptySound(Fluid fluid) {
        SoundEvent soundevent = fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY);

        if(soundevent == null) {
            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }

        return soundevent;
    }

    public static SoundEvent getFluidFillSound(Fluid fluid) {
        SoundEvent soundevent = fluid.getFluidType().getSound(SoundActions.BUCKET_FILL);

        if(soundevent == null) {
            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
        }

        return soundevent;
    }

    public static void setFluidStackData(ItemStack stack, FluidStack fluidStack, int potionType) {
        fluidStack.set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
        if(potionType == 1 || potionType == 2) {
            CompoundTag potionTypeTag = new CompoundTag();
            potionTypeTag.putInt("PotionType", potionType);
            fluidStack.set(DataComponents.CUSTOM_DATA, CustomData.of(potionTypeTag));
        }
    }

    public static PotionContents getPotionTypeFromFluidStack(FluidStack fluidStack) {
        return fluidStack.get(DataComponents.POTION_CONTENTS);
    }

    public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack) {
        return createPotionStack(Items.POTION, getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getSplashItemStackFromFluidStack(FluidStack fluidStack) {
        return createPotionStack(Items.SPLASH_POTION, getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack getLingeringItemStackFromFluidStack(FluidStack fluidStack) {
        return createPotionStack(Items.LINGERING_POTION, getPotionTypeFromFluidStack(fluidStack));
    }

    public static ItemStack createPotionStack(Item item, PotionContents contents) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponents.POTION_CONTENTS, contents);
        return itemStack;
    }
}