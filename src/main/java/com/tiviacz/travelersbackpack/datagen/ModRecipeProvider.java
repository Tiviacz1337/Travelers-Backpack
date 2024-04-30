package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeBuilder;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipeBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider
{
    public ModRecipeProvider(DataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> writer)
    {
        //Smithing
        for(Item item : BACKPACKS)
        {
            BackpackUpgradeRecipeBuilder.upgrade(Ingredient.of(item), Ingredient.of(ModTags.BACKPACK_UPGRADES), item).unlocks(getHasName(item), has(item)).save(writer, id(getItemName(item) + "_smithing"));
        }

        //Upgrades
        ShapedRecipeBuilder.shaped(ModItems.BLANK_UPGRADE.get(), 4)
                .define('A', ModItems.BACKPACK_TANK.get()).define('B', Tags.Items.LEATHER).define('C', Tags.Items.CHESTS_WOODEN)
                .pattern("BBB").pattern("ACA").pattern("BBB")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(writer);

        createFullGrid(ModItems.IRON_TIER_UPGRADE.get(), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                Ingredient.of(Tags.Items.INGOTS_IRON), Items.IRON_INGOT, Tags.Items.INGOTS_IRON).save(writer);
        createFullGrid(ModItems.GOLD_TIER_UPGRADE.get(), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                Ingredient.of(Tags.Items.INGOTS_GOLD), Items.GOLD_INGOT, Tags.Items.INGOTS_GOLD).save(writer);
        createFullGrid(ModItems.DIAMOND_TIER_UPGRADE.get(), Ingredient.of(ModItems.BLANK_UPGRADE.get()),
                Ingredient.of(Tags.Items.GEMS_DIAMOND), Items.DIAMOND, Tags.Items.GEMS_DIAMOND).save(writer);

        UpgradeRecipeBuilder.smithing(Ingredient.of(ModItems.BLANK_UPGRADE.get()), Ingredient.of(Tags.Items.INGOTS_NETHERITE), ModItems.NETHERITE_TIER_UPGRADE.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE))
                .save(writer, id("netherite_tier_upgrade"));

        ShapedRecipeBuilder.shaped(ModItems.CRAFTING_UPGRADE.get()).define('A', Items.CRAFTING_TABLE)
                .define('B', ModItems.BLANK_UPGRADE.get()).pattern("A").pattern("B")
                .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE)).save(writer);

        //All Recipes
        ShapedRecipeBuilder.shaped(ModItems.BACKPACK_TANK.get())
                .define('B', Tags.Items.GLASS_COLORLESS).define('A', Tags.Items.INGOTS_IRON)
                .pattern("BAB").pattern("B B").pattern("BAB")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(writer, id("backpack_tank"));

        ShapedRecipeBuilder.shaped(ModItems.HOSE_NOZZLE.get())
                .define('A', Tags.Items.INGOTS_GOLD).define('B', Tags.Items.INGOTS_IRON)
                .pattern(" A ").pattern("B B")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(writer, id("hose_nozzle"));

        ShapedRecipeBuilder.shaped(ModItems.HOSE.get())
                .define('A', ModItems.HOSE_NOZZLE.get()).define('B', Tags.Items.DYES_GREEN)
                .pattern("ABB").pattern("  B").pattern("  B")
                .unlockedBy(getHasName(ModItems.HOSE_NOZZLE.get()), has(ModItems.HOSE_NOZZLE.get())).save(writer, id("hose"));

        ShapedBackpackRecipeBuilder.shaped(ModItems.STANDARD_TRAVELERS_BACKPACK.get()).group("standard_travelers_backpack")
                .define('X', Tags.Items.LEATHER).define('B', Tags.Items.INGOTS_GOLD).define('C', ModItems.BACKPACK_TANK.get())
                .define('D', Tags.Items.CHESTS_WOODEN).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern("CDC").pattern("XSX")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(writer, id("standard"));

        //Netherite backpack
        UpgradeRecipeBuilder.smithing(Ingredient.of(ModItems.DIAMOND_TRAVELERS_BACKPACK.get()), Ingredient.of(Tags.Items.INGOTS_NETHERITE), ModItems.NETHERITE_TRAVELERS_BACKPACK.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE)).save(writer, id("netherite"));

        createBackpackSmallGrid(ModItems.DIAMOND_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.GEMS_DIAMOND), getHasName(Items.DIAMOND), has(Tags.Items.GEMS_DIAMOND)).save(writer);
        createBackpackSmallGrid(ModItems.GOLD_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.INGOTS_GOLD), getHasName(Items.GOLD_INGOT), has(Tags.Items.INGOTS_GOLD)).save(writer);
        createBackpackSmallGrid(ModItems.EMERALD_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.GEMS_EMERALD), getHasName(Items.EMERALD), has(Tags.Items.GEMS_EMERALD)).save(writer);
        createBackpackSmallGrid(ModItems.IRON_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.INGOTS_IRON), getHasName(Items.IRON_INGOT), has(Tags.Items.INGOTS_IRON)).save(writer);

        createBackpackSmallGrid(ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.ENDER_PEARLS), getHasName(Items.ENDER_PEARL), has(Tags.Items.ENDER_PEARLS)).save(writer);
        createBackpackSmallGrid(ModItems.WOLF_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.BONES), getHasName(Items.BONE), has(Tags.Items.BONES)).save(writer);
        createBackpackSmallGrid(ModItems.FOX_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.SWEET_BERRIES), getHasName(Items.SWEET_BERRIES), has(Items.SWEET_BERRIES)).save(writer);
        createBackpackSmallGrid(ModItems.OCELOT_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.COD), getHasName(Items.COD), has(Items.COD)).save(writer);
        createBackpackSmallGrid(ModItems.SQUID_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.INK_SAC), getHasName(Items.INK_SAC), has(Items.INK_SAC)).save(writer);

        createBackpackFullGrid(ModItems.REDSTONE_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.DUSTS_REDSTONE), getHasName(Items.REDSTONE), has(Tags.Items.DUSTS_REDSTONE)).save(writer);
        createBackpackFullGrid(ModItems.COAL_TRAVELERS_BACKPACK.get(), Ingredient.of(ItemTags.COALS), getHasName(Items.COAL), has(ItemTags.COALS)).save(writer);
        createBackpackFullGrid(ModItems.SPONGE_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.SPONGE), getHasName(Items.SPONGE), has(Items.SPONGE)).save(writer);
        createBackpackFullGrid(ModItems.HAY_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.CROPS_WHEAT), getHasName(Items.WHEAT), has(Tags.Items.CROPS_WHEAT)).save(writer);

        //Bee
        ShapedBackpackRecipeBuilder.shaped(ModItems.BEE_TRAVELERS_BACKPACK.get())
                .define('A', Items.HONEYCOMB).define('B', Items.HONEY_BOTTLE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.HONEYCOMB), has(Items.HONEYCOMB)).unlockedBy(getHasName(Items.HONEY_BOTTLE), has(Items.HONEY_BOTTLE)).save(writer);

        //Blaze
        ShapedBackpackRecipeBuilder.shaped(ModItems.BLAZE_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.RODS_BLAZE).define('B', Items.FIRE_CHARGE)
                .define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.BLAZE_POWDER).define('E', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("DED")
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Tags.Items.RODS_BLAZE)).save(writer);

        //Bookshelf
        ShapedBackpackRecipeBuilder.shaped(ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get())
                .define('A', ItemTags.PLANKS).define('B', Items.BOOK).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("BCB").pattern("AAA")
                .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK)).save(writer);

        //Cactus
        ShapedBackpackRecipeBuilder.shaped(ModItems.CACTUS_TRAVELERS_BACKPACK.get())
                .define('A', Items.CACTUS).define('B', Tags.Items.DYES_GREEN)
                .define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Tags.Items.SAND)
                .pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.CACTUS), has(Items.CACTUS)).save(writer);

        //Cake
        ShapedBackpackRecipeBuilder.shaped(ModItems.CAKE_TRAVELERS_BACKPACK.get())
                .define('A', Items.MILK_BUCKET).define('B', Tags.Items.EGGS).define('C', Items.SUGAR)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Tags.Items.CROPS_WHEAT)
                .pattern("ABA").pattern("CDC").pattern("EEE")
                .unlockedBy(getHasName(Items.EGG), has(Tags.Items.EGGS)).save(writer);

        //Chicken
        ShapedBackpackRecipeBuilder.shaped(ModItems.CHICKEN_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.FEATHERS).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .define('C', Tags.Items.EGGS).pattern(" A ").pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS)).save(writer);

        //Cow
        ShapedBackpackRecipeBuilder.shaped(ModItems.COW_TRAVELERS_BACKPACK.get())
                .define('A', Items.BEEF).define('B', Tags.Items.LEATHER).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.MILK_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("BDB")
                .unlockedBy(getHasName(Items.MILK_BUCKET), has(Items.MILK_BUCKET)).save(writer);

        //Creeper
        ShapedBackpackRecipeBuilder.shaped(ModItems.CREEPER_TRAVELERS_BACKPACK.get())
                .define('A', Items.GUNPOWDER).define('B', Items.CREEPER_HEAD).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .define('D', Items.TNT).pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.GUNPOWDER), has(Items.GUNPOWDER)).save(writer);

        //Dragon
        ShapedBackpackRecipeBuilder.shaped(ModItems.DRAGON_TRAVELERS_BACKPACK.get())
                .define('A', Items.DRAGON_BREATH).define('B', Items.DRAGON_EGG).define('C', Tags.Items.END_STONES)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Tags.Items.ENDER_PEARLS)
                .pattern("ABA").pattern("CDC").pattern("ECE")
                .unlockedBy(getHasName(Items.DRAGON_EGG), has(Items.DRAGON_EGG)).save(writer);

        //End
        ShapedBackpackRecipeBuilder.shaped(ModItems.END_TRAVELERS_BACKPACK.get())
                .define('A', Items.ENDER_EYE).define('B', Tags.Items.END_STONES).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE)).save(writer);

        //Ghast
        ShapedBackpackRecipeBuilder.shaped(ModItems.GHAST_TRAVELERS_BACKPACK.get())
                .define('A', Items.GHAST_TEAR).define('B', Items.FIRE_CHARGE).define('C', Tags.Items.GUNPOWDER)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.GHAST_TEAR), has(Items.GHAST_TEAR)).save(writer);

        //Horse
        ShapedBackpackRecipeBuilder.shaped(ModItems.HORSE_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.LEATHER).define('B', Items.APPLE).define('C', Tags.Items.CROPS_WHEAT).define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER)).save(writer);

        //Lapis
        ShapedBackpackRecipeBuilder.shaped(ModItems.LAPIS_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.STORAGE_BLOCKS_LAPIS).define('B', Tags.Items.GEMS_LAPIS).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Tags.Items.GEMS_LAPIS)).save(writer);

        //Magma Cube
        ShapedBackpackRecipeBuilder.shaped(ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get())
                .define('A', Items.MAGMA_CREAM).define('B', Items.LAVA_BUCKET).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.MAGMA_CREAM), has(Items.MAGMA_CREAM)).save(writer);

        //Melon
        ShapedBackpackRecipeBuilder.shaped(ModItems.MELON_TRAVELERS_BACKPACK.get())
                .define('A', Items.MELON_SLICE).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('C', Items.MELON_SEEDS)
                .pattern("A A").pattern("ABA").pattern("ACA")
                .unlockedBy(getHasName(Items.MELON_SLICE), has(Items.MELON_SLICE)).save(writer);

        //Nether
        ShapedBackpackRecipeBuilder.shaped(ModItems.NETHER_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.GEMS_QUARTZ).define('B', Tags.Items.CROPS_NETHER_WART).define('C', Tags.Items.NETHERRACK)
                .define('D', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Items.BLACKSTONE).define('F', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("CDC").pattern("EFE")
                .unlockedBy(getHasName(Items.NETHER_WART), has(Tags.Items.CROPS_NETHER_WART)).save(writer);

        //Pig
        ShapedBackpackRecipeBuilder.shaped(ModItems.PIG_TRAVELERS_BACKPACK.get())
                .define('A', Items.PORKCHOP).define('B', Tags.Items.CROPS_CARROT).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP)).save(writer);

        //Pumpkin
        ShapedBackpackRecipeBuilder.shaped(ModItems.PUMPKIN_TRAVELERS_BACKPACK.get())
                .define('A', Items.PUMPKIN).define('B', Items.CARVED_PUMPKIN).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Tags.Items.SEEDS_PUMPKIN)
                .pattern("ABA").pattern("ACA").pattern("ADA")
                .unlockedBy(getHasName(Items.PUMPKIN), has(Items.PUMPKIN)).save(writer);

        //Quartz
        ShapedBackpackRecipeBuilder.shaped(ModItems.QUARTZ_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.STORAGE_BLOCKS_QUARTZ).define('B', Tags.Items.GEMS_QUARTZ).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.QUARTZ), has(Tags.Items.GEMS_QUARTZ)).save(writer);

        //Sandstone
        ShapedBackpackRecipeBuilder.shaped(ModItems.SANDSTONE_TRAVELERS_BACKPACK.get())
                .define('A', Items.SANDSTONE).define('B', Items.CHISELED_SANDSTONE).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.SANDSTONE), has(Items.SANDSTONE)).save(writer);

        //Sheep
        ShapedBackpackRecipeBuilder.shaped(ModItems.SHEEP_TRAVELERS_BACKPACK.get())
                .define('A', Items.WHITE_WOOL).define('B', Items.MUTTON).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.WHITE_WOOL), has(Items.WHITE_WOOL)).save(writer);

        //Skeleton
        ShapedBackpackRecipeBuilder.shaped(ModItems.SKELETON_TRAVELERS_BACKPACK.get())
                .define('A', Items.BONE).define('B', Items.ARROW).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.BOW)
                .pattern("ABA").pattern("BCB").pattern("ADA")
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW)).save(writer);

        //Snow
        ShapedBackpackRecipeBuilder.shaped(ModItems.SNOW_TRAVELERS_BACKPACK.get())
                .define('A', Items.ICE).define('B', Items.SNOW_BLOCK).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.SNOWBALL)
                .pattern("AAA").pattern("BCB").pattern("DBD")
                .unlockedBy(getHasName(Items.SNOWBALL), has(Items.SNOWBALL)).save(writer);

        //Spider
        ShapedBackpackRecipeBuilder.shaped(ModItems.SPIDER_TRAVELERS_BACKPACK.get())
                .define('A', Items.SPIDER_EYE).define('B', Items.STRING).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING)).save(writer);

        //Wither
        ShapedBackpackRecipeBuilder.shaped(ModItems.WITHER_TRAVELERS_BACKPACK.get())
                .define('A', Items.WITHER_SKELETON_SKULL).define('B', Items.SOUL_SAND).define('C', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern(" A ").pattern("BCB").pattern(" B ")
                .unlockedBy(getHasName(Items.WITHER_SKELETON_SKULL), has(Items.WITHER_SKELETON_SKULL)).save(writer);

        //Sleeping Bags
        sleepingBagFromWool(writer, ModItems.BLACK_SLEEPING_BAG.get(), Items.BLACK_WOOL);
        sleepingBagFromWool(writer, ModItems.BLUE_SLEEPING_BAG.get(), Items.BLUE_WOOL);
        sleepingBagFromWool(writer, ModItems.BROWN_SLEEPING_BAG.get(), Items.BROWN_WOOL);
        sleepingBagFromWool(writer, ModItems.CYAN_SLEEPING_BAG.get(), Items.CYAN_WOOL);
        sleepingBagFromWool(writer, ModItems.GRAY_SLEEPING_BAG.get(), Items.GRAY_WOOL);
        sleepingBagFromWool(writer, ModItems.GREEN_SLEEPING_BAG.get(), Items.GREEN_WOOL);
        sleepingBagFromWool(writer, ModItems.LIGHT_BLUE_SLEEPING_BAG.get(), Items.LIGHT_BLUE_WOOL);
        sleepingBagFromWool(writer, ModItems.LIGHT_GRAY_SLEEPING_BAG.get(), Items.LIGHT_GRAY_WOOL);
        sleepingBagFromWool(writer, ModItems.LIME_SLEEPING_BAG.get(), Items.LIME_WOOL);
        sleepingBagFromWool(writer, ModItems.MAGENTA_SLEEPING_BAG.get(), Items.MAGENTA_WOOL);
        sleepingBagFromWool(writer, ModItems.ORANGE_SLEEPING_BAG.get(), Items.ORANGE_WOOL);
        sleepingBagFromWool(writer, ModItems.PINK_SLEEPING_BAG.get(), Items.PINK_WOOL);
        sleepingBagFromWool(writer, ModItems.PURPLE_SLEEPING_BAG.get(), Items.PURPLE_WOOL);
        sleepingBagFromWool(writer, ModItems.RED_SLEEPING_BAG.get(), Items.RED_WOOL);
        sleepingBagFromWool(writer, ModItems.WHITE_SLEEPING_BAG.get(), Items.WHITE_WOOL);
        sleepingBagFromWool(writer, ModItems.YELLOW_SLEEPING_BAG.get(), Items.YELLOW_WOOL);

        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.BLACK_SLEEPING_BAG.get(), Items.BLACK_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.BLUE_SLEEPING_BAG.get(), Items.BLUE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.BROWN_SLEEPING_BAG.get(), Items.BROWN_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.CYAN_SLEEPING_BAG.get(), Items.CYAN_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.GRAY_SLEEPING_BAG.get(), Items.GRAY_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.GREEN_SLEEPING_BAG.get(), Items.GREEN_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.LIGHT_BLUE_SLEEPING_BAG.get(), Items.LIGHT_BLUE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.LIGHT_GRAY_SLEEPING_BAG.get(), Items.LIGHT_GRAY_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.LIME_SLEEPING_BAG.get(), Items.LIME_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.MAGENTA_SLEEPING_BAG.get(), Items.MAGENTA_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.ORANGE_SLEEPING_BAG.get(), Items.ORANGE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.PINK_SLEEPING_BAG.get(), Items.PINK_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.PURPLE_SLEEPING_BAG.get(), Items.PURPLE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.RED_SLEEPING_BAG.get(), Items.RED_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.YELLOW_SLEEPING_BAG.get(), Items.YELLOW_DYE);
    }

    public static ResourceLocation id(String name)
    {
        return new ResourceLocation(TravelersBackpack.MODID, name);
    }

    protected static void sleepingBagFromWool(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike sleepingBag, ItemLike pWool)
    {
        ShapedRecipeBuilder.shaped(sleepingBag).group("sleeping_bag").define('#', pWool).define('X', Items.WHITE_WOOL).pattern("##X").unlockedBy(getHasName(pWool), has(pWool)).save(pFinishedRecipeConsumer);
    }

    protected static void sleepingBagFromWhiteSleepingBagAndDye(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pDyedBed, ItemLike pDye)
    {
        ShapelessRecipeBuilder.shapeless(pDyedBed).requires(ModItems.WHITE_SLEEPING_BAG.get()).requires(pDye).group("dyed_sleeping_bag").unlockedBy("has_sleeping_bag", has(ModItems.WHITE_SLEEPING_BAG.get())).save(pFinishedRecipeConsumer, id(getConversionRecipeName(pDyedBed, ModItems.WHITE_SLEEPING_BAG.get())));
    }

    public ShapedBackpackRecipeBuilder createBackpackSmallGrid(Item result, Ingredient ingredient, String criterionName, InventoryChangeTrigger.TriggerInstance criterion) {
        return ShapedBackpackRecipeBuilder.shaped(result)
                .define('A', ingredient).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern(" A ").pattern("ABA").pattern(" A ").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createBackpackFullGrid(Item result, Ingredient ingredient, String criterionName, InventoryChangeTrigger.TriggerInstance criterion) {
        return ShapedBackpackRecipeBuilder.shaped(result)
                .define('A', ingredient).define('B', ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createFullGrid(Item result, Ingredient ingredient, Ingredient ingredient1, Item unlocker, TagKey<Item> tag) {
        return ShapedBackpackRecipeBuilder.shaped(result)
                .define('A', ingredient1).define('B', ingredient)
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(getHasName(unlocker), has(tag));
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
}