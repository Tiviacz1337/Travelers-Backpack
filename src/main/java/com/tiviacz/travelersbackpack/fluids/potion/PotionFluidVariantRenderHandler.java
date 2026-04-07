package com.tiviacz.travelersbackpack.fluids.potion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PotionFluidVariantRenderHandler implements FluidVariantRenderHandler {
    private static final int EMPTY_COLOR = 0xf800f8;

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
        if(fluidVariant.hasComponents() && fluidVariant.getComponents().has(DataComponents.POTION_CONTENTS)) {
            return fluidVariant.getComponents().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
        }
        return EMPTY_COLOR | 0xFF000000;
    }
}