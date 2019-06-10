package com.tiviacz.travellersbackpack.gui.inventory;

import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryCraftingImproved extends InventoryCrafting
{
    private final NonNullList<ItemStack> stackList;
    private final int inventoryWidth;
    private final int inventoryHeight;
    private final Container eventHandler;
    private final IInventoryTravellersBackpack inv;

    public InventoryCraftingImproved(IInventoryTravellersBackpack inventory, EntityPlayer player, Container eventHandlerIn, int width, int height)
    {
    	super(null, width, height);
    	
        this.stackList = inventory.getCraftingGridInventory();
        this.eventHandler = eventHandlerIn;
        this.inventoryWidth = width;
        this.inventoryHeight = height;
        this.inv = inventory;
    }
    
    @Override
    public void markDirty()
    {
    	this.inv.markDirty();
    }

    @Override
    public int getSizeInventory()
    {
        return this.stackList.size();
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack itemstack : this.stackList)
        {
            if(!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(index);
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int column)
    {
        return row >= 0 && row < this.inventoryWidth && column >= 0 && column <= this.inventoryHeight ? this.getStackInSlot(row + column * this.inventoryWidth) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
    	return ItemStackHelper.getAndRemove(this.stackList, index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
    	ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);
    	 
	    if(!itemstack.isEmpty())
	    {
	    	this.eventHandler.onCraftMatrixChanged(this);
	    	this.markDirty();
		}
	    
        return itemstack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
    	this.stackList.set(index, stack);
        this.eventHandler.onCraftMatrixChanged(this);
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void clear()
    {
        this.stackList.clear();
    }

    @Override
    public int getHeight()
    {
        return this.inventoryHeight;
    }

    @Override
    public int getWidth()
    {
        return this.inventoryWidth;
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper)
    {
        for(ItemStack itemstack : this.stackList)
        {
            helper.accountStack(itemstack);
        }
    }
}