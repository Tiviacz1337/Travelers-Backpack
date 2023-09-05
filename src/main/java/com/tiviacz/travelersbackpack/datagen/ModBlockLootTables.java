package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import me.shedaniel.cloth.api.datagen.v1.LootTableData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;

public class ModBlockLootTables
{
    public static void addLootTables(LootTableData data)
    {
        for(Item item : ModRecipesProvider.BACKPACKS)
        {
            data.register(Block.getBlockFromItem(item), createBackpackDrop(Block.getBlockFromItem(item)));
        }
    }

    protected static LootTable.Builder createBackpackDrop(Block block)
    {
        return LootTable.builder()
                .pool(LootTableData.addSurvivesExplosionLootCondition(block, LootPool.builder().rolls(ConstantLootTableRange.create(1))
                        .with(ItemEntry.builder(block)
                                .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                                .apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY)
                                        .withOperation("Tier", "Tier")
                                        .withOperation("Inventory", "Inventory")
                                        .withOperation("CraftingInventory", "CraftingInventory")
                                        .withOperation("LeftTank", "LeftTank")
                                        .withOperation("LeftTankAmount", "LeftTankAmount")
                                        .withOperation("RightTank", "RightTank")
                                        .withOperation("RightTankAmount", "RightTankAmount")
                                        .withOperation("Ability", "Ability")
                                        .withOperation("LastTime", "LastTime")
                                        .withOperation("UnsortableSlots", "UnsortableSlots")
                                        .withOperation("MemorySlots", "MemorySlots")
                                        .withOperation("CraftingSettings", "CraftingSettings"))
                                .apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY)
                                        .withOperation("Color", "Color")
                                        .conditionally(LootItemHasColorCondition.hasColor()))
                                .apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY)
                                        .withOperation("SleepingBagColor", "SleepingBagColor")
                                        .conditionally(LootItemHasSleepingBagColorCondition.hasSleepingBagColor()))
                        )));
    }
}