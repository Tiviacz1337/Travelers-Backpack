package com.tiviacz.travelersbackpack.fluids.potion;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class PotionFluidVariantAttributeHandler implements FluidVariantAttributeHandler {
    @Override
    public Component getName(FluidVariant fluidVariant) {
        return Component.translatable(getTranslationKey(fluidVariant));
    }

    public String getTranslationKey(FluidVariant fluidVariant) {
        if (fluidVariant.hasComponents() && fluidVariant.getComponents().entrySet().stream().anyMatch(entry -> entry.getKey().equals(DataComponents.POTION_CONTENTS))) {
            return Potion.getName(fluidVariant.getComponents().get(DataComponents.POTION_CONTENTS).get().potion(), "item.minecraft.potion.effect.");
        }
        return Potion.getName(PotionContents.EMPTY.potion(), "item.minecraft.potion.effect.");
    }
}