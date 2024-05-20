package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeJsonBuilder;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipeJsonBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipesProvider extends FabricRecipeProvider
{
    public ModRecipesProvider(FabricDataGenerator fabricDataGenerator)
    {
        super(fabricDataGenerator);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        //Smithing
        for(Item item : BACKPACKS)
        {
            BackpackUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(item), Ingredient.fromTag(ModTags.BACKPACK_UPGRADES),  item).criterion(hasItem(item), conditionsFromItem(item)).offerTo(exporter, id(getItemPath(item) + "_smithing"));
        }

        //Upgrades
        ShapedRecipeJsonBuilder.create(ModItems.BLANK_UPGRADE, 4)
                .input('A', ModItems.BACKPACK_TANK).input('B', Items.LEATHER).input('C', Items.CHEST) //Missing wooden chests tag
                .pattern("BBB").pattern("ACA").pattern("BBB")
                .criterion("has_chest", conditionsFromItem(Items.CHEST)).offerTo(exporter);

        createFullGrid(ModItems.IRON_TIER_UPGRADE, Ingredient.ofItems(ModItems.BLANK_UPGRADE),
                Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS), Items.IRON_INGOT, ConventionalItemTags.IRON_INGOTS).offerTo(exporter);
        createFullGrid(ModItems.GOLD_TIER_UPGRADE, Ingredient.ofItems(ModItems.BLANK_UPGRADE),
                Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS), Items.GOLD_INGOT, ConventionalItemTags.GOLD_INGOTS).offerTo(exporter);
        createFullGrid(ModItems.DIAMOND_TIER_UPGRADE, Ingredient.ofItems(ModItems.BLANK_UPGRADE),
                Ingredient.fromTag(ConventionalItemTags.DIAMONDS), Items.DIAMOND, ConventionalItemTags.DIAMONDS).offerTo(exporter);

        SmithingRecipeJsonBuilder.create(Ingredient.ofItems(ModItems.BLANK_UPGRADE),
                        Ingredient.fromTag(ConventionalItemTags.NETHERITE_INGOTS),  ModItems.NETHERITE_TIER_UPGRADE)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromTag(ConventionalItemTags.NETHERITE_INGOTS))
                .offerTo(exporter, id("netherite_tier_upgrade"));

        ShapedRecipeJsonBuilder.create(ModItems.CRAFTING_UPGRADE).input('A', Items.CRAFTING_TABLE)
                .input('B', ModItems.BLANK_UPGRADE).pattern("A").pattern("B")
                .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE)).offerTo(exporter);

        //All Recipes
        ShapedRecipeJsonBuilder.create(ModItems.BACKPACK_TANK)
                .input('B', ConventionalItemTags.GLASS_BLOCKS).input('A', ConventionalItemTags.IRON_INGOTS) //Missing colorless glass tag, uses glass tag instead
                .pattern("BAB").pattern("B B").pattern("BAB")
                .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(ModItems.HOSE_NOZZLE)
                .input('A',ConventionalItemTags.GOLD_INGOTS).input('B', ConventionalItemTags.IRON_INGOTS)
                .pattern(" A ").pattern("B B")
                .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(ModItems.HOSE)
                .input('A', ModItems.HOSE_NOZZLE).input('B', ConventionalItemTags.GREEN_DYES)
                .pattern("ABB").pattern("  B").pattern("  B")
                .criterion(hasItem(ModItems.HOSE_NOZZLE), conditionsFromItem(ModItems.HOSE_NOZZLE)).offerTo(exporter);

        ShapedBackpackRecipeJsonBuilder.create(ModItems.STANDARD_TRAVELERS_BACKPACK).group("standard_travelers_backpack")
                .input('X', Items.LEATHER).input('B', ConventionalItemTags.GOLD_INGOTS).input('C', ModItems.BACKPACK_TANK)
                .input('D', Items.CHEST).input('S', ModTags.SLEEPING_BAGS) //Missing wooden chests tag
                .pattern("XBX").pattern("CDC").pattern("XSX")
                .criterion("has_chest", conditionsFromItem(Items.CHEST)).offerTo(exporter);

        //Netherite backpack
        SmithingRecipeJsonBuilder.create(Ingredient.ofItems(ModItems.DIAMOND_TRAVELERS_BACKPACK), Ingredient.fromTag(ConventionalItemTags.NETHERITE_INGOTS),
                         ModItems.NETHERITE_TRAVELERS_BACKPACK)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromTag(ConventionalItemTags.NETHERITE_INGOTS)).offerTo(exporter, id("netherite"));

        createBackpackSmallGrid(ModItems.DIAMOND_TRAVELERS_BACKPACK, Ingredient.fromTag(ConventionalItemTags.DIAMONDS), hasItem(Items.DIAMOND), conditionsFromTag(ConventionalItemTags.DIAMONDS)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.GOLD_TRAVELERS_BACKPACK, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS), hasItem(Items.GOLD_INGOT), conditionsFromTag(ConventionalItemTags.GOLD_INGOTS)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.EMERALD_TRAVELERS_BACKPACK, Ingredient.fromTag(ConventionalItemTags.EMERALDS), hasItem(Items.EMERALD), conditionsFromTag(ConventionalItemTags.EMERALDS)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.IRON_TRAVELERS_BACKPACK, Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS), hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS)).offerTo(exporter);

        createBackpackSmallGrid(ModItems.ENDERMAN_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.ENDER_PEARL), hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.WOLF_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.BONE), hasItem(Items.BONE), conditionsFromItem(Items.BONE)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.FOX_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.SWEET_BERRIES), hasItem(Items.SWEET_BERRIES), conditionsFromItem(Items.SWEET_BERRIES)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.OCELOT_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.COD), hasItem(Items.COD), conditionsFromItem(Items.COD)).offerTo(exporter);
        createBackpackSmallGrid(ModItems.SQUID_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.INK_SAC), hasItem(Items.INK_SAC), conditionsFromItem(Items.INK_SAC)).offerTo(exporter);

        createBackpackFullGrid(ModItems.REDSTONE_TRAVELERS_BACKPACK, Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), hasItem(Items.REDSTONE), conditionsFromTag(ConventionalItemTags.REDSTONE_DUSTS)).offerTo(exporter);
        createBackpackFullGrid(ModItems.COAL_TRAVELERS_BACKPACK, Ingredient.fromTag(ItemTags.COALS), hasItem(Items.COAL), conditionsFromTag(ItemTags.COALS)).offerTo(exporter);
        createBackpackFullGrid(ModItems.SPONGE_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.SPONGE), hasItem(Items.SPONGE), conditionsFromItem(Items.SPONGE)).offerTo(exporter);
        createBackpackFullGrid(ModItems.HAY_TRAVELERS_BACKPACK, Ingredient.ofItems(Items.WHEAT), hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT)).offerTo(exporter);

        //Bee
        ShapedBackpackRecipeJsonBuilder.create(ModItems.BEE_TRAVELERS_BACKPACK)
                .input('A', Items.HONEYCOMB).input('B', Items.HONEY_BOTTLE).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .criterion(hasItem(Items.HONEYCOMB), conditionsFromItem(Items.HONEYCOMB)).criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE)).offerTo(exporter);

        //Blaze
        ShapedBackpackRecipeJsonBuilder.create(ModItems.BLAZE_TRAVELERS_BACKPACK)
                .input('A', Items.BLAZE_ROD).input('B', Items.FIRE_CHARGE)
                .input('C', ModItems.STANDARD_TRAVELERS_BACKPACK).input('D', Items.BLAZE_POWDER).input('E', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("DED")
                .criterion(hasItem(Items.BLAZE_ROD), conditionsFromItem(Items.BLAZE_ROD)).offerTo(exporter);

        //Bookshelf
        ShapedBackpackRecipeJsonBuilder.create(ModItems.BOOKSHELF_TRAVELERS_BACKPACK)
                .input('A', ItemTags.PLANKS).input('B', Items.BOOK).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("AAA").pattern("BCB").pattern("AAA")
                .criterion(hasItem(Items.BOOK), conditionsFromItem(Items.BOOK)).offerTo(exporter);

        //Cactus
        ShapedBackpackRecipeJsonBuilder.create(ModItems.CACTUS_TRAVELERS_BACKPACK)
                .input('A', Items.CACTUS).input('B', ConventionalItemTags.GREEN_DYES)
                .input('C', ModItems.STANDARD_TRAVELERS_BACKPACK).input('D', Items.SAND)
                .pattern("ABA").pattern("ACA").pattern("DDD")
                .criterion(hasItem(Items.CACTUS), conditionsFromItem(Items.CACTUS)).offerTo(exporter);

        //Cake
        ShapedBackpackRecipeJsonBuilder.create(ModItems.CAKE_TRAVELERS_BACKPACK)
                .input('A', Items.MILK_BUCKET).input('B', Items.EGG).input('C', Items.SUGAR)
                .input('D', ModItems.STANDARD_TRAVELERS_BACKPACK).input('E', Items.WHEAT)
                .pattern("ABA").pattern("CDC").pattern("EEE")
                .criterion(hasItem(Items.EGG), conditionsFromItem(Items.EGG)).offerTo(exporter);

        //Chicken
        ShapedBackpackRecipeJsonBuilder.create(ModItems.CHICKEN_TRAVELERS_BACKPACK)
                .input('A', Items.FEATHER).input('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .input('C', Items.EGG).pattern(" A ").pattern("ABA").pattern("CCC")
                .criterion(hasItem(Items.FEATHER), conditionsFromItem(Items.FEATHER)).offerTo(exporter);

        //Cow
        ShapedBackpackRecipeJsonBuilder.create(ModItems.COW_TRAVELERS_BACKPACK)
                .input('A', Items.BEEF).input('B', Items.LEATHER).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK).input('D', Items.MILK_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("BDB")
                .criterion(hasItem(Items.MILK_BUCKET), conditionsFromItem(Items.MILK_BUCKET)).offerTo(exporter);

        //Creeper
        ShapedBackpackRecipeJsonBuilder.create(ModItems.CREEPER_TRAVELERS_BACKPACK)
                .input('A', Items.GUNPOWDER).input('B', Items.CREEPER_HEAD).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .input('D', Items.TNT).pattern("ABA").pattern("ACA").pattern("DDD")
                .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER)).offerTo(exporter);

        //Dragon
        ShapedBackpackRecipeJsonBuilder.create(ModItems.DRAGON_TRAVELERS_BACKPACK)
                .input('A', Items.DRAGON_BREATH).input('B', Items.DRAGON_EGG).input('C', Items.END_STONE)
                .input('D', ModItems.STANDARD_TRAVELERS_BACKPACK).input('E', Items.ENDER_PEARL)
                .pattern("ABA").pattern("CDC").pattern("ECE")
                .criterion(hasItem(Items.DRAGON_EGG), conditionsFromItem(Items.DRAGON_EGG)).offerTo(exporter);

        //End
        ShapedBackpackRecipeJsonBuilder.create(ModItems.END_TRAVELERS_BACKPACK)
                .input('A', Items.ENDER_EYE).input('B', Items.END_STONE).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE)).offerTo(exporter);

        //Ghast
        ShapedBackpackRecipeJsonBuilder.create(ModItems.GHAST_TRAVELERS_BACKPACK)
                .input('A', Items.GHAST_TEAR).input('B', Items.FIRE_CHARGE).input('C', Items.GUNPOWDER)
                .input('D', ModItems.STANDARD_TRAVELERS_BACKPACK).pattern("ABA").pattern("CDC").pattern("ACA")
                .criterion(hasItem(Items.GHAST_TEAR), conditionsFromItem(Items.GHAST_TEAR)).offerTo(exporter);

        //Horse
        ShapedBackpackRecipeJsonBuilder.create(ModItems.HORSE_TRAVELERS_BACKPACK)
                .input('A', Items.LEATHER).input('B', Items.APPLE).input('C', Items.WHEAT).input('D', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("CDC").pattern("ACA")
                .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER)).offerTo(exporter);

        //Lapis
        ShapedBackpackRecipeJsonBuilder.create(ModItems.LAPIS_TRAVELERS_BACKPACK)
                .input('A', Items.LAPIS_BLOCK).input('B', ConventionalItemTags.LAPIS).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .criterion(hasItem(Items.LAPIS_LAZULI), conditionsFromTag(ConventionalItemTags.LAPIS)).offerTo(exporter);

        //Magma Cube
        ShapedBackpackRecipeJsonBuilder.create(ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK)
                .input('A', Items.MAGMA_CREAM).input('B', Items.LAVA_BUCKET).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("ACA").pattern("ABA")
                .criterion(hasItem(Items.MAGMA_CREAM), conditionsFromItem(Items.MAGMA_CREAM)).offerTo(exporter);

        //Melon
        ShapedBackpackRecipeJsonBuilder.create(ModItems.MELON_TRAVELERS_BACKPACK)
                .input('A', Items.MELON_SLICE).input('B', ModItems.STANDARD_TRAVELERS_BACKPACK).input('C', Items.MELON_SEEDS)
                .pattern("A A").pattern("ABA").pattern("ACA")
                .criterion(hasItem(Items.MELON_SLICE), conditionsFromItem(Items.MELON_SLICE)).offerTo(exporter);

        //Nether
        ShapedBackpackRecipeJsonBuilder.create(ModItems.NETHER_TRAVELERS_BACKPACK)
                .input('A', ConventionalItemTags.QUARTZ).input('B', Items.NETHER_WART).input('C', Items.NETHERRACK)
                .input('D', ModItems.STANDARD_TRAVELERS_BACKPACK).input('E', Items.BLACKSTONE).input('F', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("CDC").pattern("EFE")
                .criterion(hasItem(Items.NETHER_WART), conditionsFromItem(Items.NETHER_WART)).offerTo(exporter);

        //Pig
        ShapedBackpackRecipeJsonBuilder.create(ModItems.PIG_TRAVELERS_BACKPACK)
                .input('A', Items.PORKCHOP).input('B', Items.CARROT).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .criterion(hasItem(Items.PORKCHOP), conditionsFromItem(Items.PORKCHOP)).offerTo(exporter);

        //Pumpkin
        ShapedBackpackRecipeJsonBuilder.create(ModItems.PUMPKIN_TRAVELERS_BACKPACK)
                .input('A', Items.PUMPKIN).input('B', Items.CARVED_PUMPKIN).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK).input('D', Items.PUMPKIN_SEEDS)
                .pattern("ABA").pattern("ACA").pattern("ADA")
                .criterion(hasItem(Items.PUMPKIN), conditionsFromItem(Items.PUMPKIN)).offerTo(exporter);

        //Quartz
        ShapedBackpackRecipeJsonBuilder.create(ModItems.QUARTZ_TRAVELERS_BACKPACK)
                .input('A', Items.QUARTZ_BLOCK).input('B', ConventionalItemTags.QUARTZ).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .criterion(hasItem(Items.QUARTZ), conditionsFromTag(ConventionalItemTags.QUARTZ)).offerTo(exporter);

        //Sandstone
        ShapedBackpackRecipeJsonBuilder.create(ModItems.SANDSTONE_TRAVELERS_BACKPACK)
                .input('A', Items.SANDSTONE).input('B', Items.CHISELED_SANDSTONE).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .criterion(hasItem(Items.SANDSTONE), conditionsFromItem(Items.SANDSTONE)).offerTo(exporter);

        //Sheep
        ShapedBackpackRecipeJsonBuilder.create(ModItems.SHEEP_TRAVELERS_BACKPACK)
                .input('A', Items.WHITE_WOOL).input('B', Items.MUTTON).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .criterion(hasItem(Items.WHITE_WOOL), conditionsFromItem(Items.WHITE_WOOL)).offerTo(exporter);

        //Skeleton
        ShapedBackpackRecipeJsonBuilder.create(ModItems.SKELETON_TRAVELERS_BACKPACK)
                .input('A', Items.BONE).input('B', Items.ARROW).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK).input('D', Items.BOW)
                .pattern("ABA").pattern("BCB").pattern("ADA")
                .criterion(hasItem(Items.ARROW), conditionsFromItem(Items.ARROW)).offerTo(exporter);

        //Snow
        ShapedBackpackRecipeJsonBuilder.create(ModItems.SNOW_TRAVELERS_BACKPACK)
                .input('A', Items.ICE).input('B', Items.SNOW_BLOCK).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK).input('D', Items.SNOWBALL)
                .pattern("AAA").pattern("BCB").pattern("DBD")
                .criterion(hasItem(Items.SNOWBALL), conditionsFromItem(Items.SNOWBALL)).offerTo(exporter);

        //Spider
        ShapedBackpackRecipeJsonBuilder.create(ModItems.SPIDER_TRAVELERS_BACKPACK)
                .input('A', Items.SPIDER_EYE).input('B', Items.STRING).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING)).offerTo(exporter);

        //Wither
        ShapedBackpackRecipeJsonBuilder.create(ModItems.WITHER_TRAVELERS_BACKPACK)
                .input('A', Items.WITHER_SKELETON_SKULL).input('B', Items.SOUL_SAND).input('C', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern(" A ").pattern("BCB").pattern(" B ")
                .criterion(hasItem(Items.WITHER_SKELETON_SKULL), conditionsFromItem(Items.WITHER_SKELETON_SKULL)).offerTo(exporter);

        //Sleeping Bags
        offerSleepingBagRecipe(exporter, ModItems.BLACK_SLEEPING_BAG, Items.BLACK_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.BLUE_SLEEPING_BAG, Items.BLUE_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.BROWN_SLEEPING_BAG, Items.BROWN_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.CYAN_SLEEPING_BAG, Items.CYAN_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.GRAY_SLEEPING_BAG, Items.GRAY_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.GREEN_SLEEPING_BAG, Items.GREEN_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.LIGHT_BLUE_SLEEPING_BAG, Items.LIGHT_BLUE_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.LIGHT_GRAY_SLEEPING_BAG, Items.LIGHT_GRAY_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.LIME_SLEEPING_BAG, Items.LIME_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.MAGENTA_SLEEPING_BAG, Items.MAGENTA_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.ORANGE_SLEEPING_BAG, Items.ORANGE_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.PINK_SLEEPING_BAG, Items.PINK_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.PURPLE_SLEEPING_BAG, Items.PURPLE_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.RED_SLEEPING_BAG, Items.RED_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.WHITE_SLEEPING_BAG, Items.WHITE_WOOL);
        offerSleepingBagRecipe(exporter, ModItems.YELLOW_SLEEPING_BAG, Items.YELLOW_WOOL);

        offerSleepingBagDyeingRecipe(exporter, ModItems.BLACK_SLEEPING_BAG, Items.BLACK_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.BLUE_SLEEPING_BAG, Items.BLUE_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.BROWN_SLEEPING_BAG, Items.BROWN_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.CYAN_SLEEPING_BAG, Items.CYAN_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.GRAY_SLEEPING_BAG, Items.GRAY_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.GREEN_SLEEPING_BAG, Items.GREEN_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.LIGHT_BLUE_SLEEPING_BAG, Items.LIGHT_BLUE_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.LIGHT_GRAY_SLEEPING_BAG, Items.LIGHT_GRAY_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.LIME_SLEEPING_BAG, Items.LIME_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.MAGENTA_SLEEPING_BAG, Items.MAGENTA_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.ORANGE_SLEEPING_BAG, Items.ORANGE_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.PINK_SLEEPING_BAG, Items.PINK_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.PURPLE_SLEEPING_BAG, Items.PURPLE_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.RED_SLEEPING_BAG, Items.RED_DYE);
        offerSleepingBagDyeingRecipe(exporter, ModItems.YELLOW_SLEEPING_BAG, Items.YELLOW_DYE);
    }

    public static Identifier id(String name)
    {
        return new Identifier(TravelersBackpack.MODID, name);
    }

    public static void offerSleepingBagRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input)
    {
        ShapedRecipeJsonBuilder.create(output).input('#', input).input('X', Items.WHITE_WOOL).pattern("##X").group("sleeping_bag").criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerSleepingBagDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input)
    {
        ShapelessRecipeJsonBuilder.create(output).input(ModItems.WHITE_SLEEPING_BAG).input(input).group("dyed_sleeping_bag").criterion("has_sleeping_bag", RecipeProvider.conditionsFromItem(ModItems.WHITE_SLEEPING_BAG)).offerTo(exporter, RecipeProvider.convertBetween(output, ModItems.WHITE_SLEEPING_BAG));
    }

    public ShapedBackpackRecipeJsonBuilder createBackpackSmallGrid(Item result, Ingredient ingredient, String criterionName, CriterionConditions criterion)
    {
        return ShapedBackpackRecipeJsonBuilder.create(result)
                .input('A', ingredient).input('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern(" A ").pattern("ABA").pattern(" A ").criterion(criterionName, criterion);
    }

    public ShapedBackpackRecipeJsonBuilder createBackpackFullGrid(Item result, Ingredient ingredient, String criterionName, CriterionConditions criterion)
    {
        return ShapedBackpackRecipeJsonBuilder.create(result)
                .input('A', ingredient).input('B', ModItems.STANDARD_TRAVELERS_BACKPACK)
                .pattern("AAA").pattern("ABA").pattern("AAA").criterion(criterionName, criterion);
    }

    public ShapedBackpackRecipeJsonBuilder createFullGrid(Item result, Ingredient ingredient, Ingredient ingredient1, Item unlocker, TagKey<Item> tag)
    {
        return ShapedBackpackRecipeJsonBuilder.create(result)
                .input('A', ingredient1).input('B', ingredient)
                .pattern("AAA").pattern("ABA").pattern("AAA").criterion(hasItem(unlocker), conditionsFromTag(tag));
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