package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.common.LootEntryItemStack;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
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
		final ItemStack stack = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, 2);
		
		if(event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) 
		{
			LootEntry entry = new LootEntryItemStack(stack, 10, 50, new LootFunction[0], new LootCondition[0], TravellersBackpack.MODID + ":loot_travellers_backpack_bat");
            event.getTable().getPool("main").addEntry(entry);
        }
		
		if(event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
		{
			LootEntry entry = new LootEntryItemStack(stack, 5, 50, new LootFunction[0], new LootCondition[0], TravellersBackpack.MODID + ":loot_travellers_backpack_bat");
            event.getTable().getPool("main").addEntry(entry);
		}
	}
}