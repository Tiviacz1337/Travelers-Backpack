package com.tiviacz.travelersbackpack.fluids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
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
    public Component getDisplayName(FluidStack stack)
    {
        return new TranslatableComponent(getTranslationKey(stack));
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

    private static int getColor(@Nullable CompoundTag tag)
    {
        if(tag != null && tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC))
        {
            return tag.getInt("CustomPotionColor");
        }
        if(PotionUtils.getPotion(tag) == Potions.EMPTY)
        {
            return EMPTY_COLOR;
        }
        return PotionUtils.getColor(PotionUtils.getAllEffects(tag));
    }

 /*   @Override
    public int getColor(FluidStack stack)
    {
        return PotionUtils.getColor(FluidUtils.getItemStackFromFluidStack(stack)) | 0xFF000000;
    } */

    public static class PotionBuilder extends Builder
    {
        public PotionBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture)
        {
            super(stillTexture, flowingTexture, PotionAttributes::new);
        }
    }
}