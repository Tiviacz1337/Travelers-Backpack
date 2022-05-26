package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class LootHandler
{
    public static void registerListeners()
    {
        if(TravelersBackpackConfig.enableLoot)
        {
            LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) ->
            {
                if(LootTables.ABANDONED_MINESHAFT_CHEST.equals(id))
                {
                    FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1))
                            .withEntry(ItemEntry.builder(ModItems.BAT_TRAVELERS_BACKPACK).build()).withCondition(RandomChanceLootCondition.builder(0.2F).build());
                    table.pool(poolBuilder);
                }
            });
        }
    }
}
