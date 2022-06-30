package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;

public class LootHandler
{
    public static void registerListeners()
    {
        if(TravelersBackpackConfig.enableLoot)
        {
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, table, setter) ->
            {
                if(LootTables.ABANDONED_MINESHAFT_CHEST.equals(id))
                {
                    LootPool.Builder poolBuilder = LootPool.builder()
                            .with(ItemEntry.builder(ModItems.BAT_TRAVELERS_BACKPACK).build()).conditionally(RandomChanceLootCondition.builder(0.2F).build());
                    table.pool(poolBuilder);
                    //.rolls(ConstantLootNumberProvider.create(1))
                }

                if(LootTables.VILLAGE_ARMORER_CHEST.equals(id))
                {
                    LootPool.Builder poolBuilder = LootPool.builder()
                            .with(ItemEntry.builder(ModItems.IRON_GOLEM_TRAVELERS_BACKPACK).build()).conditionally(RandomChanceLootCondition.builder(0.2F).build());
                    table.pool(poolBuilder);
                }
            });
        }
    }
}
