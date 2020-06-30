package com.tiviacz.travelersbackpack.gui.inventory;

import com.tiviacz.travelersbackpack.common.InventoryRecipesRegistry;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class InventoryActions 
{
	private static final boolean debug = false;
	
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

		if(InventoryRecipesRegistry.hasStackInInventoryRecipeAndCanProcess(player, inventory, stackIn, tank, slotIn))
		{
			return true;
		}

        else if(stackIn.getItem() != Items.GLASS_BOTTLE)
        {
        	//POTION PART ---
        	if(stackIn.getItem() instanceof ItemPotion)
        	{
        		int amount = Reference.POTION;
    			FluidStack fluidStack = new FluidStack(ModFluids.POTION, amount);
    			FluidUtils.setFluidStackNBT(stackIn, fluidStack);
    			
        		if(tank.getFluid() == null || FluidStack.areFluidStackTagsEqual(tank.getFluid(), fluidStack))
        		{
        			if(tank.getFluidAmount() + amount <= tank.getCapacity())
        			{
        				ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            			
            			if(inventory.getStackInSlot(slotOut).isEmpty())
            			{
            				tank.fill(fluidStack, true);
    	        			inventory.decrStackSize(slotIn, 1);
    	        			inventory.setInventorySlotContents(slotOut, bottle);
    	        			inventory.markTankDirty();
    	        			
    	        			if(player != null)
    	                    {
    	                        player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
    	                    }
    	        			
    	        			return true;
            			}
            			else if(inventory.getStackInSlot(slotOut).getItem() == bottle.getItem())
            			{
            				int maxStackSize = inventory.getStackInSlot(slotOut).getMaxStackSize();
            				
            				if(bottle.getCount() + 1 > maxStackSize)
            				{
            					return false;
            				}
            				else
            				{
            					bottle.setCount(inventory.getStackInSlot(slotOut).getCount() + 1);
            					
            					tank.fill(fluidStack, true);
                				inventory.decrStackSize(slotIn, 1);
                				inventory.setInventorySlotContents(slotOut, bottle);
                				inventory.markTankDirty();
                				
                				if(player != null)
        	                    {
        	                        player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
        	                    }
                				
                				return true;
            				}
            			}
        			}
        		}
        	}
        	//POTION PART ---
        	
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
        	// --- POTION PART ---
        	if(stackIn.getItem() == Items.GLASS_BOTTLE)
        	{
        		if(tank.getFluid().getFluid() == ModFluids.POTION && tank.getFluidAmount() >= Reference.POTION)
        		{
        			ItemStack stackOut = FluidUtils.getItemStackFromFluidStack(tank.getFluid());
        			
        			if(inventory.getStackInSlot(slotOut).isEmpty())
        			{
        				tank.drain(Reference.POTION, true);
        				inventory.decrStackSize(slotIn, 1);
                		inventory.setInventorySlotContents(slotOut, stackOut);
                		inventory.markTankDirty();
                		
                		if(player != null)
	                    {
	                        player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
	                    }
                		
                		return true;
        			}
        		}
        	}
        	// --- POTION PART ---
        	
        	else if(isFluidEqual(stackIn, tank))
        	{
        		if(tank.getFluid().getFluid() == ModFluids.POTION)
        		{
        			return false;
        		}
        		
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
			return FluidUtil.getFluidContained(stackIn).isFluidEqual(tank.getFluid());
		}
		else return FluidUtil.getFluidContained(stackIn) == null;
	}
	
	private static void debug(String string)
	{
		if(debug)
		{
			System.out.println(string);
		}
	}
}