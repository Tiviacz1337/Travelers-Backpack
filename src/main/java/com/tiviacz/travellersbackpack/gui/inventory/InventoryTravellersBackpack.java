package com.tiviacz.travellersbackpack.gui.inventory;

import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidTank;

public class InventoryTravellersBackpack extends InventoryBasic implements IInventoryTravellersBackpack
{
	private FluidTank leftTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	private FluidTank rightTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	private EntityPlayer player;
	private ItemStack stack;
	
	public InventoryTravellersBackpack(ItemStack stack)
	{
		this(stack, null);
	}
	
	public InventoryTravellersBackpack(ItemStack stack, EntityPlayer player) 
	{
		super(("InventoryTravellersBackpack"), false, Reference.INVENTORY_SIZE);
		
		this.player = player;
		this.stack = stack;
		
		this.loadAllData(this.getTagCompound(stack));
	}
	
/*	public boolean isInventoryFull(ItemStack stack)
	{
		for(int x = 0; x < 39; x++)
		{
			boolean flag = getStackInSlot(x).isEmpty();
			boolean flag2 = (ItemStack.areItemsEqual(getStackInSlot(x), stack) && getStackInSlot(x).isStackable() && getStackInSlot(x).getCount() + 1 <= getStackInSlot(x).getMaxStackSize());
			
			if(flag || flag2)
			{
				return false;
			}
		}
		return true;
	} */

	@Override
	public FluidTank getLeftTank() 
	{
		return this.leftTank;
	}

	@Override
	public FluidTank getRightTank() 
	{
		return this.rightTank;
	}
	
	@Override
    public void markDirty()
    {
		this.saveAllData(this.getTagCompound(this.stack));
	    super.markDirty();
    }
	
	@Override
	public void markTankDirty() 
	{
		this.saveTanks(this.getTagCompound(this.stack));
	}
	
	@Override
	public void saveAllData(NBTTagCompound compound)
	{
		this.markTankDirty();
		this.saveItems(compound);
	}
	
	@Override
	public void loadAllData(NBTTagCompound compound)
	{
		this.loadTanks(compound);
		this.loadItems(compound);
	}

	@Override
	public void saveTanks(NBTTagCompound compound) 
	{
		compound.setTag("LeftTank", this.leftTank.writeToNBT(new NBTTagCompound()));
		compound.setTag("RightTank", this.rightTank.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void loadTanks(NBTTagCompound compound) 
	{
		this.leftTank.readFromNBT(compound.getCompoundTag("LeftTank"));
		this.rightTank.readFromNBT(compound.getCompoundTag("RightTank"));
	}
	
	@Override
	public void saveItems(NBTTagCompound compound)
	{
    	NBTTagList itemList = new NBTTagList();
    	
    	for(int i = 0; i < this.getSizeInventory(); i++)
    	{
    		ItemStack stack = this.getStackInSlot(i);
    		
    		if(!stack.isEmpty())
    		{
    			NBTTagCompound slotTag = new NBTTagCompound();
    			slotTag.setByte("Slot", (byte)i);
    			stack.writeToNBT(slotTag);
    			itemList.appendTag(slotTag);
    		}
    	}
    	compound.setTag("Items", itemList);
	}
	
	@Override
	public void loadItems(NBTTagCompound compound)
	{
    	NBTTagList list = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
    	
    	for(int i = 0; i < list.tagCount(); i++)
    	{
    		NBTTagCompound entry = list.getCompoundTagAt(i);
    		int index = entry.getByte("Slot") & 255;
    		
    		if(index >= 0 && index < this.getSizeInventory())
    		{
    			setInventorySlotContents(index, new ItemStack(entry));
    		}
    	}
	}
	
	@Override
	public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, player) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, player);
    }
	
	@Override
	public NBTTagCompound getTagCompound(ItemStack stack)
	{
		if(stack.getTagCompound() == null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
	    	
		return stack.getTagCompound();
	}

	@Override
	public boolean hasTileEntity() 
	{
		return false;
	}

	@Override
	public boolean isSleepingBagDeployed() 
	{
		return false;
	}
}