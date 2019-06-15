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
		addBackpack(list, 43);  //Brown Mushroom
		addBackpack(list, 17); 	//Cactus
		addBackpack(list, 18); 	//Cake
		addBackpack(list, 5);	//Carrot
		addBackpack(list, 19);  //Chest
		addBackpack(list, 28);  //Chicken
		addBackpack(list, 6);	//Coal
		addBackpack(list, 20); 	//Cookie
		addBackpack(list, 1);	//Cow
	//	addBackpack(list, xx);  //Creeper
		addBackpack(list, 21);  //Cyan
		addBackpack(list, 25);  //Deluxe
		addBackpack(list, 7);	//Diamond
		addBackpack(list, 22);  //Dragon
		addBackpack(list, 23);  //Egg
		addBackpack(list, 24);  //Electric
		addBackpack(list, 8);	//Emerald
		addBackpack(list, 27);  //End
		addBackpack(list, 26);  //Enderman
		addBackpack(list, 30);  //Ghast
		addBackpack(list, 37);  //Glowstone
		addBackpack(list, 9);	//Gold
		addBackpack(list, 31);  //Gray
		addBackpack(list, 32);  //Green
		addBackpack(list, 33);  //Haybale
		addBackpack(list, 34); 	//Horse
		addBackpack(list, 10);	//Iron
		addBackpack(list, 11);	//IronGolem
		addBackpack(list, 12); 	//Lapis
		addBackpack(list, 35);  //Leather
		addBackpack(list, 36);  //Light Blue
		addBackpack(list, 38);  //Light Gray
		addBackpack(list, 39);  //Lime
	 	addBackpack(list, 40);  //Magenta
		addBackpack(list, 41);  //Magma Cube
		addBackpack(list, 42);  //Melon
	//	addBackpack(list, xx);  //Modded Network
	 	addBackpack(list, 45);  //Mooshroom
	    addBackpack(list, 46);  //Nether
	    addBackpack(list, 48);  //Obsidian
		addBackpack(list, 29);  //Ocelot
	    addBackpack(list, 49);  //Orange
	    addBackpack(list, 50);  //Overworld
	//  addBackpack(list, xx);  //Pig
	//  addBackpack(list, xx);  //Pigman
	//  addBackpack(list, xx);  //Pink
	//  addBackpack(list, xx);  //Pumpkin
	//  addBackpack(list, xx);  //Purple
	//  addBackpack(list, xx);  //Quartz
	//  addBackpack(list, xx);  //Rainbow
	//  addBackpack(list, xx);  //Red
		addBackpack(list, 44);  //Red Mushroom
		addBackpack(list, 13);	//Redstone
	//  addBackpack(list, xx);  //Sandstone
	//  addBackpack(list, xx);  //Sheep
	//  addBackpack(list, xx);  //Silverfish
	//  addBackpack(list, xx);  //Skeleton
	//  addBackpack(list, xx);  //Slime
	//  addBackpack(list, xx);  //Snow
	//  addBackpack(list, xx);  //Spider
	//  addBackpack(list, xx);  //Sponge
	//  addBackpack(list, xx);  //Squid
	//  addBackpack(list, xx);  //Sunflower
	//  addBackpack(list, xx);  //Villager
	//  addBackpack(list, xx);  //White
	    addBackpack(list, 47);  //Wither
	//  addBackpack(list, xx);  //Wither Skeleton
	//  addBackpack(list, xx);  //Wolf
	//  addBackpack(list, xx);  //Yellow
	//  addBackpack(list, xx);  //Zombie
    }
	
	public void addBackpack(NonNullList<ItemStack> list, int meta)
	{
		list.add(new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, meta));
	}
}