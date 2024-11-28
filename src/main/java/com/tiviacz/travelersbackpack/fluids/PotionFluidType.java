package com.tiviacz.travelersbackpack.fluids;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class PotionFluidType extends FluidType {
    public static final ResourceLocation POTION_STILL_RL = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_still");
    public static final ResourceLocation POTION_FLOW_RL = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_flow");

    public PotionFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public Component getDescription(FluidStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        return Potion.getName(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion(), this.getDescriptionId() + ".effect.");
    }

    @Override
    public String getDescriptionId() {
        return "item.minecraft.potion.effect.empty";
    }
}