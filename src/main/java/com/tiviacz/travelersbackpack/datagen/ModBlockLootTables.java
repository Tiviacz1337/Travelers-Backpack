package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class ModBlockLootTables extends FabricBlockLootTableProvider
{
    protected ModBlockLootTables(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void generateBlockLootTables()
    {
        for(Item item : ModRecipesProvider.BACKPACKS)
        {
            this.addDrop(Block.getBlockFromItem(item), ModBlockLootTables::createBackpackDrop);
        }
    }

    protected static LootTable.Builder createBackpackDrop(Block block)
    {
        return LootTable.builder()
                .pool(addSurvivesExplosionCondition(block, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(ItemEntry.builder(block)
                                .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                                .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                        .withOperation("Tier", "Tier")
                                        .withOperation("Inventory", "Inventory")
                                        .withOperation("CraftingInventory", "CraftingInventory")
                                        .withOperation("LeftTank", "LeftTank")
                                        .withOperation("RightTank", "RightTank")
                                        .withOperation("Ability", "Ability")
                                        .withOperation("LastTime", "LastTime")
                                        .withOperation("UnsortableSlots", "UnsortableSlots")
                                        .withOperation("MemorySlots", "MemorySlots")
                                        .withOperation("CraftingSettings", "CraftingSettings"))
                                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                                .withOperation("Color", "Color")
                                        .conditionally(LootItemHasColorCondition.hasColor()))
                                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                                .withOperation("SleepingBagColor", "SleepingBagColor")
                                                .conditionally(LootItemHasSleepingBagColorCondition.hasSleepingBagColor()))
                                )));
    }
}