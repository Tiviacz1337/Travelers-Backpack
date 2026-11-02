package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class LootHandler {
    public static void registerListeners() {
        if(TravelersBackpackConfig.getConfig().world.enableLoot) {
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
            {
                if(BuiltInLootTables.ABANDONED_MINESHAFT.equals(id)) {
                    addLootPool(tableBuilder, ModItems.BAT_TRAVELERS_BACKPACK, 0.05F);

                    addRandomDyedBackpackLoot(tableBuilder, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                    addLootPool(tableBuilder, ModItems.GOLD_TIER_UPGRADE, 0.04F);
                }

                if(BuiltInLootTables.PILLAGER_OUTPOST.equals(id)) {
                    addLootPool(tableBuilder, ModItems.IRON_GOLEM_TRAVELERS_BACKPACK, 0.06F);
                }

                if(BuiltInLootTables.SIMPLE_DUNGEON.equals(id)) {
                    addRandomDyedBackpackLoot(tableBuilder, 0.06F);
                    addLootPool(tableBuilder, ModItems.IRON_TIER_UPGRADE, 0.05F);
                }

                if(BuiltInLootTables.DESERT_PYRAMID.equals(id)) {
                    addRandomDyedBackpackLoot(tableBuilder, 0.06F);
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

    //66% chance for a dyed backpack
    public static void addRandomDyedBackpackLoot(LootTable.Builder builder, float chance) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance));

        pool.add(LootItem.lootTableItem(ModItems.STANDARD_TRAVELERS_BACKPACK)
                .setWeight(272)
                .apply(SetNbtFunction.setTag(renderInfoTag()))
        );

        for(DyeColor dyeColor : DyeColor.values()) {
            pool.add(LootItem.lootTableItem(ModItems.STANDARD_TRAVELERS_BACKPACK)
                    .setWeight(33)
                    .apply(SetNbtFunction.setTag(dyedBackpackTag(dyeColor)))
            );
        }

        builder.pool(pool.build());
    }

    private static CompoundTag renderInfoTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(ModDataHelper.RENDER_INFO, RenderInfo.EMPTY.compoundTag().copy());
        return tag;
    }

    private static CompoundTag dyedBackpackTag(DyeColor dyeColor) {
        CompoundTag tag = renderInfoTag();
        float[] colors = dyeColor.getTextureDiffuseColors();
        int red = (int)(colors[0] * 255.0F);
        int green = (int)(colors[1] * 255.0F);
        int blue = (int)(colors[2] * 255.0F);
        tag.putInt(ModDataHelper.COLOR, (red << 16) | (green << 8) | blue);
        return tag;
    }
}