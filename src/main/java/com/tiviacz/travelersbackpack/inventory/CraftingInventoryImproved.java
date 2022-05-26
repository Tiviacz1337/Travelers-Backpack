package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.Iterator;

public class CraftingInventoryImproved extends CraftingInventory
{
    private final ITravelersBackpackInventory inventory;
    private final DefaultedList<ItemStack> craftingStacks;
    private final ScreenHandler handler;

    public CraftingInventoryImproved(ITravelersBackpackInventory inventory, ScreenHandler handler)
    {
        super(handler, 3, 3);
        this.inventory = inventory;
        this.craftingStacks = inventory.getCraftingGridInventory().getStacks();
        this.handler = handler;
    }

    @Override
    public int size()
    {
        return this.craftingStacks.size();
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < size(); i++)
        {
            if(!getStack(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return slot >= this.size() ? ItemStack.EMPTY : this.craftingStacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        return Inventories.removeStack(this.craftingStacks, slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemStack = Inventories.splitStack(this.craftingStacks, slot, amount);
        if(!itemStack.isEmpty())
        {
            this.handler.onContentChanged(this);
        }
        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        this.craftingStacks.set(slot, stack);
        this.handler.onContentChanged(this);
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clear()
    {
        this.craftingStacks.clear();
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
    public void provideRecipeInputs(RecipeMatcher finder)
    {
        Iterator iterator = this.craftingStacks.iterator();

        while(iterator.hasNext())
        {
            ItemStack itemStack = (ItemStack)iterator.next();
            finder.addUnenchantedInput(itemStack);
        }

    }
}