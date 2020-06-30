package com.tiviacz.travelersbackpack.gui.container.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotDisabled extends Slot
{
	public SlotDisabled(IInventory inventoryIn, int index, int xPosition, int yPosition) 
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn)
    {
        return false;
    }
}