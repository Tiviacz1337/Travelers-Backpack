package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public abstract class InventoryImproved implements Inventory
{
    protected final DefaultedList<ItemStack> stacks;

    public InventoryImproved(DefaultedList<ItemStack> stacks)
    {
        this.stacks = stacks;
    }

    public DefaultedList<ItemStack> getStacks()
    {
        return this.stacks;
    }

    @Override
    public int size()
    {
        return this.stacks.size();
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
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemstack = Inventories.splitStack(this.stacks, slot, amount);

        if(!itemstack.isEmpty())
        {
            this.markDirty();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        ItemStack itemStack = this.stacks.get(slot);
        if(itemStack.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        else
        {
            this.stacks.set(slot, ItemStack.EMPTY);
            return itemStack;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        this.stacks.set(slot, stack);
        if(!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack())
        {
            stack.setCount(this.getMaxCountPerStack());
        }

       this.markDirty();
    }

    @Override
    public abstract void markDirty();

    @Override
    public boolean isValid(int slot, ItemStack stack)
    {
        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.isIn(ModTags.BLACKLISTED_ITEMS);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clear()
    {

    }
}