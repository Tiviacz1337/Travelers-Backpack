package com.tiviacz.travelersbackpackold.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpackold.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpackold.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpackold.util.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SlotManager
{
    protected final ITravelersBackpackInventory inventory;
    protected List<Integer> unsortableSlots = new ArrayList<>();
    protected List<Pair<Integer, ItemStack>> memorySlots = new ArrayList<>();

    protected boolean isUnsortableActive = false;
    protected boolean isMemoryActive = false;

    public static final String UNSORTABLE_SLOTS = "UnsortableSlots";
    public static final String MEMORY_SLOTS = "MemorySlots";

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

    public void setUnsortableSlots(List<Integer> slots, boolean isFinal)
    {
        if(isSelectorActive(UNSORTABLE))
        {
            unsortableSlots = slots; //Arrays.stream(slots).boxed().collect(Collectors.toList());

            if(isFinal)
            {
                setChanged();
            }
        }
    }

    public void setMemorySlots(List<Integer> slots, List<ItemStack> stacks, boolean isFinal)
    {
        if(isSelectorActive(MEMORY))
        {
            List<Pair<Integer, ItemStack>> pairs = new ArrayList<>();

            for(int i = 0; i < stacks.size(); i++)
            {
                pairs.add(Pair.of(slots.get(i), stacks.get(i)));
            }

            //Sort
            pairs.sort(Comparator.comparing(Pair::getFirst));

            this.memorySlots = pairs;

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
            if(slot <= inventory.getInventory().size() - 1)
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

    public void setMemorySlot(int slot, ItemStack stack)
    {
        if(isSelectorActive(MEMORY))
        {
            if(slot <= inventory.getInventory().size() - 1)
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

    public void clearUnsortables()
    {
        if(isSelectorActive(UNSORTABLE))
        {
            unsortableSlots = new ArrayList<>();
        }
    }

    public void setChanged()
    {
        if(inventory.getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            inventory.markDataDirty(ITravelersBackpackInventory.SLOT_DATA);
        }
        else
        {
            inventory.markDirty();
        }
    }

    public boolean isSelectorActive(byte type)
    {
        return switch (type) {
            case UNSORTABLE -> this.isUnsortableActive;
            case MEMORY -> this.isMemoryActive;
            default -> false;
        };
    }

    public void setSelectorActive(byte type, boolean bool)
    {
        switch(type)
        {
            case UNSORTABLE -> this.isUnsortableActive = bool;
            case MEMORY -> this.isMemoryActive = bool;
        }
    }

    public void writeUnsortableSlots(NbtCompound compound)
    {
        compound.putIntArray(UNSORTABLE_SLOTS, getUnsortableSlots().stream().mapToInt(i -> i).toArray());
    }

    public void writeUnsortableSlots(ItemStack stack)
    {
        Slots newSlots = new Slots(getUnsortableSlots(), getMemorySlots());
        stack.set(ModDataComponents.SLOTS, newSlots);
    }

    public void readUnsortableSlots(NbtCompound compound)
    {
        this.unsortableSlots = Arrays.stream(compound.getIntArray(UNSORTABLE_SLOTS)).boxed().collect(Collectors.toList());
    }

    public void readUnsortableSlots(ItemStack stack)
    {
        this.unsortableSlots = new ArrayList<>(stack.getOrDefault(ModDataComponents.SLOTS, Slots.createDefault()).unsortables());
    }

    public void writeMemorySlots(RegistryWrapper.WrapperLookup registryLookup, NbtCompound compound)
    {
        NbtList memorySlotsList = new NbtList();

        for(Pair<Integer, ItemStack> pair : memorySlots)
        {
            if(!pair.getSecond().isEmpty())
            {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", pair.getFirst());
                memorySlotsList.add(pair.getSecond().encode(registryLookup, itemTag));
            }
        }

        compound.put(MEMORY_SLOTS, memorySlotsList);
    }

    public void writeMemorySlots(ItemStack stack)
    {
        Slots newSlots = new Slots(getUnsortableSlots(), getMemorySlots());
        stack.set(ModDataComponents.SLOTS, newSlots);
    }

    public void readMemorySlots(RegistryWrapper.WrapperLookup registryLookup, NbtCompound compound)
    {
        NbtList tagList = compound.getList(MEMORY_SLOTS, NbtCompound.COMPOUND_TYPE);
        List<Pair<Integer, ItemStack>> pairs = new ArrayList<>();

        for(int i = 0; i < tagList.size(); i++)
        {
            NbtCompound itemTag = tagList.getCompound(i);
            int slot = itemTag.getInt("Slot");

            if(slot <= inventory.getInventory().size() - 1)
            {
                Pair<Integer, ItemStack> pair = Pair.of(slot, ItemStack.fromNbtOrEmpty(registryLookup, itemTag));
                pairs.add(pair);
            }
        }

        this.memorySlots = pairs;
    }

    public void readMemorySlots(ItemStack stack)
    {
        this.memorySlots = new ArrayList<>(stack.getOrDefault(ModDataComponents.SLOTS, Slots.createDefault()).memory());
    }

    public Slots getSlots()
    {
        return new Slots(getUnsortableSlots(), getMemorySlots());
    }

    public SlotManager getManager(Slots slots)
    {
        this.unsortableSlots = new ArrayList<>(slots.unsortables());
        this.memorySlots = new ArrayList<>(slots.memory());
        return this;
    }
}