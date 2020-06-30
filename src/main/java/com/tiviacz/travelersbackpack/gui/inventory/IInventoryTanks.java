package com.tiviacz.travelersbackpack.gui.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

public interface IInventoryTanks extends IInventory
{
	FluidTank getLeftTank();
	
	FluidTank getRightTank();
	
	void saveTanks(NBTTagCompound compound);
	
	void loadTanks(NBTTagCompound compound);
	
	boolean updateTankSlots();
	
	void markTankDirty();
}