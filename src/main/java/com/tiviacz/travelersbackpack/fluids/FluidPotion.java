package com.tiviacz.travelersbackpack.fluids;

import com.tiviacz.travelersbackpack.util.FluidUtils;

import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidPotion extends Fluid
{
	public FluidPotion(String fluidName, ResourceLocation still, ResourceLocation flowing) 
	{
		super(fluidName, still, flowing);
		
		this.setUnlocalizedName(fluidName);
		this.setGaseous(false);
		this.setDensity(1200);
		this.setViscosity(1200);
		this.setLuminosity(0);
	}
	
	@Override
	public int getColor(FluidStack stack)
	{ 
		return PotionUtils.getPotionColor(FluidUtils.getPotionTypeFromFluidStack(stack));
	}
}
