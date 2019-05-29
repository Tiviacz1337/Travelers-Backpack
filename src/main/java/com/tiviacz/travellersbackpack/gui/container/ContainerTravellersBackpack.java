package com.tiviacz.travellersbackpack.gui.container;

import com.tiviacz.travellersbackpack.gui.container.slots.SlotBackpack;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotDisabled;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotFluid;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotTool;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.util.NBTUtils;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerTravellersBackpack extends Container 
{
	public InventoryPlayer playerInv;
	public IInventoryTravellersBackpack inventory;
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	public World world;
	public byte source;
	
	private final int numRowsUpper = 3;
	private final int numColumnsUpper = 8;
	private final int numRowsLower = 3;
	private final int numColumnsLower = 5;
	
	private final int BACK_INV_START = 10, BACK_INV_END = 48;
	private final int PLAYER_INV_START = 49, PLAYER_HOT_END = 84;
	private final int BUCKET_LEFT_IN = 85, BUCKET_LEFT_OUT = 86;
	private final int BUCKET_RIGHT_IN = 87, BUCKET_RIGHT_OUT = 88;
	private final int TOOL_START = 89, TOOL_END = 90;
	
	public ContainerTravellersBackpack(World world, InventoryPlayer playerInv, IInventoryTravellersBackpack inventory, byte source)
	{
		this.world = world;
		this.playerInv = playerInv;
		this.inventory = inventory;
		this.source = source;
		int currentItemIndex = playerInv.currentItem;
		
		this.addCraftMatrix();
		this.addCraftResult(playerInv.player);
		
		this.addBackpackInventory(inventory);	
		this.addPlayerInventoryAndHotbar(playerInv, currentItemIndex);
		this.addFluidTanks(inventory);
		this.addToolSlots(inventory);
	}
	
	public void addCraftMatrix()
	{
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 152 + j * 18, 61 + i * 18));
            }
        }
	}
	
	public void addCraftResult(EntityPlayer player)
	{
		this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 226, 97));
	}
	
	public void addBackpackInventory(IInventoryTravellersBackpack inventory)
	{
		int slot = 0;
		
		//24 Slots
		
		for(int i = 0; i < this.numRowsUpper; ++i)
	    {
			for(int j = 0; j < this.numColumnsUpper; ++j)
			{
				this.addSlotToContainer(new SlotBackpack(inventory, slot++, 62 + j * 18, 7 + i * 18));
			}
	    }
		
		//15 Slots
		
		for(int i = 0; i < this.numRowsLower; ++i)
	    {
			for(int j = 0; j < this.numColumnsLower; ++j)
			{
				this.addSlotToContainer(new SlotBackpack(inventory, slot++, 62 + j * 18, 61 + i * 18));
			}
	    }
	}
	
	public void addPlayerInventoryAndHotbar(InventoryPlayer playerInv, int currentItemIndex)
	{
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 44 + x*18, 125 + y*18));
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			if(x == currentItemIndex && !this.inventory.hasTileEntity() && !NBTUtils.hasWearingTag(this.playerInv.player))
			{
				this.addSlotToContainer(new SlotDisabled(playerInv, x, 44 + x*18, 183));
			}
			else
			{
				this.addSlotToContainer(new Slot(playerInv, x, 44 + x*18, 183));
			}
		}		
	}
	
	public void addFluidTanks(IInventoryTravellersBackpack inventory)
	{
		//Left In bucket
		this.addSlotToContainer(new SlotFluid(inventory, Reference.BUCKET_IN_LEFT, 6, 7));
		
		//Left Out bucket
		this.addSlotToContainer(new SlotFluid(inventory, Reference.BUCKET_OUT_LEFT, 6, 37));
		
		//Right In bucket
		this.addSlotToContainer(new SlotFluid(inventory, Reference.BUCKET_IN_RIGHT, 226, 7));
		
		//Right Out bucket
		this.addSlotToContainer(new SlotFluid(inventory, Reference.BUCKET_OUT_RIGHT, 226, 37));
	}
	
	public void addToolSlots(IInventoryTravellersBackpack inventory)
	{
		//Upper Tool Slot
		this.addSlotToContainer(new SlotTool(inventory, Reference.TOOL_UPPER, 44, 79));
	        
		//Lower Tool slot
		this.addSlotToContainer(new SlotTool(inventory, Reference.TOOL_LOWER, 44, 97));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;
        
        if(slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            
            if(index >= 0 && index <= BACK_INV_END)
            {
                if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            
            if(index >= PLAYER_INV_START)
            {
                if(SlotTool.isValid(stack))
                {
                	if(index == TOOL_START || index == TOOL_END)
                	{
                		if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                    	{
                    		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                            {
                                return ItemStack.EMPTY;
                            }
                    	}
                	}
                	else
                	{
                		if(!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
                        {
                			if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                            {
                				if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                            	{
                					return ItemStack.EMPTY;
                            	}
                            }
                        }
                	}
                }
                
                else if(SlotFluid.isValid(stack))
                {
                	if(index == BUCKET_LEFT_OUT || index == BUCKET_RIGHT_OUT)
                	{
                		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                		{
                			if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                    		{
                    			return ItemStack.EMPTY;
                    		}
                		}
                		
                		this.inventory.updateTankSlots();
                	}
                	
                	if(index == BUCKET_LEFT_IN || index == BUCKET_RIGHT_IN)
                	{
                		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                		{
                			if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                    		{
                    			return ItemStack.EMPTY;
                    		}
                		}
                	}
                	else
                	{
                		
                		if(SlotFluid.checkFluid(stack, this.inventory.getLeftTank(), this.inventory.getRightTank()) && this.inventory.getStackInSlot(BUCKET_LEFT_IN).isEmpty())
                    	{
                    		if(!mergeItemStack(stack, BUCKET_LEFT_IN, BUCKET_LEFT_IN + 1, false))
                        	{
                        		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                            	{
                            		return ItemStack.EMPTY;
                            	}
                        	}
                    	}
                		
                    	if(SlotFluid.checkFluid(stack, this.inventory.getRightTank(), this.inventory.getLeftTank()) && this.inventory.getStackInSlot(BUCKET_RIGHT_IN).isEmpty())
                    	{
                    		if(!mergeItemStack(stack, BUCKET_RIGHT_IN, BUCKET_RIGHT_IN + 1, false))
                        	{
                        		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                            	{
                            		return ItemStack.EMPTY;
                            	}
                        	}
                    	}
                    	
                    	else
                    	{
                    		if(this.inventory.getLeftTank().getFluid() == null || this.inventory.getLeftTank().getFluidAmount() <= 0)
                    		{
                    			if(!mergeItemStack(stack, BUCKET_RIGHT_IN, BUCKET_RIGHT_IN + 1, false))
                            	{
                            		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                                	{
                                		return ItemStack.EMPTY;
                                	}
                            	}
                    		} 
                    		else
                    		{
                    			if(!mergeItemStack(stack, BUCKET_LEFT_IN, BUCKET_LEFT_IN + 1, false))
                            	{
                            		if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                                	{
                                		return ItemStack.EMPTY;
                                	}
                            	}
                    		}
                    	}
                	}
                }
                else
                {
                	if(!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            
            if(stack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            
            else
            {
                slot.onSlotChanged();
            }

            if(stack.getCount() == result.getCount())
            {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, stack); 
        }
        return result;
    }

	@Override
	public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote)
        {
            this.clearContainer(playerIn, this.world, this.craftMatrix);
        }
    }
	
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
    {
		craftResult.setInventorySlotContents(0, CraftingManager.findMatchingResult(this.craftMatrix, this.world));
    }
	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}