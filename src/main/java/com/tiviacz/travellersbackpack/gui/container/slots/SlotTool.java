package com.tiviacz.travellersbackpack.gui.container.slots;

import com.tiviacz.travellersbackpack.handlers.ConfigHandler;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class SlotTool extends Slot
{
	public SlotTool(IInventory inventoryIn, int index, int xPosition, int yPosition) 
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
		return isValid(stack);
    }
	
	public static boolean isValid(ItemStack stack)
    {
		if(stack.getMaxStackSize() == 1)
		{
			if(ConfigHandler.server.toolSlotsAcceptSwords)
			{
				if(stack.getItem() instanceof ItemSword)
				{
					return true;
				}
				
				try
				{
					//Tinker's Construct
					if(stack.getItem().getClass().getName().contains("tconstruct.tools.melee"))
					{
						return true;
					}
				} catch(Exception oops)
				{
					
				}
			}
			
			//Vanilla tools
			if(stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemFishingRod || stack.getItem() instanceof ItemShears || stack.getItem() instanceof ItemFlintAndSteel)
			{
				return true;
			}
			
			try
            {
                //Tinker's Construct
                if(stack.getItem().getClass().getName().contains("tconstruct.tools.tools"))
                {
                	return true;
                }
            } catch(Exception oops)
            {
                //  oops.printStackTrace();
            }
		}
		return false;
    }
}