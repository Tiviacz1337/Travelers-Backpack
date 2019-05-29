package com.tiviacz.travellersbackpack.init;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.fluids.FluidMilk;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids 
{
	public static final ResourceLocation MILK_STILL = new ResourceLocation(TravellersBackpack.MODID, "blocks/milk_still");
	public static final ResourceLocation MILK_FLOW = new ResourceLocation(TravellersBackpack.MODID, "blocks/milk_flow");
//	public static final ResourceLocation MUSHROOM_STEW_STILL = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_still");
//	public static final ResourceLocation MUSHROOM_STEW_FLOW = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_flow");
	
	public static FluidMilk milk;
//    public static FluidMushroomStew mushroomStew;
    
    public static void registerFluids()
    {
        milk = new FluidMilk("milk", MILK_STILL, MILK_FLOW, 0xffffff);
   //     mushroomStew = new FluidMushroomStew("mushroom_stew", MUSHROOM_STEW_STILL, MUSHROOM_STEW_FLOW, 0xcd8c6f);

        FluidRegistry.registerFluid(milk);
   //     FluidRegistry.registerFluid(mushroomStew);
    }
}