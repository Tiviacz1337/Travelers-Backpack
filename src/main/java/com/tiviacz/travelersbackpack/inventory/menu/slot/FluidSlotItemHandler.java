package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FluidSlotItemHandler extends SlotItemHandler
{
    private final int index;
    private final ITravelersBackpackContainer container;

    public FluidSlotItemHandler(ITravelersBackpackContainer container, int index, int xPosition, int yPosition)
    {
        super(container.getFluidSlotsHandler(), index, xPosition, yPosition);
        this.index = index;
        this.container = container;

        //0 - left in
        //1 - left out
        //2 - right in
        //3 - right out
    }

    @Override
    public boolean mayPickup(Player playerIn)
    {
        if(container.getRows() <= 4)
        {
            if(index == 1 || index == 3)
            {
                return this.hasItem();
            }
        }
        return true;
    }

    @Override
    public boolean isActive()
    {
        if(container.getRows() <= 4)
        {
            if(index == 1 || index == 3)
            {
                return this.hasItem();
            }
        }
        return true;
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        container.updateTankSlots();
    }
}