package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;

public class FluidStackHelper {
    public static SoundEvent getFluidEmptySound(FluidVariant fluidVariant) {
        SoundEvent soundevent = FluidVariantAttributes.getEmptySound(fluidVariant); //.getFluidType().getSound(SoundActions.BUCKET_EMPTY);

        if(soundevent == null) {
            soundevent = fluidVariant.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }

        return soundevent;
    }

    public static SoundEvent getFluidFillSound(FluidVariant fluidVariant) {
        SoundEvent soundevent = FluidVariantAttributes.getFillSound(fluidVariant);//fluid.getFluidType().getSound(SoundActions.BUCKET_FILL);

        if(soundevent == null) {
            soundevent = fluidVariant.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
        }

        return soundevent;
    }

    public static FluidVariant setPotionFluidVariant(ItemStack stack, int potionType) {
        FluidVariant newVariant;

        if(stack.has(DataComponents.POTION_CONTENTS)) {
            DataComponentPatch.Builder patch = DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
            if(potionType == 1 || potionType == 2) {
                CompoundTag potionTypeTag = new CompoundTag();
                potionTypeTag.putInt("PotionType", potionType);
                patch.set(DataComponents.CUSTOM_DATA, CustomData.of(potionTypeTag));
            }
            newVariant = FluidVariant.of(ModFluids.POTION_STILL, patch.build());
        } else {
            newVariant = FluidVariant.of(ModFluids.POTION_STILL);
        }
        return newVariant;
    }

    public static Holder<Potion> getPotionTypeFromFluidVariant(FluidVariant variant) {
        return variant.getComponents().get(DataComponents.POTION_CONTENTS).get().potion().get();
    }

    public static ItemStack getItemStackFromFluidStack(FluidVariant variant) {
        return PotionContents.createItemStack(Items.POTION, getPotionTypeFromFluidVariant(variant));
    }

    public static ItemStack getSplashItemStackFromFluidStack(FluidVariant fluidStack) {
        return PotionContents.createItemStack(Items.SPLASH_POTION, getPotionTypeFromFluidVariant(fluidStack));
    }

    public static ItemStack getLingeringItemStackFromFluidStack(FluidVariant fluidStack) {
        return PotionContents.createItemStack(Items.LINGERING_POTION, getPotionTypeFromFluidVariant(fluidStack));
    }
}