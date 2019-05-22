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
    
 /*   public static boolean canInteractWithEquippedBackpack(EntityPlayer player, EntityPlayer carrier) 
    {
    	if(isWearingBackpack(carrier))
    	{
    		IInventoryTravellersBackpack backpack = getBackpackInv(carrier);
    		
    		if((backpack == null) || !player.isEntityAlive() || !carrier.isEntityAlive()) 
    		{
    			return false;
    		}
    		else
    		{
    			double distance = player.getDistance(carrier);
        		double angle = Math.toDegrees(Math.atan2(carrier.posZ - player.posZ, carrier.posX - player.posX));
        		angle = ((angle - carrier.renderYawOffset - 90) % 360 + 540) % 360 - 180;
        		return ((distance <= INTERACT_MAX_DISTANCE) && (Math.abs(angle) < INTERACT_MAX_ANGLE / 2));
    		}
    	}
		return false;
	} */
}