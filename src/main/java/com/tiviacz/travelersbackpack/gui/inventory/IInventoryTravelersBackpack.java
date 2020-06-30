package com.tiviacz.travelersbackpack.gui.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public interface IInventoryTravelersBackpack extends IInventoryTanks
{
	void saveItems(NBTTagCompound compound);
	
	void loadItems(NBTTagCompound compound);
	
	void saveTime(NBTTagCompound compound);
	
	void loadTime(NBTTagCompound compound);
	
	void markTimeDirty();
	
	void saveAllData(NBTTagCompound compound);
	
	void loadAllData(NBTTagCompound compound);
	
	NBTTagCompound getTagCompound(ItemStack stack);
	
	boolean hasTileEntity();
	
	boolean isSleepingBagDeployed();
	
	String getColor();
	
	NonNullList<ItemStack> getCraftingGridInventory();
	
	NonNullList<ItemStack> getInventory();
	
	void setLastTime(int time);
	
	int getLastTime();
	
	BlockPos getPosition();
}