package com.tiviacz.travelersbackpack.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class FluidTypeHelper {

    public static Component getFluidVariantName(FluidVariant variant) {
        return FluidVariantAttributes.getName(variant);
    }

    public static SoundEvent getSound(FluidVariant fluid, boolean isFill) {
        return isFill ? FluidVariantAttributes.getFillSound(fluid) : FluidVariantAttributes.getEmptySound(fluid);
    }

    public static final boolean BUCKET_EMPTY = false;
    public static final boolean BUCKET_FILL = true;
}
