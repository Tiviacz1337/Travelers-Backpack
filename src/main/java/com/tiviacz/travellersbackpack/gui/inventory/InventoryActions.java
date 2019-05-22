package com.tiviacz.travellersbackpack.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class InventoryActions 
{
	private static boolean debug = false;
	
	public static boolean transferContainerTank(IInventoryTanks inventory, FluidTank tank, int slotIn, EntityPlayer player)
    {
		//Container ===> Tank
			
        ItemStack stackIn = inventory.getStackInSlot(slotIn);
        int slotOut = slotIn + 1;
        
        if(tank == null || stackIn.isEmpty())
        {
        	if(tank == null)
        	{
        		debug("Tank is null");
        	}
        	
        	if(stackIn.isEmpty())
        	{
        		debug("stackIn is null");
        	}
        	return false;
        }
        else
        {
        	IFluidHandlerItem container = FluidUtil.getFluidHandler(stackIn);
        	
        	if(container == null)
        	{
        		debug("Itemstack isn't a container");
        		return false;
        	}
        	else
        	{
        		FluidStack fluid = FluidUtil.getFluidContained(stackIn);
        		
        		if(fluid == null || fluid.amount <= 0)
        		{
        			debug("No actual fluid in a container");
        		}
        		else
        		{
        			int amount = fluid.amount;
        			ItemStack stackOut = FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, false).getResult();
        			
        			if(tank.getFluidAmount() + amount > tank.getCapacity())
        			{
        				if(tank.getFluidAmount() + amount > tank.getCapacity())
        				{
        					debug("Fluid income is bigger than tank capacity");
        					return false;
        				}
        			}
        			if(tank.getFluidAmount() > 0 && !tank.getFluid().isFluidEqual(fluid))
        			{
        				debug("Fluids aren't the same!");
        				return false;
        			}
        			
        			else
        			{
        				if(inventory.getStackInSlot(slotOut).isEmpty())
            			{
    	        			FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, true);
    	        			
    	        			inventory.decrStackSize(slotIn, 1);
    	        			inventory.setInventorySlotContents(slotOut, stackOut);
    	        			inventory.markTankDirty();
    	        			
    	        			debug("SlotOut is empty");
    	        			debug("Fluid successfuly transfered with amount of" + amount);
    	        			debug("Current capacity of fluid in tank:" + tank.getFluidAmount());
    	        			
    	        			return true;
            			}
            			
            			else if(inventory.getStackInSlot(slotOut).getItem() == stackOut.getItem())
            			{
            				int maxStackSize = inventory.getStackInSlot(slotOut).getMaxStackSize();
            				
            				if(stackOut.getCount() + 1 > maxStackSize)
            				{
            					debug("Can't reach the stack size");
            					return false;
            				}
            				else
            				{
            					stackOut.setCount(inventory.getStackInSlot(slotOut).getCount() + 1);
            					
            					FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, true);
            					
                				inventory.decrStackSize(slotIn, 1);
                				inventory.setInventorySlotContents(slotOut, stackOut);
                				inventory.markTankDirty();
                				
                				debug("SlotOut is not empty");
                				debug("Fluid successfuly transfered with amount of " + amount);
                				debug("Current capacity of fluid in tank:" + tank.getFluidAmount());
                				
                				return true;
            				}
            			}
        			}
        		}
        	}
        }
        
        //Tank ===> Container
        
        if(tank.getFluid() == null || tank.getFluidAmount() <= 0)
        {
        	debug("Tank is empty");
        	return false;
        }
        else
        {
        	if(isFluidEqual(stackIn, tank))
        	{
        		int amount = FluidUtil.getFluidHandler(stackIn).getTankProperties()[0].getCapacity();
        		ItemStack stackOut = FluidUtil.tryFillContainer(stackIn, tank, amount, player, false).getResult();
        		
        		if(inventory.getStackInSlot(slotOut).isEmpty())
        		{
            		FluidUtil.tryFillContainer(stackIn, tank, amount, player, true);
            		
            		inventory.decrStackSize(slotIn, 1);
            		inventory.setInventorySlotContents(slotOut, stackOut);
            		inventory.markTankDirty();
            		
            		debug("SlotOut is not Empty");
            		debug("Fluid successfully transfered with amount of " + amount);
            		debug("Current capacity of fluid in tank:" + tank.getFluidAmount());
            		
            		return true;
            		
        		}
        		else if(inventory.getStackInSlot(slotOut).getItem() == stackOut.getItem())
        		{
        			int maxStackSize = inventory.getStackInSlot(slotOut).getMaxStackSize();
        			
        			if(stackOut.getCount() + 1 > maxStackSize)
        			{
        				debug("Can't reach the stack size");
        				return false;
        			}
        			else
        			{
        				stackOut.setCount(inventory.getStackInSlot(slotOut).getCount() + 1);
        				
        				FluidUtil.tryFillContainer(stackIn, tank, amount, player, true);
            			
            			inventory.decrStackSize(slotIn, 1);
            			inventory.setInventorySlotContents(slotOut, stackOut);
            			inventory.markTankDirty();
            			
            			debug("SlotOut is not empty");
            			debug("Fluid successfully transfered with amount of " + amount);
            			debug("Current capacity of fluid in tank:" + tank.getFluidAmount());
            			
            			return true;
        			}
        		}
        	}
        }
        return false;
    }
	
	private static boolean isFluidEqual(ItemStack stackIn, FluidTank tank)
	{
		if(FluidUtil.getFluidContained(stackIn) != null && FluidUtil.getFluidContained(stackIn).amount > 0)
		{
			if(FluidUtil.getFluidContained(stackIn).isFluidEqual(tank.getFluid()))
			{
				return true;
			}
		}
		else if(FluidUtil.getFluidContained(stackIn) == null)
		{
			return true;
		}
		return false;
	}
	
	private static void debug(String string)
	{
		if(debug)
		{
			System.out.println(string);
		}
	}
}
