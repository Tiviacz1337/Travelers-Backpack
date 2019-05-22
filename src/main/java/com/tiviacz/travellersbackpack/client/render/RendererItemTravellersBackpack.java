package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

public class RendererItemTravellersBackpack extends TileEntityItemStackRenderer
{ 
	private final TileEntityTravellersBackpack tileBackpack = new TileEntityTravellersBackpack();
	public FluidTank leftTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	public FluidTank rightTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	
	@Override
	public void renderByItem(ItemStack stack)
    {
		Item item = stack.getItem();
		
		if(item == ModItems.TRAVELLERS_BACKPACK)
		{
			TileEntityRendererDispatcher.instance.render(this.tileBackpack, 0.0D, 0.0D, 0.0D, 0.0F);
			
	/*		if(stack.hasTagCompound())
			{
				NBTTagCompound tag = stack.getTagCompound();
				
				if(tag.hasKey("LeftTank"))
				{
					if(tag.getCompoundTag("LeftTank") != null)
					{
						this.leftTank.readFromNBT(tag.getCompoundTag("LeftTank"));
					}
				}
				if(tag.hasKey("RightTank"))
				{
					if(tag.getCompoundTag("RightTank") != null)
					{
						this.rightTank.readFromNBT(tag.getCompoundTag("RightTank"));
					}
				}
			}  */
		}
    }
}