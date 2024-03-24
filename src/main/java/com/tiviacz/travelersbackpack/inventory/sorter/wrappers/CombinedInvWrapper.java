package com.tiviacz.travelersbackpack.inventory.sorter.wrappers;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CombinedInvWrapper implements Inventory
{
    protected final ITravelersBackpackInventory inv;
    protected final Inventory[] inventory; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedInvWrapper(ITravelersBackpackInventory inv, Inventory... inventory)
    {
        this.inv = inv;
        this.inventory = inventory;
        this.baseIndex = new int[inventory.length];
        int index = 0;
        for (int i = 0; i < inventory.length; i++)
        {
            index += inventory[i].size();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected Inventory getInventoryFromIndex(int index)
    {
        if (index < 0 || index >= inventory.length)
        {
            return null;
        }
        return inventory[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public void setStack(int slot, @NotNull ItemStack stack)
    {
        int index = getIndexForSlot(slot);
        Inventory inv = getInventoryFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        inv.setStack(slot, stack);
    }

    @Override
    public int size()
    {
        return slotCount;
    }

    @Override
    @NotNull
    public ItemStack getStack(int slot)
    {
        int index = getIndexForSlot(slot);
        Inventory inv = getInventoryFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return inv.getStack(slot);
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack)
    {
        int index = getIndexForSlot(slot);
        Inventory inventory = getInventoryFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return inventory.isValid(localSlot, stack);
    }

    //Dummy

    @Override
    public void markDirty()
    {
        inv.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public void clear() {}
}