package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.ItemTravelersBackpack;
import com.tiviacz.travelersbackpack.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = TravelersBackpack.MODID)
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
		if(event.getModID().equals(TravelersBackpack.MODID))
        {
			ConfigManager.sync(TravelersBackpack.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event)
	{
		for(int i = 0; i < event.getAffectedEntities().size(); i++)
		{
			Entity entity = event.getAffectedEntities().get(i);

			if(entity instanceof EntityItem && ((EntityItem)entity).getItem().getItem() instanceof ItemTravelersBackpack)
			{
				event.getAffectedEntities().remove(i);
			}
		}
	}

	@SubscribeEvent
	public static void addBackpackToLootTable(LootTableLoadEvent event)
	{
		if(ConfigHandler.server.enableLoot)
		{
			List<ResourceLocation> list = new ArrayList<>();
			list.add(LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
			list.add(LootTableList.CHESTS_STRONGHOLD_CROSSING);
			list.add(LootTableList.CHESTS_STRONGHOLD_LIBRARY);

			addLoot(event.getName(), LootTableList.CHESTS_ABANDONED_MINESHAFT, 2, "bat", event.getTable());
			addLoot(event.getName(), LootTableList.CHESTS_VILLAGE_BLACKSMITH, 11, "irongolem", event.getTable());

			list.forEach(loc -> addLoot(event.getName(), loc, 25, "deluxe", event.getTable()));
		}
	}

	public static void addLoot(ResourceLocation name, ResourceLocation targetName, int meta, String backpackName, LootTable table)
	{
		if(name.equals(targetName))
		{
			ItemStack stack = new ItemStack(ModItems.TRAVELERS_BACKPACK, 1, meta);
			LootEntry entry = LootHandler.createItemEntry(stack, 3, 0, "loot_travelers_backpack_" + backpackName);
			table.getPool("main").addEntry(entry);
		}
	}
}