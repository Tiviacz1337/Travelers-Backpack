package com.tiviacz.travellersbackpack.gui.container;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotBackpack;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotDisabled;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotFluid;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotTool;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryCraftingImproved;
import com.tiviacz.travellersbackpack.util.EnumSource;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ContainerTravellersBackpack extends Container 
{
	public InventoryPlayer playerInv;
	public IInventoryTravellersBackpack inv;
	public InventoryCraftingImproved craftMatrix;
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	public World world;
	public EnumSource source;
	
	private final int numRows = 3;
	private final int numColumnsUpper = 8;
	private final int numColumnsLower = 5;
	
	private final int BACKPACK_INV_START = 10, BACKPACK_INV_END = 48;
	private final int PLAYER_INV_START = 49, PLAYER_HOT_END = 84;
	private final int BUCKET_LEFT_IN = 85, BUCKET_LEFT_OUT = 86;
	private final int BUCKET_RIGHT_IN = 87, BUCKET_RIGHT_OUT = 88;
	private final int TOOL_START = 89, TOOL_END = 90;
	
	public ContainerTravellersBackpack(World world, InventoryPlayer playerInv, IInventoryTravellersBackpack inventory, EnumSource source)
	{
		this.world = world;
		this.playerInv = playerInv;
		this.inv = inventory;
		this.source = source;
		this.craftMatrix = new InventoryCraftingImproved(inventory, this, 3, 3);
		int currentItemIndex = playerInv.currentItem;
		
		this.addCraftMatrix();
		this.addCraftResult(playerInv.player);
		
		this.addBackpackInventory(inventory);	
		this.addPlayerInventoryAndHotbar(playerInv, currentItemIndex);
		this.addFluidTanks(inventory);
		this.addToolSlots(inventory);
		
		this.onCraftMatrixChanged(inventory);
	}
	
	public void addCraftMatrix()
	{
		for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new SlotBackpack(this.craftMatrix, j + i * 3, 152 + j * 18, 61 + i * 18));
            }
        }
	}
	
	public void addCraftResult(EntityPlayer player)
	{
		this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 226, 97));
	}
	
	public void addBackpackInventory(IInventoryTravellersBackpack inventory)
	{
		int slot = 9;
		
		//24 Slots
		
		for(int i = 0; i < this.numRows; ++i)
	    {
			for(int j = 0; j < this.numColumnsUpper; ++j)
			{
				this.addSlotToContainer(new SlotBackpack(inventory, slot++, 62 + j * 18, 7 + i * 18));
			}
	    }
		
		//15 Slots
		
		for(int i = 0; i < this.numRows; ++i)
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
			if(x == currentItemIndex && !this.inv.hasTileEntity() && !CapabilityUtils.isWearingBackpack(this.playerInv.player))
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
            
            if(index >= 0 && index <= BACKPACK_INV_END)
            {
            	if(index == 9)
            	{
            		stack.getItem().onCreated(stack, this.world, player);
            		
            		if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                    {
                        return ItemStack.EMPTY;
                    }
            		
            		slot.onSlotChange(stack, result);
            		this.craftMatrix.markDirty();
            	}
            	
            	else if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
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
                    		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                            {
                                return ItemStack.EMPTY;
                            }
                    	}
                	}
                	else
                	{
                		if(!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
                        {
                			if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
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
                		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                		{
                			if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                    		{
                    			return ItemStack.EMPTY;
                    		}
                		}
                		
                		this.inv.updateTankSlots();
                	}
                	
                	if(index == BUCKET_LEFT_IN || index == BUCKET_RIGHT_IN)
                	{
                		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                		{
                			if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, false))
                    		{
                    			return ItemStack.EMPTY;
                    		}
                		}
                	}
                	else
                	{
                		if(SlotFluid.checkFluid(stack, this.inv.getLeftTank(), this.inv.getRightTank()) && this.inv.getStackInSlot(BUCKET_LEFT_IN).isEmpty())
                    	{
                    		if(!mergeItemStack(stack, BUCKET_LEFT_IN, BUCKET_LEFT_IN + 1, false))
                        	{
                        		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                            	{
                            		return ItemStack.EMPTY;
                            	}
                        	}
                    	}
                		
                    	if(SlotFluid.checkFluid(stack, this.inv.getRightTank(), this.inv.getLeftTank()) && this.inv.getStackInSlot(BUCKET_RIGHT_IN).isEmpty())
                    	{
                    		if(!mergeItemStack(stack, BUCKET_RIGHT_IN, BUCKET_RIGHT_IN + 1, false))
                        	{
                        		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                            	{
                            		return ItemStack.EMPTY;
                            	}
                        	}
                    	}
                    	
                    	else
                    	{
                    		if(this.inv.getLeftTank().getFluid() == null || this.inv.getLeftTank().getFluidAmount() <= 0)
                    		{
                    			if(!mergeItemStack(stack, BUCKET_RIGHT_IN, BUCKET_RIGHT_IN + 1, false))
                            	{
                            		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                                	{
                                		return ItemStack.EMPTY;
                                	}
                            	}
                    		} 
                    		else
                    		{
                    			if(!mergeItemStack(stack, BUCKET_LEFT_IN, BUCKET_LEFT_IN + 1, false))
                            	{
                            		if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
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
                	if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
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
        
        if(!this.inv.getStackInSlot(Reference.BUCKET_IN_LEFT).isEmpty() || !this.inv.getStackInSlot(Reference.BUCKET_OUT_LEFT).isEmpty() || !this.inv.getStackInSlot(Reference.BUCKET_IN_RIGHT).isEmpty() || !this.inv.getStackInSlot(Reference.BUCKET_OUT_RIGHT).isEmpty())
        {
        	this.world.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
        }
        
        if(!this.world.isRemote)
        {
        	this.clearBucketSlot(playerIn, this.world, this.inv, Reference.BUCKET_IN_LEFT);
        	this.clearBucketSlot(playerIn, this.world, this.inv, Reference.BUCKET_IN_RIGHT);
        	this.clearBucketSlot(playerIn, this.world, this.inv, Reference.BUCKET_OUT_LEFT);
        	this.clearBucketSlot(playerIn, this.world, this.inv, Reference.BUCKET_OUT_RIGHT);
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
	
	private void clearBucketSlot(EntityPlayer playerIn, World worldIn, IInventoryTravellersBackpack inventoryIn, int index)
    {
        if(!playerIn.isEntityAlive() || playerIn instanceof EntityPlayerMP && ((EntityPlayerMP)playerIn).hasDisconnected())
        {
        	playerIn.dropItem(inventoryIn.removeStackFromSlot(index), false);
        }
        else
        {
            playerIn.inventory.placeItemBackInInventory(worldIn, inventoryIn.removeStackFromSlot(index));
        }
    }
}