package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public class LootHandler {
    public static void registerListeners() {
        if(TravelersBackpackConfig.getConfig().world.enableLoot) {
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
            {
                if(BuiltInLootTables.ABANDONED_MINESHAFT.equals(id)) {
                    addLootPool(tableBuilder, ModItems.BAT_TRAVELERS_BACKPACK, 0.05F);

                    addLootPool(tableBuilder, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(BuiltInLootTables.VILLAGE_ARMORER.equals(id)) {
                    addLootPool(tableBuilder, ModItems.IRON_GOLEM_TRAVELERS_BACKPACK, 0.1F);
                }

                if(BuiltInLootTables.SIMPLE_DUNGEON.equals(id)) {
                    addLootPool(tableBuilder, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.DESERT_PYRAMID.equals(id)) {
                    addLootPool(tableBuilder, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(BuiltInLootTables.SHIPWRECK_TREASURE.equals(id)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.WOODLAND_MANSION.equals(id)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.NETHER_BRIDGE.equals(id)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if(BuiltInLootTables.BASTION_TREASURE.equals(id)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if(BuiltInLootTables.END_CITY_TREASURE.equals(id)) {
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.DIAMOND_TIER_UPGRADE, 0.06F);
                }
            });
        }
    }

    public static void addLootPool(LootTable.Builder builder, Item item, float chance) {
        builder.pool(LootPool.lootPool().with(LootItem.lootTableItem(item).build()).conditionally(LootItemRandomChanceCondition.randomChance(chance).build()).build());
    }
}