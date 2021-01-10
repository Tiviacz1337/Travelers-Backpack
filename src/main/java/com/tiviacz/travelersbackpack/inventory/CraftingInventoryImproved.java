package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CraftingInventoryImproved extends CraftingInventory
{
    private final ItemStackHandler craftingInventory;
    private final Container eventHandler;

    public CraftingInventoryImproved(ITravelersBackpackInventory inventory, Container eventHandlerIn)
    {
        super(eventHandlerIn, 3, 3);
        this.craftingInventory = inventory.getCraftingGridInventory();
        this.eventHandler = eventHandlerIn;
    }

    public int getSizeInventory()
    {
        return this.craftingInventory.getSlots();
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < craftingInventory.getSlots(); i++)
        {
            if(!craftingInventory.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.craftingInventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        if(index >= 0 && index < craftingInventory.getSlots())
        {
            ItemStack stack = getStackInSlot(index).copy();
            craftingInventory.setStackInSlot(index, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = index >= 0 && index < craftingInventory.getSlots() && !craftingInventory.getStackInSlot(index).isEmpty() && count > 0 ? craftingInventory.getStackInSlot(index).split(count) : ItemStack.EMPTY;

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
        this.craftingInventory.setStackInSlot(index, stack);
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public void markDirty()
    {
    }

    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    public void clear() { }

    @Override
    public int getHeight()
    {
        return 3;
    }

    @Override
    public int getWidth()
    {
        return 3;
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper)
    {
        for(int i = 0; i < craftingInventory.getSlots(); i++)
        {
            helper.accountPlainStack(craftingInventory.getStackInSlot(i));
        }
    }
}