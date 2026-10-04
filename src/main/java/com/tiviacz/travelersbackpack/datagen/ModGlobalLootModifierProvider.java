package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.loot.AddItemModifier;
import com.tiviacz.travelersbackpack.loot.HayBackpackLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TravelersBackpack.MODID);
    }

    @Override
    protected void start() {
        //Abandoned Mineshaft
        this.add("abandoned_mineshaft_add_bat",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05F).build() },
                        ModItems.BAT_TRAVELERS_BACKPACK.get()),
                List.of());
        this.add("abandoned_mineshaft_add_standard",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.STANDARD_TRAVELERS_BACKPACK.get()),
                List.of());
        this.add("abandoned_mineshaft_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());
        this.add("abandoned_mineshaft_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());

        //Pillager Outpost
        this.add("pillager_outpost_add_iron_golem",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/pillager_outpost")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()),
                List.of());

        //Simple Dungeon
        this.add("simple_dungeon_add_standard",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/simple_dungeon")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.STANDARD_TRAVELERS_BACKPACK.get()),
                List.of());
        this.add("simple_dungeon_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/simple_dungeon")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());

        //Desert Pyramid
        this.add("desert_pyramid_add_standard",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.STANDARD_TRAVELERS_BACKPACK.get()),
                List.of());
        this.add("desert_pyramid_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());
        this.add("desert_pyramid_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());

        //Shipwreck Treasure
        this.add("shipwreck_treasure_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/shipwreck_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());
        this.add("shipwreck_treasure_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/shipwreck_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());

        //Woodland Mansion
        this.add("woodland_mansion_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());
        this.add("woodland_mansion_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());

        //Nether Bridge
        this.add("nether_bridge_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/nether_bridge")).build(),
                        LootItemRandomChanceCondition.randomChance(0.07F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());
        this.add("nether_bridge_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/nether_bridge")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());

        //Bastion Treasure
        this.add("bastion_treasure_add_iron_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/bastion_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.07F).build() },
                        ModItems.IRON_TIER_UPGRADE.get()),
                List.of());
        this.add("bastion_treasure_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/bastion_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());

        //Endcity Treasure
        this.add("end_city_treasure_add_gold_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.07F).build() },
                        ModItems.GOLD_TIER_UPGRADE.get()),
                List.of());
        this.add("end_city_treasure_add_diamond_tier_upgrade",
                new AddItemModifier(new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06F).build() },
                        ModItems.DIAMOND_TIER_UPGRADE.get()),
                List.of());

        //Hay Backpack Ability
        this.add("hay_backpack_ability",
                new HayBackpackLootModifier(new LootItemCondition[] {},
                        List.of(Items.CARROT, Items.POTATO, Items.BEETROOT),
                        0.15F, 0.4F, 2), List.of());
    }
}