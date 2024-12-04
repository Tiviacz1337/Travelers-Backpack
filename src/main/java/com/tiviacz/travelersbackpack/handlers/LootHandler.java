package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpackneo.init.ModItems;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootHandler {
    public static void registerListeners() {
        if (TravelersBackpackConfig.getConfig().world.enableLoot) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source) ->
            {
                if (LootTables.ABANDONED_MINESHAFT_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.BAT_TRAVELERS_BACKPACK, 0.05F);

                    addLootPool(tableBuilder, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if (LootTables.VILLAGE_ARMORER_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_GOLEM_TRAVELERS_BACKPACK, 0.1F);
                }

                if (LootTables.SIMPLE_DUNGEON_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                }

                if (LootTables.DESERT_PYRAMID_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.STANDARD_TRAVELERS_BACKPACK, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if (LootTables.SHIPWRECK_TREASURE_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if (LootTables.WOODLAND_MANSION_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if (LootTables.NETHER_BRIDGE_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if (LootTables.BASTION_TREASURE_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if (LootTables.END_CITY_TREASURE_CHEST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.DIAMOND_TIER_UPGRADE, 0.06F);
                }
            });
        }
    }

    public static void addLootPool(LootTable.Builder builder, Item item, float chance) {
        builder.pool(LootPool.builder().with(ItemEntry.builder(item).build()).conditionally(RandomChanceLootCondition.builder(chance).build()));
    }
}