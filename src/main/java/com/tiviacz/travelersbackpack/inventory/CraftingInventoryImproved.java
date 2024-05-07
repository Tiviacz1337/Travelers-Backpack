package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.Iterator;
import java.util.List;

public class CraftingInventoryImproved implements RecipeInputInventory
{
    private final InventoryImproved craftingInventory;
    private final TravelersBackpackBaseScreenHandler handler;

    public CraftingInventoryImproved(ITravelersBackpackInventory inventory, TravelersBackpackBaseScreenHandler handler)
    {
        super();
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
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return this.craftingInventory.getStack(slot);
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
        this.craftingInventory.getStacks().clear();
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
    public List<ItemStack> getInputStacks()
    {
        return List.copyOf(this.craftingInventory.getStacks());
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