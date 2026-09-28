package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class LootHandler {
    public static void registerListeners() {
        if(TravelersBackpackConfig.COMMON.enableLoot.get()) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source, provider) ->
            {
                if(BuiltInLootTables.ABANDONED_MINESHAFT.equals(key)) {
                    addLootPool(tableBuilder, ModItems.BAT_TRAVELERS_BACKPACK, 0.05F);

                    addRandomDyedBackpackLoot(tableBuilder, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(BuiltInLootTables.PILLAGER_OUTPOST.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_GOLEM_TRAVELERS_BACKPACK, 0.06F);
                }

                if(BuiltInLootTables.SIMPLE_DUNGEON.equals(key)) {
                    addRandomDyedBackpackLoot(tableBuilder, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.DESERT_PYRAMID.equals(key)) {
                    addRandomDyedBackpackLoot(tableBuilder, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(BuiltInLootTables.SHIPWRECK_TREASURE.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.WOODLAND_MANSION.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.06F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.NETHER_BRIDGE.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if(BuiltInLootTables.BASTION_TREASURE.equals(key)) {
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.06F);
                }

                if(BuiltInLootTables.END_CITY_TREASURE.equals(key)) {
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.07F);
                    addLootPool(tableBuilder, ModItems.DIAMOND_TIER_UPGRADE, 0.06F);
                }
            });
        }
    }

    public static void addLootPool(LootTable.Builder builder, Item item, float chance) {
        builder.pool(LootPool.lootPool().add(LootItem.lootTableItem(item).build()).when(LootItemRandomChanceCondition.randomChance(chance).build()).build());
    }

    //66% chance for a dyed backpack
    public static void addRandomDyedBackpackLoot(LootTable.Builder builder, float chance) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance));

        pool.add(LootItem.lootTableItem(ModItems.STANDARD_TRAVELERS_BACKPACK)
                .setWeight(272)
                .apply(SetComponentsFunction.setComponent(
                        ModDataComponents.RENDER_INFO,
                        RenderInfo.EMPTY
                ))
        );

        for(DyeColor dyeColor : DyeColor.values()) {
            pool.add(LootItem.lootTableItem(ModItems.STANDARD_TRAVELERS_BACKPACK)
                    .setWeight(33)
                    .apply(SetComponentsFunction.setComponent(
                            DataComponents.DYED_COLOR,
                            new DyedItemColor(dyeColor.getTextureDiffuseColor())
                    ))
                    .apply(SetComponentsFunction.setComponent(
                            ModDataComponents.RENDER_INFO,
                            RenderInfo.EMPTY
                    ))
            );
        }

        builder.pool(pool.build());
    }
}