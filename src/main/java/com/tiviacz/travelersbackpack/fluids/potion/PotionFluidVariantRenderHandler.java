package com.tiviacz.travelersbackpack.fluids.potion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PotionFluidVariantRenderHandler implements FluidVariantRenderHandler
{
    private static final int EMPTY_COLOR = 0xf800f8;

    @Override
    public Text getName(FluidVariant fluidVariant)
    {
        return new TranslatableText(getTranslationKey(fluidVariant));
    }

    public String getTranslationKey(FluidVariant fluidVariant)
    {
        return PotionUtil.getPotion(fluidVariant.getNbt()).finishTranslationKey("item.minecraft.potion.effect.");
    }

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos)
    {
        return getColor(fluidVariant.getNbt()) | 0xFF000000;
    }

    private static int getColor(@Nullable NbtCompound nbt)
    {
        if(nbt != null && nbt.contains("CustomPotionColor", NbtElement.NUMBER_TYPE))
        {
            return nbt.getInt("CustomPotionColor");
        }
        if(PotionUtil.getPotion(nbt) == Potions.EMPTY)
        {
            return EMPTY_COLOR;
        }
        return PotionUtil.getColor(PotionUtil.getPotionEffects(nbt));
    }
}