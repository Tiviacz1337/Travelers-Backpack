package com.tiviacz.travelersbackpack.gui.container;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.gui.container.slots.SlotBackpack;
import com.tiviacz.travelersbackpack.gui.container.slots.SlotDisabled;
import com.tiviacz.travelersbackpack.gui.container.slots.SlotFluid;
import com.tiviacz.travelersbackpack.gui.container.slots.SlotTool;
import com.tiviacz.travelersbackpack.gui.inventory.IInventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryCraftingImproved;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.network.client.SyncGuiPacket;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.EnumSource;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

public class ContainerTravelersBackpack extends Container
{
	public InventoryPlayer playerInv;
	public IInventoryTravelersBackpack inv;
	public InventoryCraftingImproved craftMatrix;
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	public World world;
	public EnumSource source;
	public int[] cachedFields;

	private final int BACKPACK_INV_START = 10, BACKPACK_INV_END = 48;
	private final int TOOL_START = 49, TOOL_END = 50;
	private final int BUCKET_LEFT_IN = 51, BUCKET_LEFT_OUT = 52;
	private final int BUCKET_RIGHT_IN = 53, BUCKET_RIGHT_OUT = 54;
	private final int PLAYER_INV_START = 55, PLAYER_HOT_END = 90;
	
	public ContainerTravelersBackpack(World world, InventoryPlayer playerInv, IInventoryTravelersBackpack inventory, EnumSource source)
	{
		this.world = world;
		this.playerInv = playerInv;
		this.inv = inventory;
		this.source = source;
		this.craftMatrix = new InventoryCraftingImproved(inventory, this, 3, 3);
		int currentItemIndex = playerInv.currentItem;

		//Result Slot, Crafting Grid
		this.addCraftMatrix();
		this.addCraftResult(playerInv.player);

		//Backpack Inventory
		this.addBackpackInventory(inventory);

		//Functional Slots
		this.addToolSlots(inventory);
		this.addFluidSlots(inventory);

		//Player Inventory and Hotbar
		this.addPlayerInventoryAndHotbar(playerInv, currentItemIndex);

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
	
	public void addBackpackInventory(IInventoryTravelersBackpack inventory)
	{
		int slot = 9;
		
		//24 Slots
		
		for(int i = 0; i < 3; ++i)
	    {
			for(int j = 0; j < 8; ++j)
			{
				this.addSlotToContainer(new SlotBackpack(inventory, slot++, 62 + j * 18, 7 + i * 18));
			}
	    }
		
		//15 Slots
		
		for(int i = 0; i < 3; ++i)
	    {
			for(int j = 0; j < 5; ++j)
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
			if(x == currentItemIndex && this.source == EnumSource.ITEM)
			{
				this.addSlotToContainer(new SlotDisabled(playerInv, x, 44 + x*18, 183));
			}
			else
			{
				this.addSlotToContainer(new Slot(playerInv, x, 44 + x*18, 183));
			}
		}		
	}

	public void addToolSlots(IInventoryTravelersBackpack inventory)
	{
		//Upper Tool Slot
		this.addSlotToContainer(new SlotTool(playerInv.player, source, inventory, Reference.TOOL_UPPER, 44, 79));

		//Lower Tool slot
		this.addSlotToContainer(new SlotTool(playerInv.player, source, inventory, Reference.TOOL_LOWER, 44, 97));
	}
	
	public void addFluidSlots(IInventoryTravelersBackpack inventory)
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
	
	@Override
    public void detectAndSendChanges()
    {   
		super.detectAndSendChanges();
	
		InventoryTravelersBackpack itemInv = CapabilityUtils.getBackpackInv(playerInv.player);
        
		if(itemInv != null)
		{
			if(cachedFields == null)
	        {
	            cachedFields = new int[itemInv.getFieldCount()];
	            Arrays.fill(cachedFields, -1);
	        }
	        
	        for(IContainerListener listener : listeners)
		    {
	        	for(int i = 0; i < itemInv.getFieldCount(); i++)
		        {
		            if(cachedFields[i] != itemInv.getField(i))
		            {
		            	cachedFields[i] = itemInv.getField(i);
		                    
		                if(cachedFields[i] > Short.MAX_VALUE || cachedFields[i] < Short.MIN_VALUE)
		                {
		                    TravelersBackpack.NETWORK.sendTo(new SyncGuiPacket(i, cachedFields[i]), (EntityPlayerMP) listener);
		                }
		                else
		                {
		                	listener.sendWindowProperty(this, i, cachedFields[i]);
		                }
		            }
		        }
		    }
		}
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        inv.setField(id, data);
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		if(this.source == EnumSource.TILE)
		{
			TileEntity tile = this.world.getTileEntity(this.inv.getPosition());
			
			if(tile != null)
			{
				TileEntityTravelersBackpack backpack = (TileEntityTravelersBackpack)tile;
				return backpack.isUsableByPlayer(playerIn);
			}
			return false;
		}
		else
		{
			return true;
		}
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
            
            if(index >= 0 && index <= BUCKET_RIGHT_OUT)
            {
            	if(index == 0)
            	{
            		stack.getItem().onCreated(stack, this.world, player);
            		
            		if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                    {
                        return ItemStack.EMPTY;
                    }
            		
            		slot.onSlotChange(stack, result);
            		this.craftMatrix.markDirty();
            	}
            	
            	else if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(index >= PLAYER_INV_START)
			{
				if(SlotTool.isValid(stack))
				{
					if(!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
					{
						if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
						{
							return ItemStack.EMPTY;
						}
					}
				}

				if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
				{
					return ItemStack.EMPTY;
				}
			}
            
        /*    if(index >= PLAYER_INV_START)
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
            } */
            
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
		if(!ConfigHandler.server.disableCrafting)
		{
			craftResult.setInventorySlotContents(0, CraftingManager.findMatchingResult(this.craftMatrix, this.world));
		}
    }
	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
	
	private void clearBucketSlot(EntityPlayer playerIn, World worldIn, IInventoryTravelersBackpack inventoryIn, int index)
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