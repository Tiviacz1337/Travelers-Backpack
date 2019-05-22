package com.tiviacz.travellersbackpack.init;

import java.util.ArrayList;
import java.util.List;

import com.tiviacz.travellersbackpack.items.ItemBase;
import com.tiviacz.travellersbackpack.items.ItemHose;
import com.tiviacz.travellersbackpack.items.ItemSleepingBag;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;

import net.minecraft.item.Item;

public class ModItems 
{
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final Item TRAVELLERS_BACKPACK = new ItemTravellersBackpack("travellers_backpack");
	public static final Item BACKPACK_TANK = new ItemBase("backpack_tank").setMaxStackSize(16);
	public static final Item SLEEPING_BAG = new ItemSleepingBag(ModBlocks.SLEEPING_BAG_BOTTOM);
	
	public static final Item HOSE_NOZZLE = new ItemBase("hose_nozzle");
	public static final Item HOSE = new ItemHose("hose");	
}