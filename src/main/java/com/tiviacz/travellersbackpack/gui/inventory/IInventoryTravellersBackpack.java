package com.tiviacz.travellersbackpack.gui.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public interface IInventoryTravellersBackpack extends IInventoryTanks
{
	public void saveItems(NBTTagCompound compound);
	
	public void loadItems(NBTTagCompound compound);
	
	public void saveTime(NBTTagCompound compound);
	
	public void loadTime(NBTTagCompound compound);
	
	public void markTimeDirty();
	
	public void saveAllData(NBTTagCompound compound);
	
	public void loadAllData(NBTTagCompound compound);
	
	public NBTTagCompound getTagCompound(ItemStack stack);
	
	public boolean hasTileEntity();
	
	public boolean isSleepingBagDeployed();
	
	public String getColor();
	
	public NonNullList<ItemStack> getCraftingGridInventory();
	
	public NonNullList<ItemStack> getInventory();
	
	public void setLastTime(int time);
	
	public int getLastTime();
	
	public BlockPos getPosition();
}