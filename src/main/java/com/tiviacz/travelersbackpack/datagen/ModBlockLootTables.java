package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;

import java.util.Arrays;

public class ModBlockLootTables extends BlockLootTables
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
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(block)
                                .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
                                .apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
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
                                        .apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
                                                .copy("Color", "Color")
                                        .when(LootItemHasColorCondition.hasColor()))
                                        .apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
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