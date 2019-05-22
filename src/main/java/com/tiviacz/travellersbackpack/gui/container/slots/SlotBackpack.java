package com.tiviacz.travellersbackpack.gui.container.slots;

import com.tiviacz.travellersbackpack.init.ModItems;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBackpack extends Slot
{
	public SlotBackpack(IInventory inventoryIn, int index, int xPosition, int yPosition) 
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
        return stack.getItem() == ModItems.TRAVELLERS_BACKPACK ? false : true;
    }
}