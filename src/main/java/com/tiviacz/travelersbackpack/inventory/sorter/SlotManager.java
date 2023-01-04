package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SlotManager
{
    protected final ITravelersBackpackInventory inventory;
    protected List<Integer> unsortableSlots = new ArrayList<>();
    protected List<Pair<Integer, ItemStack>> memorySlots = new ArrayList<>();
    protected boolean isUnsortableActive = false;
    protected boolean isMemoryActive = false;

    private final String UNSORTABLE_SLOTS = "UnsortableSlots";
    private final String MEMORY_SLOTS = "MemorySlots";

    public static final byte UNSORTABLE = 0;
    public static final byte MEMORY = 1;

    public SlotManager(ITravelersBackpackInventory inventory)
    {
        this.inventory = inventory;
    }

    public List<Integer> getUnsortableSlots()
    {
        return this.unsortableSlots;
    }

    public List<Pair<Integer, ItemStack>> getMemorySlots()
    {
        return this.memorySlots;
    }

    public boolean isSlot(byte type, int slot)
    {
        if(type == UNSORTABLE)
        {
            return unsortableSlots.contains(slot);
        }

        if(type == MEMORY)
        {
            for(Pair<Integer, ItemStack> pair : memorySlots)
            {
                if(pair.getFirst() == slot) return true;
            }
        }
        return false;
    }

    public void setUnsortableSlots(int[] slots, boolean isFinal)
    {
        if(isSelectorActive(UNSORTABLE))
        {
            unsortableSlots = Arrays.stream(slots).boxed().collect(Collectors.toList());

            if(isFinal)
            {
                setChanged();
            }
        }
    }

    public void setUnsortableSlot(int slot)
    {
        if(isSelectorActive(UNSORTABLE))
        {
            if(slot <= 38)
            {
                if(isSlot(UNSORTABLE, slot))
                {
                    unsortableSlots.remove((Object)slot);
                }
                else
                {
                    unsortableSlots.add(slot);
                }
            }
        }
    }

    public void setMemorySlots(int[] slots, ItemStack[] stacks, boolean isFinal)
    {
        if(isSelectorActive(MEMORY))
        {
            List<Pair<Integer, ItemStack>> pairs = new ArrayList<>();

            for(int i = 0; i < slots.length; i++)
            {
                pairs.add(Pair.of(slots[i], stacks[i]));
            }

            this.memorySlots = pairs;

            if(isFinal)
            {
                setChanged();
            }
        }
    }

    public void setMemorySlot(int slot, ItemStack stack)
    {
        if(isSelectorActive(MEMORY))
        {
            if(slot <= 38)
            {
                if(isSlot(MEMORY, slot))
                {
                    memorySlots.removeIf(p -> p.getFirst() == slot);
                }
                else
                {
                    memorySlots.add(Pair.of(slot, stack));
                }
            }
        }
    }

    public void setChanged()
    {
        if(inventory.getScreenID() != Reference.TILE_SCREEN_ID)
        {
            inventory.setDataChanged(ITravelersBackpackInventory.SLOT_DATA);
        }
        else
        {
            inventory.setChanged();
        }
    }

    public boolean isSelectorActive(byte type)
    {
        switch (type) {
            case UNSORTABLE: return this.isUnsortableActive;
            case MEMORY: return this.isMemoryActive;
            default: return false;
        }
    }

    public void setSelectorActive(byte type, boolean bool)
    {
        switch(type)
        {
            case UNSORTABLE: this.isUnsortableActive = bool;
            case MEMORY:  this.isMemoryActive = bool;
        }
    }

    public void saveUnsortableSlots(CompoundNBT compound)
    {
        compound.putIntArray(UNSORTABLE_SLOTS, getUnsortableSlots().stream().mapToInt(i -> i).toArray());
    }

    public void loadUnsortableSlots(CompoundNBT compound)
    {
        this.unsortableSlots = Arrays.stream(compound.getIntArray(UNSORTABLE_SLOTS)).boxed().collect(Collectors.toList());
    }

    public void saveMemorySlots(CompoundNBT compound)
    {
        ListNBT memorySlotsList = new ListNBT();

        for(Pair<Integer, ItemStack> pair : memorySlots)
        {
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.putInt("Slot", pair.getFirst());
            pair.getSecond().save(itemTag);
            memorySlotsList.add(itemTag);
        }

        compound.put(MEMORY_SLOTS, memorySlotsList);
    }

    public void loadMemorySlots(CompoundNBT compound)
    {
        ListNBT tagList = compound.getList(MEMORY_SLOTS, Constants.NBT.TAG_COMPOUND);
        List<Pair<Integer, ItemStack>> pairs = new ArrayList<>();

        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT itemTag = tagList.getCompound(i);
            int slot = itemTag.getInt("Slot");

            if(slot <= 38)
            {
                Pair<Integer, ItemStack> pair = Pair.of(slot, ItemStack.of(itemTag));
                pairs.add(pair);
            }
        }

        this.memorySlots = pairs;
    }
}