package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;

public class LootHandler
{
    public static void registerListeners()
    {
        if(TravelersBackpackConfig.getConfig().world.enableLoot)
        {
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, table, setter) ->
            {
                if(LootTables.ABANDONED_MINESHAFT_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.BAT_TRAVELERS_BACKPACK, 0.05F);

                    addLootPool(table, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(LootTables.VILLAGE_ARMORER_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.IRON_GOLEM_TRAVELERS_BACKPACK, 0.1F);
                }

                if(LootTables.SIMPLE_DUNGEON_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.05F);
                }

                if(LootTables.DESERT_PYRAMID_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(LootTables.SHIPWRECK_TREASURE_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if(LootTables.WOODLAND_MANSION_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if(LootTables.NETHER_BRIDGE_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if(LootTables.BASTION_TREASURE_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if(LootTables.END_CITY_TREASURE_CHEST.equals(id))
                {
                    addLootPool(table, ModItems.GOLD_TIER_UPGRADE, 0.07F);
                    addLootPool(table, ModItems.DIAMOND_TIER_UPGRADE, 0.06F);
                }
            });
        }
    }

    public static void addLootPool(LootTable.Builder builder, Item item, float chance)
    {
        builder.pool(LootPool.builder().with(ItemEntry.builder(item).build()).conditionally(RandomChanceLootCondition.builder(chance).build()));
    }
}