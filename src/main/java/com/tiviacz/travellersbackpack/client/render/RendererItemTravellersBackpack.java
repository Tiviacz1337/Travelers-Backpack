package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.handlers.ConfigHandler;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RendererItemTravellersBackpack extends TileEntityItemStackRenderer
{ 
	private final TileEntityTravellersBackpack tileBackpack = new TileEntityTravellersBackpack();
	private boolean flag = false;
	
	@Override
	public void renderByItem(ItemStack stack)
	{
		if(stack.getItem() == ModItems.TRAVELLERS_BACKPACK)
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