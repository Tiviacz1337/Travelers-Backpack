package com.tiviacz.travelersbackpack.inventory.screen.slot;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FluidSlot extends Slot
{
    private final int index;
    private final ITravelersBackpackInventory inventory;

    public FluidSlot(ITravelersBackpackInventory inventory, int index, int x, int y)
    {
        super(inventory.getFluidSlotsInventory(), index, x, y);
        this.index = index;
        this.inventory = inventory;

        //0 - left in
        //1 - left out
        //2 - right in
        //3 - right out
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity)
    {
        if(inventory.getRows() <= 4)
        {
            if(index == 1 || index == 3)
            {
                return this.hasStack();
            }
        }
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        if(inventory.getRows() <= 4)
        {
            if(index == 1 || index == 3)
            {
                return this.hasStack();
            }
        }
        return true;
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        return inventory.getFluidSlotsInventory().isValid(index, stack) && super.canInsert(stack);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        inventory.updateTankSlots();
    }
}