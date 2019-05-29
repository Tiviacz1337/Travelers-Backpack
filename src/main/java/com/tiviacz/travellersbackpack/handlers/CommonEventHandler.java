package com.tiviacz.travellersbackpack.handlers;

import java.util.ArrayList;
import java.util.List;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.common.LootEntryItemStack;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class CommonEventHandler 
{
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
	}
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ModItems.ITEMS)
		{
			if(item instanceof IHasModel)
			{
				((IHasModel)item).registerModels();
			}
		}
		
		for(Block block : ModBlocks.BLOCKS)
		{
			if(block instanceof IHasModel)
			{
				((IHasModel)block).registerModels();
			}
		}
	}
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
		if(event.getModID().equals(TravellersBackpack.MODID))
        {
			ConfigManager.sync(TravellersBackpack.MODID, Config.Type.INSTANCE);
        }
    }
	
	@SubscribeEvent
	public static void addBackpackToLootTable(LootTableLoadEvent event)
	{
		ResourceLocation name = event.getName();
		
		List<ResourceLocation> list = new ArrayList<ResourceLocation>();
		list.add(LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
		list.add(LootTableList.CHESTS_STRONGHOLD_CROSSING);
		list.add(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
		
		ItemStack bat = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, 2);
		ItemStack ironGolem = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, 11);
		ItemStack deluxe = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, 25);
		
		if(name.equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) 
		{
			LootEntry entry = new LootEntryItemStack(bat, 10, "loot_travellers_backpack_bat");
            event.getTable().getPool("main").addEntry(entry);
        }
		
		if(name.equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
		{
			LootEntry entry = new LootEntryItemStack(bat, 5, "loot_travellers_backpack_bat");
            event.getTable().getPool("main").addEntry(entry);
		}
		
		if(name.equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH))
		{
			LootEntry entry = new LootEntryItemStack(ironGolem, 5, "loot_travellers_backpack_irongolem");
            event.getTable().getPool("main").addEntry(entry);
		}
		
		list.forEach(loc ->
		{
			if(name.equals(loc))
			{
				LootEntry entry = new LootEntryItemStack(deluxe, 10, "loot_travellers_backpack_deluxe");
	            event.getTable().getPool("main").addEntry(entry); 
	        }
		});
	}
}