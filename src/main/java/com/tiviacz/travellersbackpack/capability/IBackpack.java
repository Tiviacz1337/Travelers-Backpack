package com.tiviacz.travellersbackpack.capability;

import net.minecraft.item.ItemStack;

public interface IBackpack
{
	public boolean hasWearable();
	
	public ItemStack getWearable();
	
	public void setWearable(ItemStack stack);
	
	public void removeWearable();
	
	public static class Impl implements IBackpack 
	{
		ItemStack stack = ItemStack.EMPTY;
		
		@Override
		public boolean hasWearable() 
		{
			return !stack.isEmpty();
		}

		@Override
		public ItemStack getWearable() 
		{
			return stack;
		}

		@Override
		public void setWearable(ItemStack stack)
		{
			this.stack = stack;
		}

		@Override
		public void removeWearable() 
		{
			this.stack = ItemStack.EMPTY;
		}
	}
}