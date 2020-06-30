package com.tiviacz.travelersbackpack.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ITravelersBackpack
{
	boolean hasWearable();
	
	ItemStack getWearable();
	
	void setWearable(ItemStack stack);
	
	void removeWearable();

	void synchronise();

	void synchroniseToOthers(EntityPlayer player);
}