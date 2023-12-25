package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.PotionFluidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids
{
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TravelersBackpack.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, TravelersBackpack.MODID);

    public static DeferredHolder<FluidType, PotionFluidType> POTION_FLUID_TYPE = FLUID_TYPES.register("potion",
            () -> new PotionFluidType(FluidType.Properties.create().supportsBoating(true).canSwim(true).canConvertToSource(false).viscosity(5).density(15)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> POTION_FLUID
            = FLUIDS.register("potion_fluid", () -> new BaseFlowingFluid.Source(ModFluids.POTION_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> POTION_FLOWING
            = FLUIDS.register("potion_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.POTION_PROPERTIES));

    public static final BaseFlowingFluid.Properties POTION_PROPERTIES = new BaseFlowingFluid.Properties(() -> POTION_FLUID_TYPE.get(), () -> POTION_FLUID.get(), () -> POTION_FLOWING.get());
}