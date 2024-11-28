package com.tiviacz.travelersbackpack.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;

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

   /* public static void setFluidStackData(ItemStack stack, FluidStack fluidStack) {
        fluidStack.set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
    }

    public static Holder<Potion> getPotionTypeFromFluidStack(FluidStack fluidStack) {
        return fluidStack.get(DataComponents.POTION_CONTENTS).potion().get();
    } */

    // public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack) {
    //     return PotionContents.createItemStack(Items.POTION, getPotionTypeFromFluidStack(fluidStack));
    // }
}