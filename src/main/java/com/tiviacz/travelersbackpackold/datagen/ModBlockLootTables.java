package com.tiviacz.travelersbackpackold.datagen;

import com.tiviacz.travelersbackpackold.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BedPart;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.StringIdentifiable;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTables extends FabricBlockLootTableProvider
{
    protected ModBlockLootTables(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup)
    {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate()
    {
        for(Item item : ModRecipesProvider.BACKPACKS)
        {
            this.addDrop(Block.getBlockFromItem(item), this::createBackpackDrop);
        }

        this.addDrop(ModBlocks.BLACK_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.BLUE_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.BROWN_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.CYAN_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.GRAY_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.GREEN_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.LIGHT_BLUE_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.LIGHT_GRAY_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.LIME_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.MAGENTA_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.PURPLE_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.ORANGE_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.PINK_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.RED_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.WHITE_SLEEPING_BAG, this::createSleepingBagDrops);
        this.addDrop(ModBlocks.YELLOW_SLEEPING_BAG, this::createSleepingBagDrops);
    }

    protected LootTable.Builder createBackpackDrop(Block block)
    {
        return LootTable.builder()
                .pool(addSurvivesExplosionCondition(block, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(ItemEntry.builder(block)
                                .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                                .apply(CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
                                        .include(ModDataComponents.TIER)
                                        .include(ModDataComponents.BACKPACK_CONTAINER)
                                        .include(ModDataComponents.TOOLS_CONTAINER)
                                        .include(ModDataComponents.CRAFTING_CONTAINER)
                                        .include(ModDataComponents.FLUID_TANKS)
                                        .include(ModDataComponents.ABILITY_SWITCH)
                                        .include(ModDataComponents.SETTINGS)
                                        .include(ModDataComponents.SLOTS)
                                        .include(ModDataComponents.SLEEPING_BAG_COLOR)
                                        .include(DataComponentTypes.DYED_COLOR)))));
    }

    /*protected LootTable.Builder createBackpackDrop(Block block)
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
    } */

    public <T extends Comparable<T> & StringIdentifiable> LootTable.Builder createSleepingBagDrops(Block drop)
    {
        return LootTable.builder()
                .pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(drop).conditionally(BlockStatePropertyLootCondition.builder(drop)
                                .properties(StatePredicate.Builder.create().exactMatch(SleepingBagBlock.PART, BedPart.HEAD))))));
    }
}