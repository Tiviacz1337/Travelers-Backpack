package com.tiviacz.travellersbackpack.gui.container.slots;

import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTanks;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class SlotFluid extends Slot
{
	int index;
	
	public SlotFluid(IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
		
		this.index = index;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
    {
		IFluidHandlerItem container = FluidUtil.getFluidHandler(stack);
		
		if(index == 42 || index == 44)
		{
			return false;
		}
		
		return container != null ? true : false;
    }
	
	public static boolean isValid(ItemStack stack)
	{
		IFluidHandlerItem container = FluidUtil.getFluidHandler(stack);
		return container != null ? true : false;
	}
	
	public static boolean checkFluid(ItemStack stack, FluidTank leftTank, FluidTank rightTank)
	{
		IFluidHandlerItem container = FluidUtil.getFluidHandler(stack);
		
		if(container != null)
		{
			if(leftTank.getFluid() != null || leftTank.getFluidAmount() != 0)
			{
				if(leftTank.getFluid().isFluidEqual(container.getTankProperties()[0].getContents()))
				{
					if(leftTank.getFluidAmount() == leftTank.getCapacity())
					{
						return false;
					}
					else
					{
						return true;
					}
				}
			}
			else
			{
				if(rightTank.getFluid() != null || rightTank.getFluidAmount() != 0)
				{
					if(container.getTankProperties()[0].getContents() == null)
					{
						return false;
					}
					
					else if(!rightTank.getFluid().isFluidEqual(container.getTankProperties()[0].getContents()) || rightTank.getFluidAmount() == rightTank.getCapacity())
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onSlotChanged()
    {
        if(inventory instanceof IInventoryTanks)
        {
        	((IInventoryTanks)inventory).updateTankSlots();
        }
        
        super.onSlotChanged();
    }
}