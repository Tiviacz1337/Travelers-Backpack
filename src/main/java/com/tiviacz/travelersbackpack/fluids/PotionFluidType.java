package com.tiviacz.travelersbackpack.fluids;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class PotionFluidType extends FluidType {
    public static final Identifier POTION_STILL_RL = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_still");
    public static final Identifier POTION_FLOW_RL = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_flow");

    public PotionFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public Component getDescription(FluidStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        String s = contents.customName().or(() -> contents.potion().map(p_372776_ -> p_372776_.value().name())).orElse("empty");
        return "item.minecraft.potion.effect." + s;
    }

    @Override
    public String getDescriptionId() {
        return "item.minecraft.potion.effect.empty";
    }
}