package com.tiviacz.travelersbackpack.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtils 
{
	public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack)
	{
		if(fluidStack.tag == null)
		{
			fluidStack.tag = new NBTTagCompound();
		}

		if(stack.getTagCompound() != null && stack.getTagCompound().getString("Potion") != null)
		{
			fluidStack.tag.setString("Potion", stack.getTagCompound().getString("Potion"));
		}
	}
	
	public static PotionType getPotionTypeFromFluidStack(FluidStack fluidStack)
	{
		return PotionUtils.getPotionTypeFromNBT(fluidStack.tag);
	}
	
	public static ItemStack getItemStackFromFluidStack(FluidStack fluidStack)
	{
		return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), getPotionTypeFromFluidStack(fluidStack));
	}
	
	public static ItemStack getItemStackFromPotionType(PotionType type)
	{
		return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), type);
	}
}
