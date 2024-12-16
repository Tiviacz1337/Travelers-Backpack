package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.PotionFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;

public class ModFluids {
    public static final FlowingFluid POTION_STILL = register("potion_still", new PotionFluid.Still());
    public static final FlowingFluid POTION_FLOWING = register("potion_flowing", new PotionFluid.Flowing());
    //public static final FlowingFluid MILK_STILL = register("milk_still", new MilkFluid.Still());
    //public static final FlowingFluid MILK_FLOWING = register("milk_flowing", new MilkFluid.Flowing());

    private static FlowingFluid register(String name, FlowingFluid flowableFluid) {
        return Registry.register(BuiltInRegistries.FLUID, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, name), flowableFluid);
    }
}