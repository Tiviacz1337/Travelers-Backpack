package com.tiviacz.travelersbackpack.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class PotionAttributes extends FluidAttributes
{
    private static final int EMPTY_COLOR = 0xf800f8;

    protected PotionAttributes(Builder builder, Fluid fluid)
    {
        super(builder, fluid);
    }

    @Override
    public String getTranslationKey()
    {
        return "item.minecraft.potion.effect.empty";
    }

    @Override
    public String getTranslationKey(FluidStack stack)
    {
        return PotionUtils.getPotion(stack.getTag()).getName("item.minecraft.potion.effect.");
    }

    @Override
    public TextComponent getDisplayName(FluidStack stack)
    {
        return new TranslationTextComponent(getTranslationKey(stack));
    }

    @Override
    public int getColor()
    {
        return EMPTY_COLOR | 0xFF000000;
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return getColor(stack.getTag()) | 0xFF000000;
    }

    private static int getColor(@Nullable CompoundNBT compoundNBT)
    {
        if(compoundNBT != null && compoundNBT.contains("CustomPotionColor", Constants.NBT.TAG_ANY_NUMERIC))
        {
            return compoundNBT.getInt("CustomPotionColor");
        }

        if(PotionUtils.getPotion(compoundNBT) == Potions.EMPTY)
        {
            return EMPTY_COLOR;
        }

        return PotionUtils.getColor(PotionUtils.getAllEffects(compoundNBT));
    }

    public static class PotionBuilder extends Builder
    {
        public PotionBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture)
        {
            super(stillTexture, flowingTexture, PotionAttributes::new);
        }
    }
}