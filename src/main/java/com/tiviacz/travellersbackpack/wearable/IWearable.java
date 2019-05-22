package com.tiviacz.travellersbackpack.wearable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IWearable 
{
	public EntityPlayer getPlayer();
	
	public NBTTagCompound getPlayerData();
	
	public void saveDataToPlayer();
	
	public void loadDataFromPlayer();
	
	public boolean hasWearable();
	
	public ItemStack getWearable();
	
	public void setWearable(ItemStack stack);
}