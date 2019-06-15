package com.tiviacz.travellersbackpack.capability;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapability;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapabilityMP;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;

public class CapabilityUtils 
{
	public static IBackpack getCapability(EntityPlayer player)
	{
		return player.getCapability(BackpackProvider.BACKPACK_CAP, null);
	}
	
    public static boolean isWearingBackpack(EntityPlayer player)
    {
    	IBackpack cap = player.getCapability(BackpackProvider.BACKPACK_CAP, null);
    	ItemStack backpack = cap.getWearable();
    	
        return cap.hasWearable() && backpack.getItem() instanceof ItemTravellersBackpack;
    }

    public static ItemStack getWearingBackpack(EntityPlayer player)
    {
    	IBackpack cap = player.getCapability(BackpackProvider.BACKPACK_CAP, null);
    	ItemStack backpack = cap.getWearable();
    	
        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY;
    }
    
    public static void equipBackpack(EntityPlayer player, ItemStack stack)
    {
    	IBackpack cap = player.getCapability(BackpackProvider.BACKPACK_CAP, null);
    	
    	if(!player.world.isRemote)
    	{
    		if(!cap.hasWearable())
    		{
    			cap.setWearable(stack);
    			player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);
    		
    			//Sync
    			TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(stack.writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
				TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(stack.writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
    		}
    	}
    }
    
    public static void unequipBackpack(EntityPlayer player)
    {
    	IBackpack cap = player.getCapability(BackpackProvider.BACKPACK_CAP, null);
    	
    	if(!player.world.isRemote)
    	{
    		if(cap.hasWearable())
    		{
    			cap.setWearable(ItemStack.EMPTY);
    			player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);
    		}
    	}
    }

    public static InventoryTravellersBackpack getBackpackInv(EntityPlayer player)
    {
        ItemStack wearable = CapabilityUtils.getWearingBackpack(player);
        
        if(wearable.getItem() instanceof ItemTravellersBackpack)
        {
        	return new InventoryTravellersBackpack(wearable, player);
        }
        return null;
    }
}