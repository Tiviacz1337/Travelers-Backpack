package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public abstract class InventoryImproved implements Inventory
{
    protected DefaultedList<ItemStack> stacks;

    public InventoryImproved(int size)
    {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
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

    public NbtCompound writeNbt()
    {
        NbtList nbtTagList = new NbtList();

        for(int i = 0; i < this.stacks.size(); ++i)
        {
            if (!this.stacks.get(i).isEmpty())
            {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                this.stacks.get(i).writeNbt(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        NbtCompound nbt = new NbtCompound();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        NbtList tagList = nbt.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i)
        {
            NbtCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if(slot >= 0 && slot < this.stacks.size())
            {
                this.stacks.set(slot, ItemStack.fromNbt(itemTags));
            }
        }
    }

    public void readNbtOld(NbtCompound nbt, boolean isInventory)
    {
        this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        NbtList tagList = isInventory ? nbt.getList(ITravelersBackpackInventory.INVENTORY, 10) : nbt.getList(ITravelersBackpackInventory.CRAFTING_INVENTORY, 10);

        for(int i = 0; i < tagList.size(); ++i)
        {
            NbtCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if(slot >= 0 && slot < this.stacks.size())
            {
                this.stacks.set(slot, ItemStack.fromNbt(itemTags));
            }
        }
        //nbt.remove(isInventory ? ITravelersBackpackInventory.INVENTORY : ITravelersBackpackInventory.CRAFTING_INVENTORY);
    }

    @Override
    public abstract void markDirty();

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
    public void clear()
    {

    }
}