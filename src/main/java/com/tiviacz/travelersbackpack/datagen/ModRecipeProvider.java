package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeBuilder;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipeBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> writer) {
        //Smithing
        for(Item item : BACKPACKS) {
            BackpackUpgradeRecipeBuilder.backpackUpgrade(Ingredient.of(Items.LEATHER), Ingredient.of(item), Ingredient.of(ModTags.BACKPACK_UPGRADES), RecipeCategory.MISC, item).unlocks(getHasName(item), has(item)).save(writer, id(getItemName(item) + "_smithing"));
        }

        //Upgrades
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLANK_UPGRADE, 4)
                .define('A', Items.LEATHER).define('B', Items.STRING)
                .pattern(" B ").pattern("BAB").pattern(" B ")
                .unlockedBy("has_leather", has(Items.LEATHER)).save(writer, id("blank_upgrade"));

        createFullGrid(ModItems.IRON_TIER_UPGRADE, Ingredient.of(ModItems.BLANK_UPGRADE),
                Ingredient.of(ConventionalItemTags.IRON_INGOTS), Items.IRON_INGOT, ConventionalItemTags.IRON_INGOTS).save(writer, id("iron_tier_upgrade"));
        createFullGrid(ModItems.GOLD_TIER_UPGRADE, Ingredient.of(ModItems.BLANK_UPGRADE),
                Ingredient.of(ConventionalItemTags.GOLD_INGOTS), Items.GOLD_INGOT, ConventionalItemTags.GOLD_INGOTS).save(writer, id("gold_tier_upgrade"));
        createFullGrid(ModItems.DIAMOND_TIER_UPGRADE, Ingredient.of(ModItems.BLANK_UPGRADE),
                Ingredient.of(ConventionalItemTags.DIAMONDS), Items.DIAMOND, ConventionalItemTags.DIAMONDS).save(writer, id("diamond_tier_upgrade"));

        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLANK_UPGRADE),
                        Ingredient.of(ConventionalItemTags.NETHERITE_INGOTS), RecipeCategory.MISC, ModItems.NETHERITE_TIER_UPGRADE)
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(ConventionalItemTags.NETHERITE_INGOTS))
                .save(writer, id("netherite_tier_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TANKS_UPGRADE).define('A', ModItems.BACKPACK_TANK)
                .define('B', ModItems.BLANK_UPGRADE).pattern("ABA")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("tanks_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRAFTING_UPGRADE).define('A', Blocks.CRAFTING_TABLE)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.CHESTS).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("crafting_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FURNACE_UPGRADE).define('A', Blocks.FURNACE)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.CHESTS).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("furnace_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SMOKER_UPGRADE).define('A', Blocks.SMOKER)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.CHESTS).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("smoker_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLAST_FURNACE_UPGRADE).define('A', Blocks.BLAST_FURNACE)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.CHESTS).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("blast_furnace_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FEEDING_UPGRADE).define('A', Items.GOLDEN_CARROT)
                .define('B', ModItems.BLANK_UPGRADE).define('C', Items.GOLDEN_APPLE).define('D', ConventionalItemTags.REDSTONE_DUSTS).pattern("ABC").pattern("DDD")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("feeding_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PICKUP_UPGRADE).define('A', Items.HOPPER)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.REDSTONE_DUSTS).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("pickup_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.JUKEBOX_UPGRADE).define('A', ConventionalItemTags.REDSTONE_DUSTS)
                .define('B', ModItems.BLANK_UPGRADE).define('C', Items.JUKEBOX).pattern(" B ").pattern("ACA")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("jukebox_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REFILL_UPGRADE).define('A', Items.DROPPER)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.REDSTONE_DUSTS).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("refill_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGNET_UPGRADE).define('A', Items.ENDER_PEARL)
                .define('B', ConventionalItemTags.REDSTONE_DUSTS).define('C', ModItems.BLANK_UPGRADE)
                .define('D', ConventionalItemTags.LAPIS).define('E', ConventionalItemTags.IRON_INGOTS).pattern("A A").pattern("BCD").pattern("BED")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("magnet_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VOID_UPGRADE).define('A', Items.LAVA_BUCKET)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.REDSTONE_DUSTS).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("void_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LANTERN_UPGRADE).define('A', Items.LANTERN)
                .define('B', ModItems.BLANK_UPGRADE).define('C', ConventionalItemTags.REDSTONE_DUSTS).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItems.BLANK_UPGRADE), has(ModItems.BLANK_UPGRADE)).save(writer, id("lantern_upgrade"));

        //All Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BACKPACK_TANK)
                .define('B', ConventionalItemTags.GLASS_BLOCKS).define('A', ConventionalItemTags.IRON_INGOTS)
                .pattern("BAB").pattern("B B").pattern("BAB")
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS)).save(writer, id("backpack_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HOSE_NOZZLE)
                .define('A', ConventionalItemTags.GOLD_INGOTS).define('B', ConventionalItemTags.IRON_INGOTS)
                .pattern(" A ").pattern("B B")
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS)).save(writer, id("hose_nozzle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HOSE)
                .define('A', ModItems.HOSE_NOZZLE).define('B', ConventionalItemTags.GREEN_DYES)
                .pattern("ABB").pattern("  B").pattern("  B")
                .unlockedBy(getHasName(ModItems.HOSE_NOZZLE), has(ModItems.HOSE_NOZZLE)).save(writer, id("hose"));

        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STANDARD_TRAVELERS_BACKPACK).group("standard_travelers_backpack")
                .define('X', Items.LEATHER).define('B', Items.STRING).define('C', ModItems.BACKPACK_TANK)
                .define('D', ConventionalItemTags.CHESTS).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern("CDC").pattern("XSX")
                .unlockedBy("has_chest", has(ConventionalItemTags.CHESTS)).save(writer, id("standard"));

        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STANDARD_TRAVELERS_BACKPACK).group("standard_travelers_backpack")
                .define('X', Items.LEATHER).define('B', Items.STRING)
                .define('D', ConventionalItemTags.CHESTS).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern(" D ").pattern("XSX")
                .unlockedBy("has_chest", has(ConventionalItemTags.CHESTS)).save(writer, id("standard_no_tanks"));

        //Netherite backpack
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_TRAVELERS_BACKPACK), Ingredient.of(ConventionalItemTags.NETHERITE_INGOTS),
                        RecipeCategory.MISC, ModItems.NETHERITE_TRAVELERS_BACKPACK)
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(ConventionalItemTags.NETHERITE_INGOTS)).save(writer, id("netherite"));

        createBackpackSmallGrid(ModItems.DIAMOND_TRAVELERS_BACKPACK, ConventionalItemTags.DIAMONDS, getHasName(Items.DIAMOND), has(ConventionalItemTags.DIAMONDS)).save(writer, id("diamond"));
        createBackpackSmallGrid(ModItems.GOLD_TRAVELERS_BACKPACK, Ingredient.of(ConventionalItemTags.GOLD_INGOTS), getHasName(Items.GOLD_INGOT), has(ConventionalItemTags.GOLD_INGOTS)).save(writer, id("gold"));
        createBackpackSmallGrid(ModItems.EMERALD_TRAVELERS_BACKPACK, Ingredient.of(ConventionalItemTags.EMERALDS), getHasName(Items.EMERALD), has(ConventionalItemTags.EMERALDS)).save(writer, id("emerald"));
        createBackpackSmallGrid(ModItems.IRON_TRAVELERS_BACKPACK, Ingredient.of(ConventionalItemTags.IRON_INGOTS), getHasName(Items.IRON_INGOT), has(ConventionalItemTags.IRON_INGOTS)).save(writer, id("iron"));

        createBackpackSmallGrid(ModItems.ENDERMAN_TRAVELERS_BACKPACK, Ingredient.of(Items.ENDER_PEARL), getHasName(Items.ENDER_PEARL), has(Items.ENDER_PEARL)).save(writer, id("enderman"));
        createBackpackSmallGrid(ModItems.WOLF_TRAVELERS_BACKPACK, Ingredient.of(Items.BONE), getHasName(Items.BONE), has(Items.BONE)).save(writer, id("wolf"));
        createBackpackSmallGrid(ModItems.FOX_TRAVELERS_BACKPACK, Ingredient.of(Items.SWEET_BERRIES), getHasName(Items.SWEET_BERRIES), has(Items.SWEET_BERRIES)).save(writer, id("fox"));
        createBackpackSmallGrid(ModItems.OCELOT_TRAVELERS_BACKPACK, Ingredient.of(Items.COD), getHasName(Items.COD), has(Items.COD)).save(writer, id("ocelot"));

        createBackpackFullGrid(ModItems.REDSTONE_TRAVELERS_BACKPACK, Ingredient.of(ConventionalItemTags.REDSTONE_DUSTS), getHasName(Items.REDSTONE), has(ConventionalItemTags.REDSTONE_DUSTS)).save(writer, id("redstone"));
        createBackpackFullGrid(ModItems.COAL_TRAVELERS_BACKPACK, Ingredient.of(ItemTags.COALS), getHasName(Items.COAL), has(ItemTags.COALS)).save(writer, id("coal"));
        createBackpackFullGrid(ModItems.SPONGE_TRAVELERS_BACKPACK, Ingredient.of(Items.SPONGE), getHasName(Items.SPONGE), has(Items.SPONGE)).save(writer, id("sponge"));
        createBackpackFullGrid(ModItems.HAY_TRAVELERS_BACKPACK, Ingredient.of(Items.WHEAT), getHasName(Items.WHEAT), has(Items.WHEAT)).save(writer, id("hay"));

        //Bee
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BEE_TRAVELERS_BACKPACK)
                .define('A', Items.HONEYCOMB).define('B', Items.HONEY_BOTTLE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.HONEYCOMB), has(Items.HONEYCOMB)).unlockedBy(getHasName(Items.HONEY_BOTTLE), has(Items.HONEY_BOTTLE)).save(writer);

        //Blaze
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLAZE_TRAVELERS_BACKPACK)
                .define('A', Items.BLAZE_ROD).define('B', Items.FIRE_CHARGE)
                .define('C', ModItems.STANDARD_TRAVELERS_BACKPACK).define('D', Items.BLAZE_POWDER).define('E', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("DED")
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD)).save(writer);

        //Bookshelf
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOOKSHELF_TRAVELERS_BACKPACK)
                .define('A', ItemTags.PLANKS).define('B', Items.BOOK).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("AAA").pattern("BCB").pattern("AAA")
                .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK)).save(writer);

        //Cactus
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CACTUS_TRAVELERS_BACKPACK)
                .define('A', Items.CACTUS).define('B', ConventionalItemTags.GREEN_DYES)
                .define('C', ModItems.STANDARD_TRAVELERS_BACKPACK).define('D', ItemTags.SAND)
                .pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.CACTUS), has(Items.CACTUS)).save(writer);

        //Cake
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CAKE_TRAVELERS_BACKPACK)
                .define('A', Items.MILK_BUCKET).define('B', Items.EGG).define('C', Items.SUGAR)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK).define('E', Items.WHEAT)
                .pattern("ABA").pattern("CDC").pattern("EEE")
                .unlockedBy(getHasName(Items.EGG), has(Items.EGG)).save(writer);

        //Chicken
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHICKEN_TRAVELERS_BACKPACK)
                .define('A', Items.FEATHER).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .define('C', Items.EGG).pattern(" A ").pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(Items.FEATHER), has(Items.FEATHER)).save(writer);

        //Cow
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COW_TRAVELERS_BACKPACK)
                .define('A', Items.BEEF).define('B', Items.LEATHER).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK).define('D', Items.MILK_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("BDB")
                .unlockedBy(getHasName(Items.MILK_BUCKET), has(Items.MILK_BUCKET)).save(writer);

        //Creeper
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CREEPER_TRAVELERS_BACKPACK)
                .define('A', Items.GUNPOWDER).define('B', Items.CREEPER_HEAD).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .define('D', Items.TNT).pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.GUNPOWDER), has(Items.GUNPOWDER)).save(writer);

        //Dragon
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DRAGON_TRAVELERS_BACKPACK)
                .define('A', Items.DRAGON_BREATH).define('B', Items.DRAGON_HEAD).define('C', Items.END_STONE)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK).define('E', Items.ENDER_PEARL)
                .pattern("ABA").pattern("CDC").pattern("ECE")
                .unlockedBy(getHasName(Items.DRAGON_BREATH), has(Items.DRAGON_BREATH)).save(writer);

        //End
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.END_TRAVELERS_BACKPACK)
                .define('A', Items.ENDER_EYE).define('B', Items.END_STONE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE)).save(writer);

        //Ghast
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GHAST_TRAVELERS_BACKPACK)
                .define('A', Items.GHAST_TEAR).define('B', Items.FIRE_CHARGE).define('C', Items.GUNPOWDER)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK).pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.GHAST_TEAR), has(Items.GHAST_TEAR)).save(writer);

        //Horse
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HORSE_TRAVELERS_BACKPACK)
                .define('A', Items.LEATHER).define('B', Items.APPLE).define('C', Items.WHEAT).define('D', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER)).save(writer);

        //Lapis
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LAPIS_TRAVELERS_BACKPACK)
                .define('A', Items.LAPIS_BLOCK).define('B', ConventionalItemTags.LAPIS).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(ConventionalItemTags.LAPIS)).save(writer);

        //Magma Cube
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK)
                .define('A', Items.MAGMA_CREAM).define('B', Items.LAVA_BUCKET).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.MAGMA_CREAM), has(Items.MAGMA_CREAM)).save(writer);

        //Melon
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MELON_TRAVELERS_BACKPACK)
                .define('A', Items.MELON_SLICE).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK).define('C', Items.MELON_SEEDS)
                .pattern("A A").pattern("ABA").pattern("ACA")
                .unlockedBy(getHasName(Items.MELON_SLICE), has(Items.MELON_SLICE)).save(writer);

        //Nether
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NETHER_TRAVELERS_BACKPACK)
                .define('A', ConventionalItemTags.QUARTZ).define('B', Items.NETHER_WART).define('C', Items.NETHERRACK)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK).define('E', Items.BLACKSTONE).define('F', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("CDC").pattern("EFE")
                .unlockedBy(getHasName(Items.NETHER_WART), has(Items.NETHER_WART)).save(writer);

        //Pig
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PIG_TRAVELERS_BACKPACK)
                .define('A', Items.PORKCHOP).define('B', Items.CARROT).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP)).save(writer);

        //Pumpkin
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PUMPKIN_TRAVELERS_BACKPACK)
                .define('A', Items.PUMPKIN).define('B', Items.CARVED_PUMPKIN).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK).define('D', Items.PUMPKIN_SEEDS)
                .pattern("ABA").pattern("ACA").pattern("ADA")
                .unlockedBy(getHasName(Items.PUMPKIN), has(Items.PUMPKIN)).save(writer);

        //Quartz
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.QUARTZ_TRAVELERS_BACKPACK)
                .define('A', Items.QUARTZ_BLOCK).define('B', ConventionalItemTags.QUARTZ).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.QUARTZ), has(ConventionalItemTags.QUARTZ)).save(writer);

        //Sandstone
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SANDSTONE_TRAVELERS_BACKPACK)
                .define('A', Items.SANDSTONE).define('B', Items.CHISELED_SANDSTONE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.SANDSTONE), has(Items.SANDSTONE)).save(writer);

        //Sheep
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHEEP_TRAVELERS_BACKPACK)
                .define('A', Items.WHITE_WOOL).define('B', Items.MUTTON).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.WHITE_WOOL), has(Items.WHITE_WOOL)).save(writer);

        //Skeleton
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SKELETON_TRAVELERS_BACKPACK)
                .define('A', Items.BONE).define('B', Items.ARROW).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK).define('D', Items.BOW)
                .pattern("ABA").pattern("BCB").pattern("ADA")
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW)).save(writer);

        //Snow
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SNOW_TRAVELERS_BACKPACK)
                .define('A', Items.ICE).define('B', Items.SNOW_BLOCK).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK).define('D', Items.SNOWBALL)
                .pattern("AAA").pattern("BCB").pattern("DBD")
                .unlockedBy(getHasName(Items.SNOWBALL), has(Items.SNOWBALL)).save(writer);

        //Spider
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPIDER_TRAVELERS_BACKPACK)
                .define('A', Items.SPIDER_EYE).define('B', Items.STRING).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING)).save(writer);

        //Squid
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SQUID_TRAVELERS_BACKPACK)
                .define('A', Items.GLOW_INK_SAC).define('B', Items.INK_SAC).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.INK_SAC), has(Items.INK_SAC)).save(writer);

        //Wither
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WITHER_TRAVELERS_BACKPACK)
                .define('A', Items.WITHER_SKELETON_SKULL).define('B', Items.SOUL_SAND).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("AAA").pattern("BCB").pattern(" B ")
                .unlockedBy(getHasName(Items.WITHER_SKELETON_SKULL), has(Items.WITHER_SKELETON_SKULL)).save(writer);

        //Warden
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WARDEN_TRAVELERS_BACKPACK)
                .define('A', Items.ECHO_SHARD).define('B', Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("AAA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD)).save(writer);

        //Sleeping Bags
        sleepingBagFromWool(writer, ModItems.BLACK_SLEEPING_BAG, Items.BLACK_WOOL);
        sleepingBagFromWool(writer, ModItems.BLUE_SLEEPING_BAG, Items.BLUE_WOOL);
        sleepingBagFromWool(writer, ModItems.BROWN_SLEEPING_BAG, Items.BROWN_WOOL);
        sleepingBagFromWool(writer, ModItems.CYAN_SLEEPING_BAG, Items.CYAN_WOOL);
        sleepingBagFromWool(writer, ModItems.GRAY_SLEEPING_BAG, Items.GRAY_WOOL);
        sleepingBagFromWool(writer, ModItems.GREEN_SLEEPING_BAG, Items.GREEN_WOOL);
        sleepingBagFromWool(writer, ModItems.LIGHT_BLUE_SLEEPING_BAG, Items.LIGHT_BLUE_WOOL);
        sleepingBagFromWool(writer, ModItems.LIGHT_GRAY_SLEEPING_BAG, Items.LIGHT_GRAY_WOOL);
        sleepingBagFromWool(writer, ModItems.LIME_SLEEPING_BAG, Items.LIME_WOOL);
        sleepingBagFromWool(writer, ModItems.MAGENTA_SLEEPING_BAG, Items.MAGENTA_WOOL);
        sleepingBagFromWool(writer, ModItems.ORANGE_SLEEPING_BAG, Items.ORANGE_WOOL);
        sleepingBagFromWool(writer, ModItems.PINK_SLEEPING_BAG, Items.PINK_WOOL);
        sleepingBagFromWool(writer, ModItems.PURPLE_SLEEPING_BAG, Items.PURPLE_WOOL);
        sleepingBagFromWool(writer, ModItems.RED_SLEEPING_BAG, Items.RED_WOOL);
        sleepingBagFromWool(writer, ModItems.WHITE_SLEEPING_BAG, Items.WHITE_WOOL);
        sleepingBagFromWool(writer, ModItems.YELLOW_SLEEPING_BAG, Items.YELLOW_WOOL);

        List<Item> list = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);
        List<Item> list2 = List.of(ModItems.BLACK_SLEEPING_BAG, ModItems.BLUE_SLEEPING_BAG, ModItems.BROWN_SLEEPING_BAG, ModItems.CYAN_SLEEPING_BAG, ModItems.GRAY_SLEEPING_BAG, ModItems.GREEN_SLEEPING_BAG, ModItems.LIGHT_BLUE_SLEEPING_BAG, ModItems.LIGHT_GRAY_SLEEPING_BAG, ModItems.LIME_SLEEPING_BAG, ModItems.MAGENTA_SLEEPING_BAG, ModItems.ORANGE_SLEEPING_BAG, ModItems.PINK_SLEEPING_BAG, ModItems.PURPLE_SLEEPING_BAG, ModItems.RED_SLEEPING_BAG, ModItems.YELLOW_SLEEPING_BAG, ModItems.WHITE_SLEEPING_BAG);
        colorBlockWithDye(writer, list, list2, "sleeping_bag");
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(TravelersBackpack.MODID, name);
    }

    protected static void sleepingBagFromWool(Consumer<FinishedRecipe> recipeOutput, ItemLike sleepingBag, ItemLike pWool) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sleepingBag).group("sleeping_bag").define('#', pWool).define('X', Items.WHITE_WOOL).pattern("##X").unlockedBy(getHasName(pWool), has(pWool)).save(recipeOutput);
    }

    public static void colorBlockWithDye(Consumer<FinishedRecipe> recipeOutput, List<Item> pDyes, List<Item> pDyeableItems, String pGroup) {
        for(int i = 0; i < pDyes.size(); ++i) {
            Item item = pDyes.get(i);
            Item item1 = pDyeableItems.get(i);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, item1)
                    .requires(item).requires(Ingredient.of(pDyeableItems.stream().filter((p_288265_) -> !p_288265_.equals(item1))
                            .map(ItemStack::new))).group(pGroup).unlockedBy("has_needed_dye", has(item))
                    .save(recipeOutput, id("dye_" + getItemName(item1)));
        }
    }

    public ShapedBackpackRecipeBuilder createBackpackSmallGrid(Item result, Ingredient ingredient, String criterionName, InventoryChangeTrigger.TriggerInstance criterion) {
        return ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', ingredient).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern(" A ").pattern("ABA").pattern(" A ").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createBackpackSmallGrid(Item result, TagKey<Item> tag, String criterionName, InventoryChangeTrigger.TriggerInstance criterion) {
        return ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', tag).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern(" A ").pattern("ABA").pattern(" A ").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createBackpackFullGrid(Item result, Ingredient ingredient, String criterionName, InventoryChangeTrigger.TriggerInstance criterion) {
        return ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', ingredient).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(criterionName, criterion);
    }

    public ShapedRecipeBuilder createFullGrid(Item result, Ingredient ingredient, Ingredient ingredient1, Item unlocker, TagKey<Item> tag) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', ingredient1).define('B', ingredient)
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(getHasName(unlocker), has(tag));
    }

    public static final Item[] BACKPACKS = {
            ModItems.STANDARD_TRAVELERS_BACKPACK,
            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            ModItems.LAPIS_TRAVELERS_BACKPACK,
            ModItems.REDSTONE_TRAVELERS_BACKPACK,
            ModItems.COAL_TRAVELERS_BACKPACK,

            ModItems.QUARTZ_TRAVELERS_BACKPACK,
            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            ModItems.END_TRAVELERS_BACKPACK,
            ModItems.NETHER_TRAVELERS_BACKPACK,
            ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            ModItems.SNOW_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            ModItems.HAY_TRAVELERS_BACKPACK,
            ModItems.MELON_TRAVELERS_BACKPACK,
            ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            ModItems.SKELETON_TRAVELERS_BACKPACK,
            ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,
            ModItems.WARDEN_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
            ModItems.BEE_TRAVELERS_BACKPACK,
            ModItems.WOLF_TRAVELERS_BACKPACK,
            ModItems.FOX_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            ModItems.HORSE_TRAVELERS_BACKPACK,
            ModItems.COW_TRAVELERS_BACKPACK,
            ModItems.PIG_TRAVELERS_BACKPACK,
            ModItems.SHEEP_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK,
            ModItems.SQUID_TRAVELERS_BACKPACK,
            ModItems.VILLAGER_TRAVELERS_BACKPACK,
            ModItems.IRON_GOLEM_TRAVELERS_BACKPACK,
    };
}