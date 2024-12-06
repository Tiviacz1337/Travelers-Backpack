package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTables extends FabricBlockLootTableProvider {
    protected ModBlockLootTables(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> holderLookupProvidr) {
        super(output, holderLookupProvidr);
    }

    @Override
    public void generate() {
        for(Item item : ModRecipeProvider.BACKPACKS) {
            this.add(Block.byItem(item), this::createBackpackDrop);
        }

        this.add(ModBlocks.BLACK_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.BLUE_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.BROWN_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.CYAN_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.GRAY_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.GREEN_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.LIGHT_BLUE_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.LIGHT_GRAY_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.LIME_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.MAGENTA_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.PURPLE_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.ORANGE_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.PINK_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.RED_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.WHITE_SLEEPING_BAG, this::createSleepingBagDrop);
        this.add(ModBlocks.YELLOW_SLEEPING_BAG, this::createSleepingBagDrop);
    }

    protected LootTable.Builder createBackpackDrop(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(ModDataComponents.TIER)
                                        .include(ModDataComponents.STORAGE_SLOTS)
                                        .include(ModDataComponents.UPGRADE_SLOTS)
                                        .include(ModDataComponents.TOOL_SLOTS)
                                        .include(ModDataComponents.BACKPACK_CONTAINER)
                                        .include(ModDataComponents.TOOLS_CONTAINER)
                                        .include(ModDataComponents.UPGRADES)
                                        .include(ModDataComponents.SHOW_TOOL_SLOTS)
                                        .include(ModDataComponents.SLEEPING_BAG_COLOR)
                                        .include(ModDataComponents.ABILITY_ENABLED)
                                        .include(ModDataComponents.COOLDOWN)
                                        .include(ModDataComponents.RENDER_INFO)
                                        .include(ModDataComponents.STARTER_UPGRADES)
                                        .include(ModDataComponents.SLOTS)
                                        .include(ModDataComponents.IS_VISIBLE)
                                        .include(ModDataComponents.UPGRADE_TICK_INTERVAL)
                                        .include(DataComponents.DYED_COLOR)))));
    }

    protected LootTable.Builder createSleepingBagDrop(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SleepingBagBlock.PART, BedPart.HEAD))))));
    }
}