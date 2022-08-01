package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class CraftingInventoryImproved extends CraftingInventory
{
    private final ItemStackHandler craftingInventory;
    private final Container eventHandler;
    public boolean checkChanges = true;

    public CraftingInventoryImproved(ITravelersBackpackInventory inventory, Container eventHandlerIn)
    {
        super(eventHandlerIn, 3, 3);
        this.craftingInventory = inventory.getCraftingGridInventory();
        this.eventHandler = eventHandlerIn;
    }

    @Override
    public int getContainerSize()
    {
        return this.craftingInventory.getSlots();
    }

    public NonNullList<ItemStack> getStackList()
    {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for(int i = 0; i < craftingInventory.getSlots(); i++)
        {
            stacks.add(i, getItem(i));
        }
        return stacks;
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < getContainerSize(); i++)
        {
            if(!getItem(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index)
    {
        return index >= this.getContainerSize() ? ItemStack.EMPTY : this.craftingInventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        if(index >= 0 && index < this.getContainerSize())
        {
            ItemStack stack = getItem(index).copy();
            setItem(index, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        ItemStack itemstack = index >= 0 && index < getContainerSize() && !getItem(index).isEmpty() && count > 0 ? getItem(index).split(count) : ItemStack.EMPTY;

        if(!itemstack.isEmpty())
        {
            if(checkChanges)
            {
                this.eventHandler.slotsChanged(this);
            }
        }
        return itemstack;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        this.craftingInventory.setStackInSlot(index, stack);
        if(checkChanges)this.eventHandler.slotsChanged(this);
    }

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
        for(int i = 0; i < getContainerSize(); i++)
        {
            helper.accountSimpleStack(getItem(i));
        }
    }
}