package com.tiviacz.travellersbackpack.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BackpackWearable implements IBackpack
{
	private ItemStack wearable = ItemStack.EMPTY;
	private EntityPlayer player;
	
	public BackpackWearable()
	{

	}
	
	public BackpackWearable(EntityPlayer player)
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
}