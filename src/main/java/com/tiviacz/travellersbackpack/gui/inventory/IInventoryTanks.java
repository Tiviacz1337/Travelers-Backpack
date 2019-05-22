package com.tiviacz.travellersbackpack.gui.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

public interface IInventoryTanks extends IInventory
{
	public FluidTank getLeftTank();
	
	public FluidTank getRightTank();
	
	public void saveTanks(NBTTagCompound compound);
	
	public void loadTanks(NBTTagCompound compound);
	
	public boolean updateTankSlots();
	
	public void markTankDirty();
}