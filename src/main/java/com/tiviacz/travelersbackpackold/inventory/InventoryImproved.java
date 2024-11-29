package com.tiviacz.travelersbackpackold.inventory;

import com.tiviacz.travelersbackpackold.inventory.screen.slot.BackpackSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

public class InventoryImproved implements Inventory
{
    protected DefaultedList<ItemStack> stacks;

    public InventoryImproved(int size)
    {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public InventoryImproved(DefaultedList<ItemStack> stacks)
    {
        this.stacks = stacks;
    }

    public void setSize(int size)
    {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
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
        if(slot < 0 || slot >= this.stacks.size())
        {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemstack = Inventories.splitStack(this.stacks, slot, amount);

        if(!itemstack.isEmpty())
        {
            this.onContentsChanged(slot);
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

            //Call onContentsChanged
            this.onContentsChanged(slot);

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
        this.onContentsChanged(slot);
    }

    public NbtCompound writeNbt(RegistryWrapper.WrapperLookup lookup)
    {
        NbtList nbtTagList = new NbtList();

        for(int i = 0; i < this.stacks.size(); ++i)
        {
            if(!this.stacks.get(i).isEmpty())
            {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                nbtTagList.add(this.stacks.get(i).encode(lookup, itemTag));
            }
        }
        NbtCompound nbt = new NbtCompound();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    public void readNbt(RegistryWrapper.WrapperLookup lookup, NbtCompound nbt)
    {
        this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        NbtList tagList = nbt.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i)
        {
            NbtCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if(slot >= 0 && slot < this.stacks.size())
            {
                ItemStack.fromNbt(lookup, itemTags).ifPresent(stack -> stacks.set(slot, stack));
            }
        }
    }

    @Override
    public void markDirty() {
        //Save here
    }

    public void onContentsChanged(int index) {
        //Save slots here
    }

    @Override
    public boolean isValid(int slot, ItemStack stack)
    {
        return BackpackSlot.isValid(stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clear() {}
}