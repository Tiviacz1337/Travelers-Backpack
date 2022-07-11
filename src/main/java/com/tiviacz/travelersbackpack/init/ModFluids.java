package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.PotionAttributes;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids
{
    public static final ResourceLocation POTION_STILL_RL = new ResourceLocation(TravelersBackpack.MODID, "block/potion_still");
    public static final ResourceLocation POTION_FLOW_RL = new ResourceLocation(TravelersBackpack.MODID, "block/potion_flow");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, TravelersBackpack.MODID);

    public static final RegistryObject<FlowingFluid> POTION_FLUID
            = FLUIDS.register("potion_fluid", () -> new ForgeFlowingFluid.Source(ModFluids.POTION_PROPERTIES));

    public static final RegistryObject<FlowingFluid> POTION_FLOWING
            = FLUIDS.register("potion_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.POTION_PROPERTIES));

    public static final ForgeFlowingFluid.Properties POTION_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> POTION_FLUID.get(), () -> POTION_FLOWING.get(), new PotionAttributes.PotionBuilder(POTION_STILL_RL, POTION_FLOW_RL)
            .color(13458603).density(15).luminosity(5).viscosity(5).sound(SoundEvents.BOTTLE_FILL, SoundEvents.BOTTLE_EMPTY));
}
