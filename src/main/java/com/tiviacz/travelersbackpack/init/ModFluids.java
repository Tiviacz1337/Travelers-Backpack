package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.PotionFluidType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids
{
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, TravelersBackpack.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, TravelersBackpack.MODID);

    public static RegistryObject<FluidType> POTION_FLUID_TYPE = FLUID_TYPES.register("potion",
            () -> new PotionFluidType(FluidType.Properties.create().supportsBoating(true).canSwim(true).canConvertToSource(false).viscosity(5).density(15)));

    public static final RegistryObject<FlowingFluid> POTION_FLUID
            = FLUIDS.register("potion_fluid", () -> new ForgeFlowingFluid.Source(ModFluids.POTION_PROPERTIES));

    public static final RegistryObject<FlowingFluid> POTION_FLOWING
            = FLUIDS.register("potion_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.POTION_PROPERTIES));

    public static final ForgeFlowingFluid.Properties POTION_PROPERTIES = new ForgeFlowingFluid.Properties(() -> POTION_FLUID_TYPE.get(), () -> POTION_FLUID.get(), () -> POTION_FLOWING.get());
}