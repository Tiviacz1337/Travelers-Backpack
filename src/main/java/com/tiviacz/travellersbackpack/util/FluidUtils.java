package com.tiviacz.travellersbackpack.util;

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
		
		fluidStack.tag.setString("Potion", stack.getTagCompound().getString("Potion"));
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
	
/*	public static void setFluidStackNBT(ItemStack stack, FluidStack fluidStack)
	{
		List<PotionEffect> potionEffects = PotionUtils.getEffectsFromStack(stack);
		NBTTagList list = new NBTTagList();
		
		for(PotionEffect effect : potionEffects)
		{
			NBTTagCompound effectTag = new NBTTagCompound();
			effect.writeCustomPotionEffectToNBT(effectTag);
			list.appendTag(effectTag);
		}
		
		if(fluidStack.tag == null)
		{
			fluidStack.tag = new NBTTagCompound();
		}
		
		fluidStack.tag.setTag("Potion", list);
	}
	
	public static List<PotionEffect> getPotionEffectList(FluidStack fluidStack)
	{
		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
		NBTTagList list = fluidStack.tag.getTagList("Effects", Constants.NBT.TAG_COMPOUND);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound fluidTag = list.getCompoundTagAt(i);
			PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(fluidTag);
			potionEffects.add(effect);
		}
		return potionEffects;
	} */
}
