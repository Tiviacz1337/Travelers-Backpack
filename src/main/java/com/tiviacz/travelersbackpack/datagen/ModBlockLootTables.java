package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Arrays;

public class ModBlockLootTables extends BlockLoot
{
    @Override
    protected void addTables()
    {
        for(Item item : ModRecipeProvider.BACKPACKS)
        {
            this.add(Block.byItem(item), ModBlockLootTables::createBackpackDrop);
        }
    }

    protected static LootTable.Builder createBackpackDrop(Block block)
    {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy("Tier", "Tier")
                                        .copy("Inventory", "Inventory")
                                        .copy("CraftingInventory", "CraftingInventory")
                                        .copy("LeftTank", "LeftTank")
                                        .copy("RightTank", "RightTank")
                                        .copy("Ability", "Ability")
                                        .copy("LastTime", "LastTime")
                                        .copy("UnsortableSlots", "UnsortableSlots")
                                        .copy("MemorySlots", "MemorySlots")
                                        .copy("CraftingSettings", "CraftingSettings"))
                                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                .copy("Color", "Color")
                                        .when(LootItemHasColorCondition.hasColor()))
                                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                .copy("SleepingBagColor", "SleepingBagColor")
                                                .when(LootItemHasSleepingBagColorCondition.hasSleepingBagColor()))
                                )));
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return Arrays.stream(ModRecipeProvider.BACKPACKS).map(Block::byItem)::iterator;
    }
}