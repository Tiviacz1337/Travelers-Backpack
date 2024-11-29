package com.tiviacz.travelersbackpackold.fluids.potion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PotionFluidVariantRenderHandler implements FluidVariantRenderHandler
{
    private static final int EMPTY_COLOR = 0xf800f8;

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos)
    {
        if(fluidVariant.hasComponents() && fluidVariant.getComponents().entrySet().stream().anyMatch(entry -> entry.getKey().equals(DataComponentTypes.POTION_CONTENTS)))
        {
            return fluidVariant.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getColor();
        }
        return EMPTY_COLOR | 0xFF000000;
        //return getColor(fluidVariant.getNbt()) | 0xFF000000;
    }
}