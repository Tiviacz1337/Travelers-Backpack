package com.tiviacz.travellersbackpack.items;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.util.IHasModel;

import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel
{
	public ItemBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(TravellersBackpack.TRAVELLERSBACKPACKTAB);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels() 
	{
		TravellersBackpack.proxy.registerItemRenderer(this, 0, "inventory");
	}
}