package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeBuilder;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipeBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModRecipeProvider extends RecipeProvider {

    // The parameters are stored in protected fields
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        //Smithing
        for(Item item : BACKPACKS) {
            BackpackUpgradeRecipeBuilder.backpackUpgrade(ofTag(Tags.Items.LEATHERS), Ingredient.of(item), Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.BACKPACK_UPGRADES)), RecipeCategory.MISC, item).unlocks(getHasName(item), has(item)).save(output, getItemName(item) + "_smithing");
        }

        //Upgrades
        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.BLANK_UPGRADE.get(), 4)
                .define('A', Tags.Items.LEATHERS).define('B', Tags.Items.STRINGS)
                .pattern(" B ").pattern("BAB").pattern(" B ")
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS)).save(output);

        createFullGrid(ModItems.IRON_TIER_UPGRADE.get(), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                ofTag(Tags.Items.INGOTS_IRON), Items.IRON_INGOT, Tags.Items.INGOTS_IRON).save(output);
        createFullGrid(ModItems.GOLD_TIER_UPGRADE.get(), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                ofTag(Tags.Items.INGOTS_GOLD), Items.GOLD_INGOT, Tags.Items.INGOTS_GOLD).save(output);
        createFullGrid(ModItems.DIAMOND_TIER_UPGRADE.get(), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                ofTag(Tags.Items.GEMS_DIAMOND), Items.DIAMOND, Tags.Items.GEMS_DIAMOND).save(output);

        createBackpackSmallGrid(ModItems.DIAMOND_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.GEMS_DIAMOND), getHasName(Items.DIAMOND), has(Tags.Items.GEMS_DIAMOND)).save(output);
        createBackpackSmallGrid(ModItems.GOLD_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.INGOTS_GOLD), getHasName(Items.GOLD_INGOT), has(Tags.Items.INGOTS_GOLD)).save(output);
        createBackpackSmallGrid(ModItems.EMERALD_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.GEMS_EMERALD), getHasName(Items.EMERALD), has(Tags.Items.GEMS_EMERALD)).save(output);
        createBackpackSmallGrid(ModItems.IRON_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.INGOTS_IRON), getHasName(Items.IRON_INGOT), has(Tags.Items.INGOTS_IRON)).save(output);

        createBackpackSmallGrid(ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.ENDER_PEARLS), getHasName(Items.ENDER_PEARL), has(Tags.Items.ENDER_PEARLS)).save(output);
        createBackpackSmallGrid(ModItems.WOLF_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.BONES), getHasName(Items.BONE), has(Tags.Items.BONES)).save(output);
        createBackpackSmallGrid(ModItems.FOX_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.SWEET_BERRIES), getHasName(Items.SWEET_BERRIES), has(Items.SWEET_BERRIES)).save(output);
        createBackpackSmallGrid(ModItems.OCELOT_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.COD), getHasName(Items.COD), has(Items.COD)).save(output);
        //createBackpackSmallGrid(ModItems.SQUID_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.INK_SAC), getHasName(Items.INK_SAC), has(Items.INK_SAC)).save(output);

        createBackpackFullGrid(ModItems.REDSTONE_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.DUSTS_REDSTONE), getHasName(Items.REDSTONE), has(Tags.Items.DUSTS_REDSTONE)).save(output);
        createBackpackFullGrid(ModItems.COAL_TRAVELERS_BACKPACK.get(), ofTag(ItemTags.COALS), getHasName(Items.COAL), has(ItemTags.COALS)).save(output);
        createBackpackFullGrid(ModItems.SPONGE_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.SPONGE), getHasName(Items.SPONGE), has(Items.SPONGE)).save(output);
        createBackpackFullGrid(ModItems.HAY_TRAVELERS_BACKPACK.get(), ofTag(Tags.Items.CROPS_WHEAT), getHasName(Items.WHEAT), has(Tags.Items.CROPS_WHEAT)).save(output);

        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                        ofTag(Tags.Items.INGOTS_NETHERITE), RecipeCategory.MISC, ModItems.NETHERITE_TIER_UPGRADE.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite_tier_upgrade")));

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.TANKS_UPGRADE.get()).define('A', ModItems.BACKPACK_TANK)
                .define('B', ModItems.BLANK_UPGRADE.get()).pattern("ABA")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.CRAFTING_UPGRADE.get()).define('A', Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.CHESTS_WOODEN).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.FURNACE_UPGRADE.get()).define('A', Blocks.FURNACE)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.CHESTS_WOODEN).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE.get()), has(ModItems.BLANK_UPGRADE.get())).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SMOKER_UPGRADE.get()).define('A', Blocks.SMOKER)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.CHESTS_WOODEN).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE.get()), has(ModItems.BLANK_UPGRADE.get())).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.BLAST_FURNACE_UPGRADE.get()).define('A', Blocks.BLAST_FURNACE)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.CHESTS_WOODEN).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE.get()), has(ModItems.BLANK_UPGRADE.get())).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.FEEDING_UPGRADE.get()).define('A', Items.GOLDEN_CARROT)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Items.GOLDEN_APPLE).define('D', Tags.Items.DUSTS_REDSTONE).pattern("ABC").pattern("DDD")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.PICKUP_UPGRADE.get()).define('A', Items.HOPPER)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.DUSTS_REDSTONE).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.JUKEBOX_UPGRADE.get()).define('A', Tags.Items.DUSTS_REDSTONE)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Items.JUKEBOX).pattern(" B ").pattern("ACA")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.REFILL_UPGRADE.get()).define('A', Items.DROPPER)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.DUSTS_REDSTONE).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.MAGNET_UPGRADE.get()).define('A', Tags.Items.ENDER_PEARLS)
                .define('B', Tags.Items.DUSTS_REDSTONE).define('C', ModItems.BLANK_UPGRADE)
                .define('D', Tags.Items.GEMS_LAPIS).define('E', Tags.Items.INGOTS_IRON).pattern("A A").pattern("BCD").pattern("BED")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.VOID_UPGRADE.get()).define('A', Items.LAVA_BUCKET)
                .define('B', ModItems.BLANK_UPGRADE.get()).define('C', Tags.Items.DUSTS_REDSTONE).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.LANTERN_UPGRADE.get()).define('A', Items.LANTERN)
                .define('B', ModItems.BLANK_UPGRADE).define('C', Tags.Items.DUSTS_REDSTONE).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(output);

        //All Recipes
        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.BACKPACK_TANK.get())
                .define('B', Tags.Items.GLASS_BLOCKS_COLORLESS).define('A', Tags.Items.INGOTS_IRON)
                .pattern("BAB").pattern("B B").pattern("BAB")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.HOSE_NOZZLE.get())
                .define('A', Tags.Items.INGOTS_GOLD).define('B', Tags.Items.INGOTS_IRON)
                .pattern(" A ").pattern("B B")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(output);

        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TOOLS, ModItems.HOSE.get())
                .define('A', ModItems.HOSE_NOZZLE.get()).define('B', Tags.Items.DYES_GREEN)
                .pattern("ABB").pattern("  B").pattern("  B")
                .unlockedBy(getHasName(ModItems.HOSE_NOZZLE.get()), has(ModItems.HOSE_NOZZLE.get())).save(output);

        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.STANDARD_TRAVELERS_BACKPACK.get()).group("standard_travelers_backpack")
                .define('X', Tags.Items.LEATHERS).define('B', Tags.Items.STRINGS).define('C', ModItems.BACKPACK_TANK.get())
                .define('D', Tags.Items.CHESTS_WOODEN).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern("CDC").pattern("XSX")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(output);

        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.STANDARD_TRAVELERS_BACKPACK.get()).group("standard_travelers_backpack")
                .define('X', Tags.Items.LEATHERS).define('B', Tags.Items.STRINGS)
                .define('D', Tags.Items.CHESTS_WOODEN).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern(" D ").pattern("XSX")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "standard_no_tanks")));

        //Netherite backpack
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_TRAVELERS_BACKPACK.get()), ofTag(Tags.Items.INGOTS_NETHERITE),
                        RecipeCategory.MISC, ModItems.NETHERITE_TRAVELERS_BACKPACK.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE)).save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite")));

        //Bee
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.BEE_TRAVELERS_BACKPACK.get())
                .define('A', Items.HONEYCOMB).define('B', Items.HONEY_BOTTLE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.HONEYCOMB), has(Items.HONEYCOMB)).unlockedBy(getHasName(Items.HONEY_BOTTLE), has(Items.HONEY_BOTTLE)).save(output);

        //Blaze
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.BLAZE_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.RODS_BLAZE).define('B', Items.FIRE_CHARGE)
                .define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.BLAZE_POWDER).define('E', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("DED")
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Tags.Items.RODS_BLAZE)).save(output);

        //Bookshelf
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get())
                .define('A', ItemTags.PLANKS).define('B', Items.BOOK).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("BCB").pattern("AAA")
                .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK)).save(output);

        //Cactus
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.CACTUS_TRAVELERS_BACKPACK.get())
                .define('A', Items.CACTUS).define('B', Tags.Items.DYES_GREEN)
                .define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Tags.Items.SANDS)
                .pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.CACTUS), has(Items.CACTUS)).save(output);

        //Cake
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.CAKE_TRAVELERS_BACKPACK.get())
                .define('A', Items.MILK_BUCKET).define('B', Tags.Items.EGGS).define('C', Items.SUGAR)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Tags.Items.CROPS_WHEAT)
                .pattern("ABA").pattern("CDC").pattern("EEE")
                .unlockedBy(getHasName(Items.EGG), has(Tags.Items.EGGS)).save(output);

        //Chicken
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.CHICKEN_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.FEATHERS).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .define('C', Tags.Items.EGGS).pattern(" A ").pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS)).save(output);

        //Cow
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.COW_TRAVELERS_BACKPACK.get())
                .define('A', Items.BEEF).define('B', Tags.Items.LEATHERS).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.MILK_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("BDB")
                .unlockedBy(getHasName(Items.MILK_BUCKET), has(Items.MILK_BUCKET)).save(output);

        //Creeper
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.CREEPER_TRAVELERS_BACKPACK.get())
                .define('A', Items.GUNPOWDER).define('B', Items.CREEPER_HEAD).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .define('D', Items.TNT).pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.GUNPOWDER), has(Items.GUNPOWDER)).save(output);

        //Dragon
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.DRAGON_TRAVELERS_BACKPACK.get())
                .define('A', Items.DRAGON_BREATH).define('B', Items.DRAGON_HEAD).define('C', Tags.Items.END_STONES)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Tags.Items.ENDER_PEARLS)
                .pattern("ABA").pattern("CDC").pattern("ECE")
                .unlockedBy(getHasName(Items.DRAGON_BREATH), has(Items.DRAGON_BREATH)).save(output);

        //End
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.END_TRAVELERS_BACKPACK.get())
                .define('A', Items.ENDER_EYE).define('B', Tags.Items.END_STONES).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE)).save(output);

        //Ghast
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.GHAST_TRAVELERS_BACKPACK.get())
                .define('A', Items.GHAST_TEAR).define('B', Items.FIRE_CHARGE).define('C', Tags.Items.GUNPOWDERS)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.GHAST_TEAR), has(Items.GHAST_TEAR)).save(output);

        //Horse
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.HORSE_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.LEATHERS).define('B', Items.APPLE).define('C', Tags.Items.CROPS_WHEAT).define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER)).save(output);

        //Lapis
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.LAPIS_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.STORAGE_BLOCKS_LAPIS).define('B', Tags.Items.GEMS_LAPIS).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Tags.Items.GEMS_LAPIS)).save(output);

        //Magma Cube
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get())
                .define('A', Items.MAGMA_CREAM).define('B', Items.LAVA_BUCKET).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.MAGMA_CREAM), has(Items.MAGMA_CREAM)).save(output);

        //Melon
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.MELON_TRAVELERS_BACKPACK.get())
                .define('A', Items.MELON_SLICE).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('C', Items.MELON_SEEDS)
                .pattern("A A").pattern("ABA").pattern("ACA")
                .unlockedBy(getHasName(Items.MELON_SLICE), has(Items.MELON_SLICE)).save(output);

        //Nether
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NETHER_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.GEMS_QUARTZ).define('B', Tags.Items.CROPS_NETHER_WART).define('C', Tags.Items.NETHERRACKS)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Items.BLACKSTONE).define('F', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("CDC").pattern("EFE")
                .unlockedBy(getHasName(Items.NETHER_WART), has(Tags.Items.CROPS_NETHER_WART)).save(output);

        //Pig
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.PIG_TRAVELERS_BACKPACK.get())
                .define('A', Items.PORKCHOP).define('B', Tags.Items.CROPS_CARROT).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP)).save(output);

        //Pumpkin
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.PUMPKIN_TRAVELERS_BACKPACK.get())
                .define('A', Items.PUMPKIN).define('B', Items.CARVED_PUMPKIN).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Tags.Items.SEEDS_PUMPKIN)
                .pattern("ABA").pattern("ACA").pattern("ADA")
                .unlockedBy(getHasName(Items.PUMPKIN), has(Items.PUMPKIN)).save(output);

        //Quartz
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.QUARTZ_TRAVELERS_BACKPACK.get())
                .define('A', Items.QUARTZ_BLOCK).define('B', Tags.Items.GEMS_QUARTZ).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.QUARTZ), has(Tags.Items.GEMS_QUARTZ)).save(output);

        //Sandstone
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SANDSTONE_TRAVELERS_BACKPACK.get())
                .define('A', Items.SANDSTONE).define('B', Items.CHISELED_SANDSTONE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.SANDSTONE), has(Items.SANDSTONE)).save(output);

        //Sheep
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SHEEP_TRAVELERS_BACKPACK.get())
                .define('A', Items.WHITE_WOOL).define('B', Items.MUTTON).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.WHITE_WOOL), has(Items.WHITE_WOOL)).save(output);

        //Skeleton
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SKELETON_TRAVELERS_BACKPACK.get())
                .define('A', Items.BONE).define('B', Items.ARROW).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.BOW)
                .pattern("ABA").pattern("BCB").pattern("ADA")
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW)).save(output);

        //Snow
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SNOW_TRAVELERS_BACKPACK.get())
                .define('A', Items.ICE).define('B', Items.SNOW_BLOCK).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.SNOWBALL)
                .pattern("AAA").pattern("BCB").pattern("DBD")
                .unlockedBy(getHasName(Items.SNOWBALL), has(Items.SNOWBALL)).save(output);

        //Spider
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SPIDER_TRAVELERS_BACKPACK.get())
                .define('A', Items.SPIDER_EYE).define('B', Items.STRING).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING)).save(output);

        //Squid
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SQUID_TRAVELERS_BACKPACK.get())
                .define('A', Items.GLOW_INK_SAC).define('B', Items.INK_SAC).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.INK_SAC), has(Items.INK_SAC)).save(output);

        //Wither
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.WITHER_TRAVELERS_BACKPACK.get())
                .define('A', Items.WITHER_SKELETON_SKULL).define('B', Items.SOUL_SAND).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("BCB").pattern(" B ")
                .unlockedBy(getHasName(Items.WITHER_SKELETON_SKULL), has(Items.WITHER_SKELETON_SKULL)).save(output);

        //Warden
        ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.WARDEN_TRAVELERS_BACKPACK.get())
                .define('A', Items.ECHO_SHARD).define('B', Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD)).save(output);

        //Sleeping Bags
        sleepingBagFromWool(output, ModItems.BLACK_SLEEPING_BAG.get(), Items.BLACK_WOOL);
        sleepingBagFromWool(output, ModItems.BLUE_SLEEPING_BAG.get(), Items.BLUE_WOOL);
        sleepingBagFromWool(output, ModItems.BROWN_SLEEPING_BAG.get(), Items.BROWN_WOOL);
        sleepingBagFromWool(output, ModItems.CYAN_SLEEPING_BAG.get(), Items.CYAN_WOOL);
        sleepingBagFromWool(output, ModItems.GRAY_SLEEPING_BAG.get(), Items.GRAY_WOOL);
        sleepingBagFromWool(output, ModItems.GREEN_SLEEPING_BAG.get(), Items.GREEN_WOOL);
        sleepingBagFromWool(output, ModItems.LIGHT_BLUE_SLEEPING_BAG.get(), Items.LIGHT_BLUE_WOOL);
        sleepingBagFromWool(output, ModItems.LIGHT_GRAY_SLEEPING_BAG.get(), Items.LIGHT_GRAY_WOOL);
        sleepingBagFromWool(output, ModItems.LIME_SLEEPING_BAG.get(), Items.LIME_WOOL);
        sleepingBagFromWool(output, ModItems.MAGENTA_SLEEPING_BAG.get(), Items.MAGENTA_WOOL);
        sleepingBagFromWool(output, ModItems.ORANGE_SLEEPING_BAG.get(), Items.ORANGE_WOOL);
        sleepingBagFromWool(output, ModItems.PINK_SLEEPING_BAG.get(), Items.PINK_WOOL);
        sleepingBagFromWool(output, ModItems.PURPLE_SLEEPING_BAG.get(), Items.PURPLE_WOOL);
        sleepingBagFromWool(output, ModItems.RED_SLEEPING_BAG.get(), Items.RED_WOOL);
        sleepingBagFromWool(output, ModItems.WHITE_SLEEPING_BAG.get(), Items.WHITE_WOOL);
        sleepingBagFromWool(output, ModItems.YELLOW_SLEEPING_BAG.get(), Items.YELLOW_WOOL);

        List<Item> list = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);
        List<Item> list2 = List.of(ModItems.BLACK_SLEEPING_BAG.get(), ModItems.BLUE_SLEEPING_BAG.get(), ModItems.BROWN_SLEEPING_BAG.get(), ModItems.CYAN_SLEEPING_BAG.get(), ModItems.GRAY_SLEEPING_BAG.get(), ModItems.GREEN_SLEEPING_BAG.get(), ModItems.LIGHT_BLUE_SLEEPING_BAG.get(), ModItems.LIGHT_GRAY_SLEEPING_BAG.get(), ModItems.LIME_SLEEPING_BAG.get(), ModItems.MAGENTA_SLEEPING_BAG.get(), ModItems.ORANGE_SLEEPING_BAG.get(), ModItems.PINK_SLEEPING_BAG.get(), ModItems.PURPLE_SLEEPING_BAG.get(), ModItems.RED_SLEEPING_BAG.get(), ModItems.YELLOW_SLEEPING_BAG.get(), ModItems.WHITE_SLEEPING_BAG.get());
        colorWithDye(output, list, list2, null, "sleeping_bag");
    }

    public Ingredient ofTag(TagKey<Item> tag) {
        return Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag));
    }

    public ShapedBackpackRecipeBuilder createBackpackSmallGrid(Item result, Ingredient ingredient, String criterionName, Criterion<?> criterion) {
        return ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, result)
                .define('A', ingredient).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern(" A ").pattern("ABA").pattern(" A ").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createBackpackFullGrid(Item result, Ingredient ingredient, String criterionName, Criterion<?> criterion) {
        return ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, result)
                .define('A', ingredient).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createFullGrid(Item result, Ingredient ingredient, Ingredient ingredient1, Item unlocker, TagKey<Item> tag) {
        return ShapedBackpackRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, result)
                .define('A', ingredient1).define('B', ingredient)
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(getHasName(unlocker), has(tag));
    }

    protected void sleepingBagFromWool(RecipeOutput recipeOutput, ItemLike sleepingBag, ItemLike pWool) {
        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.DECORATIONS, sleepingBag).group("sleeping_bag").define('#', pWool).define('X', Items.WHITE_WOOL).pattern("##X").unlockedBy(getHasName(pWool), has(pWool)).save(recipeOutput);
    }

    protected void colorWithDye(RecipeOutput output, List<Item> dyes, List<Item> dyeableItems, @Nullable Item dye, String group) {
        for(int i = 0; i < dyes.size(); i++) {
            Item item = dyes.get(i);
            Item item1 = dyeableItems.get(i);
            Stream<Item> stream = dyeableItems.stream().filter(p_288265_ -> !p_288265_.equals(item1));
            if(dye != null) {
                stream = Stream.concat(stream, Stream.of(dye));
            }

            ShapelessRecipeBuilder.shapeless(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.BUILDING_BLOCKS, item1)
                    .requires(item)
                    .requires(Ingredient.of(stream))
                    .group(group)
                    .unlockedBy("has_needed_dye", this.has(item))
                    .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "dye_" + getItemName(item1))));
        }
    }

    public static final Item[] BACKPACKS = {
            ModItems.STANDARD_TRAVELERS_BACKPACK.get(),
            ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
            ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
            ModItems.GOLD_TRAVELERS_BACKPACK.get(),
            ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_TRAVELERS_BACKPACK.get(),
            ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
            ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),
            ModItems.COAL_TRAVELERS_BACKPACK.get(),

            ModItems.QUARTZ_TRAVELERS_BACKPACK.get(),
            ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
            ModItems.END_TRAVELERS_BACKPACK.get(),
            ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            ModItems.SNOW_TRAVELERS_BACKPACK.get(),
            ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

            ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
            ModItems.HAY_TRAVELERS_BACKPACK.get(),
            ModItems.MELON_TRAVELERS_BACKPACK.get(),
            ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
            ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
            ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
            ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItems.WITHER_TRAVELERS_BACKPACK.get(),
            ModItems.WARDEN_TRAVELERS_BACKPACK.get(),

            ModItems.BAT_TRAVELERS_BACKPACK.get(),
            ModItems.BEE_TRAVELERS_BACKPACK.get(),
            ModItems.WOLF_TRAVELERS_BACKPACK.get(),
            ModItems.FOX_TRAVELERS_BACKPACK.get(),
            ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
            ModItems.HORSE_TRAVELERS_BACKPACK.get(),
            ModItems.COW_TRAVELERS_BACKPACK.get(),
            ModItems.PIG_TRAVELERS_BACKPACK.get(),
            ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
            ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
            ModItems.SQUID_TRAVELERS_BACKPACK.get(),
            ModItems.VILLAGER_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get(),
    };

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Traveler's Backpack Recipes";
        }
    }
}