package com.tiviacz.travellersbackpack.wearable;

import javax.annotation.Nullable;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.network.client.SyncPlayerDataPacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Wearable implements IWearable
{
	private final EntityPlayer player;
    private ItemStack wearable;

    public Wearable(EntityPlayer player)
    {
    	this.player = player;
    	
    	this.loadDataFromPlayer();
    }
    
    @Override
    public EntityPlayer getPlayer()
    {
    	return this.player;
    }
    
    @Override
    public NBTTagCompound getPlayerData()
    {
    	return this.player.getEntityData();
    }
    
    @Override
    public void saveDataToPlayer()
    {
    	NBTTagCompound playerData = this.getPlayerData();
    	
    	NBTTagCompound stackTag = new NBTTagCompound();
    	
    	if(this.wearable != null)
    	{
    		this.wearable.writeToNBT(stackTag);
    		playerData.setTag("Wearable", stackTag);
    		TravellersBackpack.NETWORK.sendTo(new SyncPlayerDataPacket(stackTag, true), (EntityPlayerMP)player);
    	}
    	else
    	{
    		playerData.removeTag("Wearable");
    		TravellersBackpack.NETWORK.sendTo(new SyncPlayerDataPacket(stackTag, false), (EntityPlayerMP)player);
    	}
    } 
    
    @Override
    public void loadDataFromPlayer()
    {
    	NBTTagCompound playerData = this.getPlayerData();
    	
    	if(!playerData.hasKey("Wearable"))
    	{
    		this.saveDataToPlayer();
    		return;
    	}
    	
    	if(playerData.getCompoundTag("Wearable") == null)
    	{
    		this.saveDataToPlayer();
    		return;
    	}
    	
    	if(playerData.getCompoundTag("Wearable") != null)
    	{
    		NBTTagCompound stackTag = playerData.getCompoundTag("Wearable");
    		
    		if(stackTag != null)
    		{
    			this.wearable = new ItemStack(stackTag);
    		}
    	}
    }
    
    @Override
    public boolean hasWearable()
    {
        return wearable != null;
    }
    
    @Override
    public ItemStack getWearable()
    {
        return wearable;
    }
    
    @Override
    public void setWearable(@Nullable ItemStack stack)
    {
        this.wearable = stack;
    }
}