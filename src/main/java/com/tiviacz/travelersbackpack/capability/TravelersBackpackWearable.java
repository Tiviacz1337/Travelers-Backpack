package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.network.client.SyncBackpackCapability;
import com.tiviacz.travelersbackpack.network.client.SyncBackpackCapabilityMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TravelersBackpackWearable implements ITravelersBackpack
{
	private ItemStack wearable = ItemStack.EMPTY;
	private EntityPlayer player;
	
	public TravelersBackpackWearable(EntityPlayer player)
	{
		this.player = player;
	}
	
	public EntityPlayer getPlayer()
	{
		return this.player;
	}

	@Override
	public boolean hasWearable() 
	{
		return !this.wearable.isEmpty();
	}

	@Override
	public ItemStack getWearable() 
	{
		return this.wearable;
	}

	@Override
	public void setWearable(ItemStack stack) 
	{
		this.wearable = stack;
	}

	@Override
	public void removeWearable() 
	{
		this.wearable = ItemStack.EMPTY;
	}

	@Override
	public void synchronise()
	{
		if(player != null && !player.getEntityWorld().isRemote)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)player;

			if(CapabilityUtils.getCapability(playerMP) != null)
			{
				TravelersBackpack.NETWORK.sendTo(new SyncBackpackCapability(this.wearable.writeToNBT(new NBTTagCompound())), playerMP);
			}
		}
	}

	@Override
	public void synchroniseToOthers(EntityPlayer player)
	{
		if(player != null && !player.getEntityWorld().isRemote)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			TravelersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(this.wearable.writeToNBT(new NBTTagCompound()), playerMP.getEntityId()), playerMP);
		}
	}
}