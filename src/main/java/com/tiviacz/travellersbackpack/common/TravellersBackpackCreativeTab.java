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
		addBackpack(list, 0);	//Standard
		addBackpack(list, 2);	//Bat
		addBackpack(list, 3);	//Black
		addBackpack(list, 4);	//Blaze
		addBackpack(list, 14);	//Blue
		addBackpack(list, 15);	//Bookshelf
		addBackpack(list, 16); 	//Brown
		addBackpack(list, 17); 	//Cactus
		addBackpack(list, 18); 	//Cake
		addBackpack(list, 5);	//Carrot
		addBackpack(list, 19);  //Chest
		addBackpack(list, 6);	//Coal
		addBackpack(list, 20); 	//Cookie
		addBackpack(list, 1);	//Cow
		addBackpack(list, 21);  //Cyan
		addBackpack(list, 25);  //Deluxe
		addBackpack(list, 7);	//Diamond
		addBackpack(list, 22);  //Dragon
		addBackpack(list, 23);  //Egg
		addBackpack(list, 24);  //Electric
		addBackpack(list, 8);	//Emerald
		addBackpack(list, 9);	//Gold
		addBackpack(list, 10);	//Iron
		addBackpack(list, 11);	//IronGolem
		addBackpack(list, 12); 	//Lapis
		addBackpack(list, 13);	//Redstone
    }
	
	public void addBackpack(NonNullList<ItemStack> list, int meta)
	{
		list.add(new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, meta));
	}
}
