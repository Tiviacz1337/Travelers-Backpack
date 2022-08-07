package com.tiviacz.travelersbackpack.inventory.sorter;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SlotManager
{
    protected final ITravelersBackpackInventory inventory;
    protected List<Integer> unsortableSlots = new ArrayList<>();
    protected boolean isActive = false;

    private final String UNSORTABLE_SLOTS = "UnsortableSlots";

    public SlotManager(ITravelersBackpackInventory inventory)
    {
        this.inventory = inventory;
    }

    public List<Integer> getUnsortableSlots()
    {
        return this.unsortableSlots;
    }

    public boolean hasSlot(int slot)
    {
        return unsortableSlots.contains(slot);
    }

    public void setUnsortableSlots(int[] slots, boolean isFinal)
    {
        if(isActive())
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
        if(isActive())
        {
            if(slot <= 38)
            {
                if(hasSlot(slot))
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

    public void clearSlots()
    {
        unsortableSlots = new ArrayList<>();
    }

    public boolean isActive()
    {
        return this.isActive;
    }

    public void setActive(boolean bool)
    {
        this.isActive = bool;
    }

    public void writeUnsortableSlots(NbtCompound compound)
    {
        compound.putIntArray(UNSORTABLE_SLOTS, getUnsortableSlots().stream().mapToInt(i -> i).toArray());
    }

    public void readUnsortableSlots(NbtCompound compound)
    {
        this.unsortableSlots = Arrays.stream(compound.getIntArray(UNSORTABLE_SLOTS)).boxed().collect(Collectors.toList());
    }
}