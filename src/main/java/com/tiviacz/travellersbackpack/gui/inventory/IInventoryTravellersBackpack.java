package com.tiviacz.travellersbackpack.gui.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventoryTravellersBackpack extends IInventoryTanks
{
	public void saveItems(NBTTagCompound compound);
	
	public void loadItems(NBTTagCompound compound);
	
	public void saveAllData(NBTTagCompound compound);
	
	public void loadAllData(NBTTagCompound compound);
	
	public NBTTagCompound getTagCompound(ItemStack stack);
	
	public boolean hasTileEntity();
	
	public boolean isSleepingBagDeployed();
	
	public String getColor();
}