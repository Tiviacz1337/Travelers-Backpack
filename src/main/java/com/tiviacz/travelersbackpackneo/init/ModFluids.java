package com.tiviacz.travelersbackpackneo.init;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.fluids.MilkFluid;
import com.tiviacz.travelersbackpackold.fluids.PotionFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModFluids
{
    public static final FlowableFluid POTION_STILL = register("potion_still", new PotionFluid.Still());
    public static final FlowableFluid POTION_FLOWING = register("potion_flowing", new PotionFluid.Flowing());
    public static final FlowableFluid MILK_STILL = register("milk_still", new MilkFluid.Still());
    public static final FlowableFluid MILK_FLOWING = register("milk_flowing", new MilkFluid.Flowing());

    private static FlowableFluid register(String name, FlowableFluid flowableFluid)
    {
        return Registry.register(Registries.FLUID, Identifier.of(TravelersBackpack.MODID, name), flowableFluid);
    }
}