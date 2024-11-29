package com.tiviacz.travelersbackpackneo.datagen;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpackneo.initold.ModBlocks;
import com.tiviacz.travelersbackpackneo.initold.ModDataComponents;
import com.tiviacz.travelersbackpackneo.initold.ModItemsNeo;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ModBlockLootTables extends BlockLootSubProvider {
    protected ModBlockLootTables(HolderLookup.Provider holderLookupProvidr) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), holderLookupProvidr);
    }

    @Override
    protected void generate() {
        for(Item item : ModRecipeProvider.BACKPACKS) {
            this.add(Block.byItem(item), this::createBackpackDrop);
        }

        this.add(ModBlocks.BLACK_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.BLUE_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.BROWN_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.CYAN_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.GRAY_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.GREEN_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.LIME_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.MAGENTA_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.PURPLE_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.ORANGE_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.PINK_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.RED_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.WHITE_SLEEPING_BAG.get(), this::createSleepingBagDrop);
        this.add(ModBlocks.YELLOW_SLEEPING_BAG.get(), this::createSleepingBagDrop);
    }

    protected LootTable.Builder createBackpackDrop(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(ModDataComponents.TIER.get())
                                        .include(ModDataComponents.STORAGE_SLOTS.get())
                                        .include(ModDataComponents.UPGRADE_SLOTS.get())
                                        .include(ModDataComponents.TOOL_SLOTS.get())
                                        .include(ModDataComponents.BACKPACK_CONTAINER.get())
                                        .include(ModDataComponents.TOOLS_CONTAINER.get())
                                        .include(ModDataComponents.UPGRADES.get())
                                        .include(ModDataComponents.SHOW_TOOL_SLOTS.get())
                                        .include(ModDataComponents.SLEEPING_BAG_COLOR.get())
                                        .include(ModDataComponents.ABILITY_ENABLED.get())
                                        .include(ModDataComponents.COOLDOWN.get())
                                        .include(ModDataComponents.RENDER_INFO.get())
                                        .include(ModDataComponents.STARTER_UPGRADES.get())
                                        .include(ModDataComponents.SLOTS.get())
                                        .include(ModDataComponents.IS_VISIBLE.get())
                                        .include(ModDataComponents.UPGRADE_TICK_INTERVAL.get())
                                        .include(DataComponents.DYED_COLOR)))));
    }

    protected LootTable.Builder createSleepingBagDrop(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SleepingBagBlock.PART, BedPart.HEAD))))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Item> backpacks = Arrays.asList(ModRecipeProvider.BACKPACKS);
        List<Item> sleepingBags = List.of(ModItemsNeo.BLACK_SLEEPING_BAG.get(), ModItemsNeo.BLUE_SLEEPING_BAG.get(), ModItemsNeo.BROWN_SLEEPING_BAG.get(), ModItemsNeo.CYAN_SLEEPING_BAG.get(),
                ModItemsNeo.GRAY_SLEEPING_BAG.get(), ModItemsNeo.GREEN_SLEEPING_BAG.get(), ModItemsNeo.LIGHT_BLUE_SLEEPING_BAG.get(), ModItemsNeo.LIGHT_GRAY_SLEEPING_BAG.get(),
                ModItemsNeo.LIME_SLEEPING_BAG.get(), ModItemsNeo.MAGENTA_SLEEPING_BAG.get(), ModItemsNeo.PURPLE_SLEEPING_BAG.get(), ModItemsNeo.ORANGE_SLEEPING_BAG.get(),
                ModItemsNeo.PINK_SLEEPING_BAG.get(), ModItemsNeo.RED_SLEEPING_BAG.get(), ModItemsNeo.WHITE_SLEEPING_BAG.get(), ModItemsNeo.YELLOW_SLEEPING_BAG.get());
        return Stream.concat(backpacks.stream(), sleepingBags.stream()).map(Block::byItem)::iterator;
    }
}