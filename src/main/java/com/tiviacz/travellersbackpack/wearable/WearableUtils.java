package com.tiviacz.travellersbackpack.wearable;

import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;
import com.tiviacz.travellersbackpack.util.NBTUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class WearableUtils
{
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
    
    public static void setWearingBackpack(EntityPlayer player, ItemStack stack)
    {
    	Wearable wearable = new Wearable(player);
    	
    	if(!player.world.isRemote)
    	{
    		if(!wearable.hasWearable())
    		{
    			wearable.setWearable(stack);
    			wearable.saveDataToPlayer();
    			player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);
    		}
    	}
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