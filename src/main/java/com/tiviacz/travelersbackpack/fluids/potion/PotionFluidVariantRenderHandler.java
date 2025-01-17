package com.tiviacz.travelersbackpack.fluids.potion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PotionFluidVariantRenderHandler implements FluidVariantRenderHandler {
    private static final int EMPTY_COLOR = 0xf800f8;

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
        return getColor(fluidVariant.getNbt()) | 0xFF000000;
    }

    private static int getColor(@Nullable CompoundTag nbt) {
        if(nbt != null && nbt.contains("CustomPotionColor", CompoundTag.TAG_INT)) {
            return nbt.getInt("CustomPotionColor");
        }
        if(PotionUtils.getPotion(nbt) == Potions.EMPTY) {
            return EMPTY_COLOR;
        }
        return PotionUtils.getColor(PotionUtils.getAllEffects(nbt));
    }
}