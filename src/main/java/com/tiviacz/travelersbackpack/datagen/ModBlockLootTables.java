package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
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
                                        .withOperation(ITravelersBackpackInventory.TIER, ITravelersBackpackInventory.TIER)
                                        .withOperation(ITravelersBackpackInventory.INVENTORY, ITravelersBackpackInventory.INVENTORY)
                                        .withOperation(ITravelersBackpackInventory.TOOLS_INVENTORY, ITravelersBackpackInventory.TOOLS_INVENTORY)
                                        .withOperation(ITravelersBackpackInventory.CRAFTING_INVENTORY, ITravelersBackpackInventory.CRAFTING_INVENTORY)
                                        .withOperation(ITravelersBackpackInventory.LEFT_TANK, ITravelersBackpackInventory.LEFT_TANK)
                                        .withOperation(ITravelersBackpackInventory.RIGHT_TANK, ITravelersBackpackInventory.RIGHT_TANK)
                                        .withOperation(ITravelersBackpackInventory.ABILITY, ITravelersBackpackInventory.ABILITY)
                                        .withOperation(ITravelersBackpackInventory.LAST_TIME, ITravelersBackpackInventory.LAST_TIME)
                                        .withOperation(SlotManager.UNSORTABLE_SLOTS, SlotManager.UNSORTABLE_SLOTS)
                                        .withOperation(SlotManager.MEMORY_SLOTS, SlotManager.MEMORY_SLOTS)
                                        .withOperation(SettingsManager.CRAFTING_SETTINGS, SettingsManager.CRAFTING_SETTINGS))
                                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                                .withOperation(ITravelersBackpackInventory.COLOR, ITravelersBackpackInventory.COLOR)
                                                .conditionally(LootItemHasColorCondition.hasColor()))
                                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                                .withOperation(ITravelersBackpackInventory.SLEEPING_BAG_COLOR, ITravelersBackpackInventory.SLEEPING_BAG_COLOR)
                                                .conditionally(LootItemHasSleepingBagColorCondition.hasSleepingBagColor()))
                                )));
    }
}