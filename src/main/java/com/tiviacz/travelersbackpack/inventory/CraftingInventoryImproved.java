package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;

import java.util.Iterator;

public class CraftingInventoryImproved extends CraftingInventory
{
    private final InventoryImproved craftingInventory;
    private final TravelersBackpackBaseScreenHandler handler;

    public CraftingInventoryImproved(ITravelersBackpackInventory inventory, TravelersBackpackBaseScreenHandler handler)
    {
        super(handler, 3, 3);
        this.craftingInventory = inventory.getCraftingGridInventory();
        this.handler = handler;
    }

    @Override
    public int size()
    {
        return this.craftingInventory.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.craftingInventory.isEmpty();

        /*for(int i = 0; i < size(); i++)
        {
            if(!getStack(i).isEmpty())
            {
                return false;
            }
        }
        return true; */
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return this.craftingInventory.getStack(slot);
        //return slot >= this.size() ? ItemStack.EMPTY : this.craftingStacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        return Inventories.removeStack(this.craftingInventory.getStacks(), slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemStack = Inventories.splitStack(this.craftingInventory.getStacks(), slot, amount);
        if(!itemStack.isEmpty())
        {
            this.handler.onContentChanged(this);
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        this.craftingInventory.getStacks().set(slot, stack);
        this.handler.onContentChanged(this);
        this.markDirty();
    }

    @Override
    public void markDirty()
    {
        this.craftingInventory.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clear()
    {
        this.craftingInventory.clear();
        this.markDirty();
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
        Iterator iterator = this.craftingInventory.getStacks().iterator();

        while(iterator.hasNext())
        {
            ItemStack itemStack = (ItemStack)iterator.next();
            finder.addUnenchantedInput(itemStack);
        }
    }
}