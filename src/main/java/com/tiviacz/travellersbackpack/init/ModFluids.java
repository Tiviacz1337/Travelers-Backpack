package com.tiviacz.travellersbackpack.init;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.fluids.FluidMilk;
import com.tiviacz.travellersbackpack.fluids.FluidPotion;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids 
{
	public static final ResourceLocation MILK_STILL = new ResourceLocation(TravellersBackpack.MODID, "blocks/milk_still");
	public static final ResourceLocation MILK_FLOW = new ResourceLocation(TravellersBackpack.MODID, "blocks/milk_flow");
	public static final ResourceLocation POTION_STILL = new ResourceLocation(TravellersBackpack.MODID, "blocks/potion_still");
	public static final ResourceLocation POTION_FLOW = new ResourceLocation(TravellersBackpack.MODID, "blocks/potion_flow");
//	public static final ResourceLocation MUSHROOM_STEW_STILL = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_still");
//	public static final ResourceLocation MUSHROOM_STEW_FLOW = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_flow");
	
	public static FluidMilk MILK;
	public static FluidPotion POTION;
//    public static FluidMushroomStew mushroomStew;
    
    public static void registerFluids()
    {
        MILK = new FluidMilk("milk", MILK_STILL, MILK_FLOW, 0xffffff);
        POTION = new FluidPotion("potion", POTION_STILL, POTION_FLOW);
   //     mushroomStew = new FluidMushroomStew("mushroom_stew", MUSHROOM_STEW_STILL, MUSHROOM_STEW_FLOW, 0xcd8c6f);

        FluidRegistry.registerFluid(MILK);
        FluidRegistry.registerFluid(POTION);
   //     FluidRegistry.registerFluid(mushroomStew);
    }
}