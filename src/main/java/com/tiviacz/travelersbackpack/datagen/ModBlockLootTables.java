package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
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
                                        .copy(ITravelersBackpackContainer.TIER, ITravelersBackpackContainer.TIER)
                                        .copy(ITravelersBackpackContainer.INVENTORY, ITravelersBackpackContainer.INVENTORY)
                                        .copy(ITravelersBackpackContainer.TOOLS_INVENTORY, ITravelersBackpackContainer.TOOLS_INVENTORY)
                                        .copy(ITravelersBackpackContainer.CRAFTING_INVENTORY, ITravelersBackpackContainer.CRAFTING_INVENTORY)
                                        .copy(ITravelersBackpackContainer.LEFT_TANK, ITravelersBackpackContainer.LEFT_TANK)
                                        .copy(ITravelersBackpackContainer.RIGHT_TANK, ITravelersBackpackContainer.RIGHT_TANK)
                                        .copy(ITravelersBackpackContainer.ABILITY, ITravelersBackpackContainer.ABILITY)
                                        .copy(ITravelersBackpackContainer.LAST_TIME, ITravelersBackpackContainer.LAST_TIME)
                                        .copy(SlotManager.UNSORTABLE_SLOTS, SlotManager.UNSORTABLE_SLOTS)
                                        .copy(SlotManager.MEMORY_SLOTS, SlotManager.MEMORY_SLOTS)
                                        .copy(SettingsManager.CRAFTING_SETTINGS, SettingsManager.CRAFTING_SETTINGS))
                                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                .copy(ITravelersBackpackContainer.COLOR, ITravelersBackpackContainer.COLOR)
                                                .when(LootItemHasColorCondition.hasColor()))
                                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                .copy(ITravelersBackpackContainer.SLEEPING_BAG_COLOR, ITravelersBackpackContainer.SLEEPING_BAG_COLOR)
                                                .when(LootItemHasSleepingBagColorCondition.hasSleepingBagColor()))
                                )));
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return Arrays.stream(ModRecipeProvider.BACKPACKS).map(Block::byItem)::iterator;
    }
}