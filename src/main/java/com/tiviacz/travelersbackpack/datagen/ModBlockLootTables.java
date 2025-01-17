package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBlockLootTables extends FabricBlockLootTableProvider {
    protected ModBlockLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
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
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy(backpackPath(ModDataHelper.TIER), ModDataHelper.TIER)
                                        .copy(backpackPath(ModDataHelper.STORAGE_SLOTS), ModDataHelper.STORAGE_SLOTS)
                                        .copy(backpackPath(ModDataHelper.UPGRADE_SLOTS), ModDataHelper.UPGRADE_SLOTS)
                                        .copy(backpackPath(ModDataHelper.TOOL_SLOTS), ModDataHelper.TOOL_SLOTS)
                                        .copy(backpackPath(ModDataHelper.BACKPACK_CONTAINER), ModDataHelper.BACKPACK_CONTAINER)
                                        .copy(backpackPath(ModDataHelper.TOOLS_CONTAINER), ModDataHelper.TOOLS_CONTAINER)
                                        .copy(backpackPath(ModDataHelper.UPGRADES), ModDataHelper.UPGRADES)
                                        .copy(backpackPath(ModDataHelper.SHOW_TOOL_SLOTS), ModDataHelper.SHOW_TOOL_SLOTS)
                                        .copy(backpackPath(ModDataHelper.SLEEPING_BAG_COLOR), ModDataHelper.SLEEPING_BAG_COLOR)
                                        .copy(backpackPath(ModDataHelper.ABILITY_ENABLED), ModDataHelper.ABILITY_ENABLED)
                                        .copy(backpackPath(ModDataHelper.COOLDOWN), ModDataHelper.COOLDOWN)
                                        .copy(backpackPath(ModDataHelper.RENDER_INFO), ModDataHelper.RENDER_INFO)
                                        .copy(backpackPath(ModDataHelper.STARTER_UPGRADES), ModDataHelper.STARTER_UPGRADES)
                                        .copy(backpackPath(ModDataHelper.SLOTS), ModDataHelper.SLOTS)
                                        .copy(backpackPath(ModDataHelper.IS_VISIBLE), ModDataHelper.IS_VISIBLE)
                                        .copy(backpackPath(ModDataHelper.UPGRADE_TICK_INTERVAL), ModDataHelper.UPGRADE_TICK_INTERVAL)
                                        .copy(backpackPath(ModDataHelper.COLOR), ModDataHelper.COLOR)))));
    }

    protected String backpackPath(String path) {
        return "Backpack.tag." + path;
    }

    protected LootTable.Builder createSleepingBagDrop(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SleepingBagBlock.PART, BedPart.HEAD))))));
    }
}