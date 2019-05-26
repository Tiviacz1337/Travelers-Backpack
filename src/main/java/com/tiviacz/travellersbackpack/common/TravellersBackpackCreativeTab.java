package com.tiviacz.travellersbackpack.common;

import com.tiviacz.travellersbackpack.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TravellersBackpackCreativeTab extends CreativeTabs
{
	public TravellersBackpackCreativeTab(String label) 
	{
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(ModItems.TRAVELLERS_BACKPACK);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> list)
    {
		list.add(new ItemStack(ModItems.SLEEPING_BAG));
		list.add(new ItemStack(ModItems.BACKPACK_TANK));
		list.add(new ItemStack(ModItems.HOSE_NOZZLE));
		list.add(new ItemStack(ModItems.HOSE));
		
		//Alphabetical order
		addBackpack(list, 0);
		addBackpack(list, 2);
		addBackpack(list, 3);
		addBackpack(list, 4);
		addBackpack(list, 5);
		addBackpack(list, 6);
		addBackpack(list, 1);
		addBackpack(list, 7);
		addBackpack(list, 8);
		addBackpack(list, 9);
		addBackpack(list, 10);
    }
	
	public void addBackpack(NonNullList<ItemStack> list, int meta)
	{
		list.add(new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, meta));
	}
}
