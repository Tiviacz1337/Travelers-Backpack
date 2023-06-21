package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.*;
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
            if(slot <= inventory.getTier().getStorageSlotsWithCrafting() - 1)
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
            int[] sortOrder = slots;

            if(inventory.getSettingsManager().isCraftingGridLocked())
            {
                sortOrder = inventory.getTier().getSortOrder(inventory.getSettingsManager().isCraftingGridLocked());
                sortOrder = Arrays.stream(sortOrder).filter(i -> Arrays.stream(slots).anyMatch(j -> j == i)).toArray();
                List<Pair<Integer, ItemStack>> stacksList = new ArrayList<>();

                int k = 0;

                for(int j : slots)
                {
                    stacksList.add(Pair.of(j, stacks[k]));
                    k++;
                }

                List<ItemStack> sortedStacks = new ArrayList<>();

                do {
                    for(int i : sortOrder)
                    {
                        for(Iterator<Pair<Integer, ItemStack>> iterator = stacksList.iterator(); iterator.hasNext();)
                        {
                            Pair<Integer, ItemStack> pair = iterator.next();

                            if(i == pair.getFirst())
                            {
                                sortedStacks.add(pair.getSecond());
                                iterator.remove();
                            }
                        }
                    }
                }while(!stacksList.isEmpty());

                stacks = sortedStacks.toArray(ItemStack[]::new);
            }

            for(int i = 0; i < stacks.length; i++)
            {
                pairs.add(Pair.of(sortOrder[i], stacks[i]));
            }

            if(!inventory.getSettingsManager().isCraftingGridLocked())
            {
                //Sort
                pairs.sort(Comparator.comparing(Pair::getFirst));
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
            if(slot <= inventory.getTier().getStorageSlotsWithCrafting() - 1)
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

    public void clearMemory()
    {
        if(isSelectorActive(MEMORY))
        {
            memorySlots = new ArrayList<>();
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

    public void readUnsortableSlots(NbtCompound compound)
    {
        this.unsortableSlots = Arrays.stream(compound.getIntArray(UNSORTABLE_SLOTS)).boxed().collect(Collectors.toList());
    }

    public void writeMemorySlots(NbtCompound compound)
    {
        NbtList memorySlotsList = new NbtList();

        for(Pair<Integer, ItemStack> pair : memorySlots)
        {
            NbtCompound itemTag = new NbtCompound();
            itemTag.putInt("Slot", pair.getFirst());
            pair.getSecond().writeNbt(itemTag);
            memorySlotsList.add(itemTag);
        }

        compound.put(MEMORY_SLOTS, memorySlotsList);
    }

    public void readMemorySlots(NbtCompound compound)
    {
        NbtList tagList = compound.getList(MEMORY_SLOTS, NbtCompound.COMPOUND_TYPE);
        List<Pair<Integer, ItemStack>> pairs = new ArrayList<>();

        for(int i = 0; i < tagList.size(); i++)
        {
            NbtCompound itemTag = tagList.getCompound(i);
            int slot = itemTag.getInt("Slot");

            if(slot <= inventory.getTier().getStorageSlotsWithCrafting() - 1)
            {
                Pair<Integer, ItemStack> pair = Pair.of(slot, ItemStack.fromNbt(itemTag));
                pairs.add(pair);
            }
        }

        this.memorySlots = pairs;
    }
}