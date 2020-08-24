package com.tiviacz.travelersbackpack.gui.container.slots;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.util.EnumSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;

public class SlotTool extends Slot
{
	private EntityPlayer player;
	private EnumSource source;
	private static String[] validToolNames = {
			"wrench", "hammer", "axe", "shovel", "grafter", "scoop", "crowbar", "mattock", "drill", "chisel", "cutter", "dirt", "disassembler", "tool"
	};

	public SlotTool(EntityPlayer player, EnumSource source, IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);

		this.player = player;
		this.source = source;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
    	System.out.println(isValid(stack));
		return isValid(stack);
    }
	
	public static boolean isValid(ItemStack stack)
    {
		if(stack.getMaxStackSize() == 1)
		{
			//Vanilla tools
			if(stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemFishingRod || stack.getItem() instanceof ItemShears || stack.getItem() instanceof ItemFlintAndSteel)
			{
				return true;
			}

			for(String name : validToolNames)
			{
				if(stack.getUnlocalizedName().toLowerCase().contains(name))
				{
					return true;
				}
			}

			if(stack.getItem().getClass().getName().contains("tconstruct.tools.tools"))
			{
				return true;
			}

			if(ConfigHandler.server.toolSlotsAcceptSwords)
			{
				if(stack.getItem() instanceof ItemSword)
				{
					return true;
				}

				if(stack.getItem().getClass().getName().contains("tconstruct.tools.melee"))
				{
					return true;
				}
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