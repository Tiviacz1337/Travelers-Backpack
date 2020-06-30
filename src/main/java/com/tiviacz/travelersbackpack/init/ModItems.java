package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.items.ItemBase;
import com.tiviacz.travelersbackpack.items.ItemHose;
import com.tiviacz.travelersbackpack.items.ItemSleepingBag;
import com.tiviacz.travelersbackpack.items.ItemTravelersBackpack;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModItems 
{
	public static final List<Item> ITEMS = new ArrayList<>();
	
	public static final Item TRAVELERS_BACKPACK = new ItemTravelersBackpack("travelers_backpack");
	public static final Item BACKPACK_TANK = new ItemBase("backpack_tank").setMaxStackSize(16);
	public static final Item SLEEPING_BAG = new ItemSleepingBag(ModBlocks.SLEEPING_BAG_BOTTOM);
	
	public static final Item HOSE_NOZZLE = new ItemBase("hose_nozzle");
	public static final Item HOSE = new ItemHose("hose");	
}