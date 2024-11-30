package com.tiviacz.travelersbackpackneo.datagen;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeBuilder;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipeBuilder;
import com.tiviacz.travelersbackpackneo.initold.ModItemsNeo;
import com.tiviacz.travelersbackpackneo.initold.ModTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> holderProvider) {
        super(output, holderProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput writer) {
        //Smithing
        for(Item item : BACKPACKS) {
            BackpackUpgradeRecipeBuilder.backpackUpgrade(Ingredient.EMPTY, Ingredient.of(item), Ingredient.of(ModTags.BACKPACK_UPGRADES), RecipeCategory.MISC, item).unlocks(getHasName(item), has(item)).save(writer, id(getItemName(item) + "_smithing"));
        }

        //Upgrades
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.BLANK_UPGRADE.get(), 4)
                .define('A', Tags.Items.LEATHERS).define('B', Tags.Items.STRINGS)
                .pattern(" B ").pattern("BAB").pattern(" B ")
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS)).save(writer);

        createFullGrid(ModItemsNeo.IRON_TIER_UPGRADE.get(), Ingredient.of(ModItemsNeo.BLANK_UPGRADE.get()),
                Ingredient.of(Tags.Items.INGOTS_IRON), Items.IRON_INGOT, Tags.Items.INGOTS_IRON).save(writer);
        createFullGrid(ModItemsNeo.GOLD_TIER_UPGRADE.get(), Ingredient.of(ModItemsNeo.BLANK_UPGRADE.get()),
                Ingredient.of(Tags.Items.INGOTS_GOLD), Items.GOLD_INGOT, Tags.Items.INGOTS_GOLD).save(writer);
        createFullGrid(ModItemsNeo.DIAMOND_TIER_UPGRADE.get(), Ingredient.of(ModItemsNeo.BLANK_UPGRADE.get()),
                Ingredient.of(Tags.Items.GEMS_DIAMOND), Items.DIAMOND, Tags.Items.GEMS_DIAMOND).save(writer);

        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItemsNeo.BLANK_UPGRADE.get()),
                        Ingredient.of(Tags.Items.INGOTS_NETHERITE), RecipeCategory.MISC, ModItemsNeo.NETHERITE_TIER_UPGRADE.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE))
                .save(writer, id("netherite_tier_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.TANKS_UPGRADE.get()).define('A', ModItemsNeo.BACKPACK_TANK)
                .define('B', ModItemsNeo.BLANK_UPGRADE.get()).pattern("ABA")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("tanks_upgrade"));
        //.unlockedBy(getHasName(ModItems.), has(ModItems.BACKPACK_TANK)).save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.CRAFTING_UPGRADE.get()).define('A', Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
                .define('B', ModItemsNeo.BLANK_UPGRADE.get()).define('C', Tags.Items.CHESTS_WOODEN).pattern("A").pattern("B").pattern("C")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("crafting_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.FEEDING_UPGRADE.get()).define('A', Items.GOLDEN_CARROT)
                .define('B', ModItemsNeo.BLANK_UPGRADE.get()).define('C', Items.GOLDEN_APPLE).define('D', Tags.Items.DUSTS_REDSTONE).pattern("ABC").pattern("DDD")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("feeding_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.PICKUP_UPGRADE.get()).define('A', Items.HOPPER)
                .define('B', ModItemsNeo.BLANK_UPGRADE.get()).define('C', Tags.Items.DUSTS_REDSTONE).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("pickup_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.JUKEBOX_UPGRADE.get()).define('A', Tags.Items.DUSTS_REDSTONE)
                .define('B', ModItemsNeo.BLANK_UPGRADE.get()).define('C', Items.JUKEBOX).pattern(" B ").pattern("ACA")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("jukebox_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.MAGNET_UPGRADE.get()).define('A', Tags.Items.ENDER_PEARLS)
                .define('B', Tags.Items.DUSTS_REDSTONE).define('C', ModItemsNeo.BLANK_UPGRADE)
                .define('D', Tags.Items.GEMS_LAPIS).define('E', Tags.Items.INGOTS_IRON).pattern("A A").pattern("BCD").pattern("BED")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("magnet_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.VOID_UPGRADE.get()).define('A', Items.LAVA_BUCKET)
                .define('B', ModItemsNeo.BLANK_UPGRADE.get()).define('C', Tags.Items.DUSTS_REDSTONE).pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(ModItemsNeo.BLANK_UPGRADE), has(ModItemsNeo.BLANK_UPGRADE)).save(writer, id("void_upgrade"));

        //All Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.BACKPACK_TANK.get())
                .define('B', Tags.Items.GLASS_BLOCKS_COLORLESS).define('A', Tags.Items.INGOTS_IRON)
                .pattern("BAB").pattern("B B").pattern("BAB")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(writer, id("backpack_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.HOSE_NOZZLE.get())
                .define('A', Tags.Items.INGOTS_GOLD).define('B', Tags.Items.INGOTS_IRON)
                .pattern(" A ").pattern("B B")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(writer, id("hose_nozzle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItemsNeo.HOSE.get())
                .define('A', ModItemsNeo.HOSE_NOZZLE.get()).define('B', Tags.Items.DYES_GREEN)
                .pattern("ABB").pattern("  B").pattern("  B")
                .unlockedBy(getHasName(ModItemsNeo.HOSE_NOZZLE.get()), has(ModItemsNeo.HOSE_NOZZLE.get())).save(writer, id("hose"));

        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).group("standard_travelers_backpack")
                .define('X', Tags.Items.LEATHERS).define('B', Tags.Items.STRINGS).define('C', ModItemsNeo.BACKPACK_TANK.get())
                .define('D', Tags.Items.CHESTS_WOODEN).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern("CDC").pattern("XSX")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(writer, id("standard"));

        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).group("standard_travelers_backpack")
                .define('X', Tags.Items.LEATHERS).define('B', Tags.Items.STRINGS)
                .define('D', Tags.Items.CHESTS_WOODEN).define('S', ModTags.SLEEPING_BAGS)
                .pattern("XBX").pattern(" D ").pattern("XSX")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(writer, id("standard_no_tanks"));

        //Netherite backpack
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItemsNeo.DIAMOND_TRAVELERS_BACKPACK.get()), Ingredient.of(Tags.Items.INGOTS_NETHERITE),
                        RecipeCategory.MISC, ModItemsNeo.NETHERITE_TRAVELERS_BACKPACK.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE)).save(writer, id("netherite"));

        createBackpackSmallGrid(ModItemsNeo.DIAMOND_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.GEMS_DIAMOND), getHasName(Items.DIAMOND), has(Tags.Items.GEMS_DIAMOND)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.GOLD_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.INGOTS_GOLD), getHasName(Items.GOLD_INGOT), has(Tags.Items.INGOTS_GOLD)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.EMERALD_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.GEMS_EMERALD), getHasName(Items.EMERALD), has(Tags.Items.GEMS_EMERALD)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.IRON_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.INGOTS_IRON), getHasName(Items.IRON_INGOT), has(Tags.Items.INGOTS_IRON)).save(writer);

        createBackpackSmallGrid(ModItemsNeo.ENDERMAN_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.ENDER_PEARLS), getHasName(Items.ENDER_PEARL), has(Tags.Items.ENDER_PEARLS)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.WOLF_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.BONES), getHasName(Items.BONE), has(Tags.Items.BONES)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.FOX_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.SWEET_BERRIES), getHasName(Items.SWEET_BERRIES), has(Items.SWEET_BERRIES)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.OCELOT_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.COD), getHasName(Items.COD), has(Items.COD)).save(writer);
        createBackpackSmallGrid(ModItemsNeo.SQUID_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.INK_SAC), getHasName(Items.INK_SAC), has(Items.INK_SAC)).save(writer);

        createBackpackFullGrid(ModItemsNeo.REDSTONE_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.DUSTS_REDSTONE), getHasName(Items.REDSTONE), has(Tags.Items.DUSTS_REDSTONE)).save(writer);
        createBackpackFullGrid(ModItemsNeo.COAL_TRAVELERS_BACKPACK.get(), Ingredient.of(ItemTags.COALS), getHasName(Items.COAL), has(ItemTags.COALS)).save(writer);
        createBackpackFullGrid(ModItemsNeo.SPONGE_TRAVELERS_BACKPACK.get(), Ingredient.of(Items.SPONGE), getHasName(Items.SPONGE), has(Items.SPONGE)).save(writer);
        createBackpackFullGrid(ModItemsNeo.HAY_TRAVELERS_BACKPACK.get(), Ingredient.of(Tags.Items.CROPS_WHEAT), getHasName(Items.WHEAT), has(Tags.Items.CROPS_WHEAT)).save(writer);

        //Bee
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.BEE_TRAVELERS_BACKPACK.get())
                .define('A', Items.HONEYCOMB).define('B', Items.HONEY_BOTTLE).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.HONEYCOMB), has(Items.HONEYCOMB)).unlockedBy(getHasName(Items.HONEY_BOTTLE), has(Items.HONEY_BOTTLE)).save(writer);

        //Blaze
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.BLAZE_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.RODS_BLAZE).define('B', Items.FIRE_CHARGE)
                .define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.BLAZE_POWDER).define('E', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("DED")
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Tags.Items.RODS_BLAZE)).save(writer);

        //Bookshelf
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.BOOKSHELF_TRAVELERS_BACKPACK.get())
                .define('A', ItemTags.PLANKS).define('B', Items.BOOK).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("BCB").pattern("AAA")
                .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK)).save(writer);

        //Cactus
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.CACTUS_TRAVELERS_BACKPACK.get())
                .define('A', Items.CACTUS).define('B', Tags.Items.DYES_GREEN)
                .define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Tags.Items.SANDS)
                .pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.CACTUS), has(Items.CACTUS)).save(writer);

        //Cake
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.CAKE_TRAVELERS_BACKPACK.get())
                .define('A', Items.MILK_BUCKET).define('B', Tags.Items.EGGS).define('C', Items.SUGAR)
                .define('D', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Tags.Items.CROPS_WHEAT)
                .pattern("ABA").pattern("CDC").pattern("EEE")
                .unlockedBy(getHasName(Items.EGG), has(Tags.Items.EGGS)).save(writer);

        //Chicken
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.CHICKEN_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.FEATHERS).define('B', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .define('C', Tags.Items.EGGS).pattern(" A ").pattern("ABA").pattern("CCC")
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS)).save(writer);

        //Cow
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.COW_TRAVELERS_BACKPACK.get())
                .define('A', Items.BEEF).define('B', Tags.Items.LEATHERS).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.MILK_BUCKET)
                .pattern("ABA").pattern("ACA").pattern("BDB")
                .unlockedBy(getHasName(Items.MILK_BUCKET), has(Items.MILK_BUCKET)).save(writer);

        //Creeper
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.CREEPER_TRAVELERS_BACKPACK.get())
                .define('A', Items.GUNPOWDER).define('B', Items.CREEPER_HEAD).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .define('D', Items.TNT).pattern("ABA").pattern("ACA").pattern("DDD")
                .unlockedBy(getHasName(Items.GUNPOWDER), has(Items.GUNPOWDER)).save(writer);

        //Dragon
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.DRAGON_TRAVELERS_BACKPACK.get())
                .define('A', Items.DRAGON_BREATH).define('B', Items.DRAGON_EGG).define('C', Tags.Items.END_STONES)
                .define('D', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Tags.Items.ENDER_PEARLS)
                .pattern("ABA").pattern("CDC").pattern("ECE")
                .unlockedBy(getHasName(Items.DRAGON_EGG), has(Items.DRAGON_EGG)).save(writer);

        //End
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.END_TRAVELERS_BACKPACK.get())
                .define('A', Items.ENDER_EYE).define('B', Tags.Items.END_STONES).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE)).save(writer);

        //Ghast
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.GHAST_TRAVELERS_BACKPACK.get())
                .define('A', Items.GHAST_TEAR).define('B', Items.FIRE_CHARGE).define('C', Tags.Items.GUNPOWDERS)
                .define('D', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.GHAST_TEAR), has(Items.GHAST_TEAR)).save(writer);

        //Horse
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.HORSE_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.LEATHERS).define('B', Items.APPLE).define('C', Tags.Items.CROPS_WHEAT).define('D', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("CDC").pattern("ACA")
                .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER)).save(writer);

        //Lapis
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.LAPIS_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.STORAGE_BLOCKS_LAPIS).define('B', Tags.Items.GEMS_LAPIS).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Tags.Items.GEMS_LAPIS)).save(writer);

        //Magma Cube
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.MAGMA_CUBE_TRAVELERS_BACKPACK.get())
                .define('A', Items.MAGMA_CREAM).define('B', Items.LAVA_BUCKET).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.MAGMA_CREAM), has(Items.MAGMA_CREAM)).save(writer);

        //Melon
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.MELON_TRAVELERS_BACKPACK.get())
                .define('A', Items.MELON_SLICE).define('B', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('C', Items.MELON_SEEDS)
                .pattern("A A").pattern("ABA").pattern("ACA")
                .unlockedBy(getHasName(Items.MELON_SLICE), has(Items.MELON_SLICE)).save(writer);

        //Nether
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.NETHER_TRAVELERS_BACKPACK.get())
                .define('A', Tags.Items.GEMS_QUARTZ).define('B', Tags.Items.CROPS_NETHER_WART).define('C', Tags.Items.NETHERRACKS)
                .define('D', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('E', Items.BLACKSTONE).define('F', Items.LAVA_BUCKET)
                .pattern("ABA").pattern("CDC").pattern("EFE")
                .unlockedBy(getHasName(Items.NETHER_WART), has(Tags.Items.CROPS_NETHER_WART)).save(writer);

        //Pig
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.PIG_TRAVELERS_BACKPACK.get())
                .define('A', Items.PORKCHOP).define('B', Tags.Items.CROPS_CARROT).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP)).save(writer);

        //Pumpkin
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.PUMPKIN_TRAVELERS_BACKPACK.get())
                .define('A', Items.PUMPKIN).define('B', Items.CARVED_PUMPKIN).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Tags.Items.SEEDS_PUMPKIN)
                .pattern("ABA").pattern("ACA").pattern("ADA")
                .unlockedBy(getHasName(Items.PUMPKIN), has(Items.PUMPKIN)).save(writer);

        //Quartz
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.QUARTZ_TRAVELERS_BACKPACK.get())
                .define('A', Items.QUARTZ_BLOCK).define('B', Tags.Items.GEMS_QUARTZ).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.QUARTZ), has(Tags.Items.GEMS_QUARTZ)).save(writer);

        //Sandstone
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.SANDSTONE_TRAVELERS_BACKPACK.get())
                .define('A', Items.SANDSTONE).define('B', Items.CHISELED_SANDSTONE).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("ACA").pattern("AAA")
                .unlockedBy(getHasName(Items.SANDSTONE), has(Items.SANDSTONE)).save(writer);

        //Sheep
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.SHEEP_TRAVELERS_BACKPACK.get())
                .define('A', Items.WHITE_WOOL).define('B', Items.MUTTON).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.WHITE_WOOL), has(Items.WHITE_WOOL)).save(writer);

        //Skeleton
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.SKELETON_TRAVELERS_BACKPACK.get())
                .define('A', Items.BONE).define('B', Items.ARROW).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.BOW)
                .pattern("ABA").pattern("BCB").pattern("ADA")
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW)).save(writer);

        //Snow
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.SNOW_TRAVELERS_BACKPACK.get())
                .define('A', Items.ICE).define('B', Items.SNOW_BLOCK).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get()).define('D', Items.SNOWBALL)
                .pattern("AAA").pattern("BCB").pattern("DBD")
                .unlockedBy(getHasName(Items.SNOWBALL), has(Items.SNOWBALL)).save(writer);

        //Spider
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.SPIDER_TRAVELERS_BACKPACK.get())
                .define('A', Items.SPIDER_EYE).define('B', Items.STRING).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("ABA").pattern("BCB").pattern("ABA")
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING)).save(writer);

        //Wither
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.WITHER_TRAVELERS_BACKPACK.get())
                .define('A', Items.WITHER_SKELETON_SKULL).define('B', Items.SOUL_SAND).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern(" A ").pattern("BCB").pattern(" B ")
                .unlockedBy(getHasName(Items.WITHER_SKELETON_SKULL), has(Items.WITHER_SKELETON_SKULL)).save(writer);

        //Warden
        ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, ModItemsNeo.WARDEN_TRAVELERS_BACKPACK.get())
                .define('A', Items.ECHO_SHARD).define('B', Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).define('C', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("ACA").pattern("ABA")
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD)).save(writer);

        //Sleeping Bags
        sleepingBagFromWool(writer, ModItemsNeo.BLACK_SLEEPING_BAG.get(), Items.BLACK_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.BLUE_SLEEPING_BAG.get(), Items.BLUE_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.BROWN_SLEEPING_BAG.get(), Items.BROWN_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.CYAN_SLEEPING_BAG.get(), Items.CYAN_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.GRAY_SLEEPING_BAG.get(), Items.GRAY_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.GREEN_SLEEPING_BAG.get(), Items.GREEN_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.LIGHT_BLUE_SLEEPING_BAG.get(), Items.LIGHT_BLUE_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.LIGHT_GRAY_SLEEPING_BAG.get(), Items.LIGHT_GRAY_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.LIME_SLEEPING_BAG.get(), Items.LIME_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.MAGENTA_SLEEPING_BAG.get(), Items.MAGENTA_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.ORANGE_SLEEPING_BAG.get(), Items.ORANGE_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.PINK_SLEEPING_BAG.get(), Items.PINK_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.PURPLE_SLEEPING_BAG.get(), Items.PURPLE_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.RED_SLEEPING_BAG.get(), Items.RED_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.WHITE_SLEEPING_BAG.get(), Items.WHITE_WOOL);
        sleepingBagFromWool(writer, ModItemsNeo.YELLOW_SLEEPING_BAG.get(), Items.YELLOW_WOOL);

        List<Item> list = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);
        List<Item> list2 = List.of(ModItemsNeo.BLACK_SLEEPING_BAG.get(), ModItemsNeo.BLUE_SLEEPING_BAG.get(), ModItemsNeo.BROWN_SLEEPING_BAG.get(), ModItemsNeo.CYAN_SLEEPING_BAG.get(), ModItemsNeo.GRAY_SLEEPING_BAG.get(), ModItemsNeo.GREEN_SLEEPING_BAG.get(), ModItemsNeo.LIGHT_BLUE_SLEEPING_BAG.get(), ModItemsNeo.LIGHT_GRAY_SLEEPING_BAG.get(), ModItemsNeo.LIME_SLEEPING_BAG.get(), ModItemsNeo.MAGENTA_SLEEPING_BAG.get(), ModItemsNeo.ORANGE_SLEEPING_BAG.get(), ModItemsNeo.PINK_SLEEPING_BAG.get(), ModItemsNeo.PURPLE_SLEEPING_BAG.get(), ModItemsNeo.RED_SLEEPING_BAG.get(), ModItemsNeo.YELLOW_SLEEPING_BAG.get(), ModItemsNeo.WHITE_SLEEPING_BAG.get());
        colorBlockWithDye(writer, list, list2, "sleeping_bag");
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, name);
    }

    protected static void sleepingBagFromWool(RecipeOutput recipeOutput, ItemLike sleepingBag, ItemLike pWool) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sleepingBag).group("sleeping_bag").define('#', pWool).define('X', Items.WHITE_WOOL).pattern("##X").unlockedBy(getHasName(pWool), has(pWool)).save(recipeOutput);
    }

    protected static void colorBlockWithDye(RecipeOutput recipeOutput, List<Item> pDyes, List<Item> pDyeableItems, String pGroup) {
        for(int i = 0; i < pDyes.size(); ++i) {
            Item item = pDyes.get(i);
            Item item1 = pDyeableItems.get(i);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, item1)
                    .requires(item).requires(Ingredient.of(pDyeableItems.stream().filter((p_288265_) -> !p_288265_.equals(item1))
                            .map(ItemStack::new))).group(pGroup).unlockedBy("has_needed_dye", has(item))
                    .save(recipeOutput, id("dye_" + getItemName(item1)));
        }
    }

    public ShapedBackpackRecipeBuilder createBackpackSmallGrid(Item result, Ingredient ingredient, String criterionName, Criterion<?> criterion) {
        return ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', ingredient).define('B', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern(" A ").pattern("ABA").pattern(" A ").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createBackpackFullGrid(Item result, Ingredient ingredient, String criterionName, Criterion<?> criterion) {
        return ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', ingredient).define('B', ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get())
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(criterionName, criterion);
    }

    public ShapedBackpackRecipeBuilder createFullGrid(Item result, Ingredient ingredient, Ingredient ingredient1, Item unlocker, TagKey<Item> tag) {
        return ShapedBackpackRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('A', ingredient1).define('B', ingredient)
                .pattern("AAA").pattern("ABA").pattern("AAA").unlockedBy(getHasName(unlocker), has(tag));
    }

    public static final Item[] BACKPACKS = {
            ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.NETHERITE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.DIAMOND_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.GOLD_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.EMERALD_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.IRON_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.LAPIS_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.REDSTONE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.COAL_TRAVELERS_BACKPACK.get(),

            ModItemsNeo.QUARTZ_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.BOOKSHELF_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.END_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.NETHER_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SANDSTONE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SNOW_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SPONGE_TRAVELERS_BACKPACK.get(),

            ModItemsNeo.CAKE_TRAVELERS_BACKPACK.get(),

            ModItemsNeo.CACTUS_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.HAY_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.MELON_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItemsNeo.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.DRAGON_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.BLAZE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.GHAST_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SKELETON_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.WITHER_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.WARDEN_TRAVELERS_BACKPACK.get(),

            ModItemsNeo.BAT_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.BEE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.WOLF_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.FOX_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.OCELOT_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.HORSE_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.COW_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.PIG_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SHEEP_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.CHICKEN_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.SQUID_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.VILLAGER_TRAVELERS_BACKPACK.get(),
            ModItemsNeo.IRON_GOLEM_TRAVELERS_BACKPACK.get(),
    };
}