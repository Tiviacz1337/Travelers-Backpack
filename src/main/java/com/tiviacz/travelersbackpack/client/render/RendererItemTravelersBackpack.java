package com.tiviacz.travelersbackpack.client.render;

import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RendererItemTravelersBackpack extends TileEntityItemStackRenderer
{ 
	private final TileEntityTravelersBackpack tileBackpack = new TileEntityTravelersBackpack();
	private boolean flag = false;
	
	@Override
	public void renderByItem(ItemStack stack)
	{
		if(stack.getItem() == ModItems.TRAVELERS_BACKPACK)
		{
			int meta = stack.getMetadata();
			this.tileBackpack.setColorFromMeta(meta);
			
			if(ConfigHandler.client.enableBackpackItemFluidRenderer)
			{
				if(stack.getTagCompound() != null)
				{
					this.tileBackpack.loadTanks(stack.getTagCompound());
				}
				
				if(stack.getTagCompound() == null)
				{
					this.tileBackpack.loadTanks(new NBTTagCompound());
				}
				
				if(!flag)
				{
					flag = true;
				}
			}
			
			if(!ConfigHandler.client.enableBackpackItemFluidRenderer && flag)
			{
				this.tileBackpack.loadTanks(new NBTTagCompound());
				flag = false;
			}
			
			TileEntityRendererDispatcher.instance.render(this.tileBackpack, 0.0D, 0.0D, 0.0D, 0.0F);
		}
    }
}