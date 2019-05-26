package com.tiviacz.travellersbackpack.wearable;

import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;
import com.tiviacz.travellersbackpack.util.NBTUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class WearableUtils
{
	public static double INTERACT_MAX_DISTANCE = 1.8D;
	public static double INTERACT_MAX_ANGLE = 110D;
	
    public static boolean isWearingBackpack(EntityPlayer player)
    {
    	ItemStack backpack = new ItemStack(NBTUtils.getWearingTag(player));
    	
        return NBTUtils.getWearingTag(player) != null && backpack.getItem() instanceof ItemTravellersBackpack;
    }

    public static ItemStack getWearingBackpack(EntityPlayer player)
    {
    	ItemStack backpack = new ItemStack(NBTUtils.getWearingTag(player));
    	
        return isWearingBackpack(player) ? backpack : null;
    }

    public static InventoryTravellersBackpack getBackpackInv(EntityPlayer player)
    {
        ItemStack wearable = WearableUtils.getWearingBackpack(player);
        
        if(wearable.getItem() instanceof ItemTravellersBackpack)
        {
        	return new InventoryTravellersBackpack(wearable);
        }
        return null;
    }
}