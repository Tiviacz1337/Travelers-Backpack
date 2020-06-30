package com.tiviacz.travelersbackpack.gui.container.slots;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;

import com.tiviacz.travelersbackpack.util.EnumSource;
import net.minecraft.entity.player.EntityPlayer;
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
	private EntityPlayer player;
	private EnumSource source;

	public SlotTool(EntityPlayer player, EnumSource source, IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);

		this.player = player;
		this.source = source;
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
				} catch(Exception ignored)
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

	@Override
	public void onSlotChanged()
	{
		if(this.source == EnumSource.WEARABLE)
		{
			CapabilityUtils.synchronise(this.player);
			CapabilityUtils.synchroniseToOthers(this.player);
		}
	}
}