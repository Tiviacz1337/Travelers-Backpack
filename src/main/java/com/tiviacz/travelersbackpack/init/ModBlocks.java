package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class ModBlocks
{

    //Backpacks
    public static Block STANDARD_TRAVELERS_BACKPACK;

    public static Block NETHERITE_TRAVELERS_BACKPACK;
    public static Block DIAMOND_TRAVELERS_BACKPACK;
    public static Block GOLD_TRAVELERS_BACKPACK;
    public static Block EMERALD_TRAVELERS_BACKPACK;
    public static Block IRON_TRAVELERS_BACKPACK;
    public static Block LAPIS_TRAVELERS_BACKPACK;
    public static Block REDSTONE_TRAVELERS_BACKPACK;
    public static Block COAL_TRAVELERS_BACKPACK;

    public static Block QUARTZ_TRAVELERS_BACKPACK;
    public static Block BOOKSHELF_TRAVELERS_BACKPACK;
    public static Block END_TRAVELERS_BACKPACK;
    public static Block NETHER_TRAVELERS_BACKPACK;
    public static Block SANDSTONE_TRAVELERS_BACKPACK;
    public static Block SNOW_TRAVELERS_BACKPACK;
    public static Block SPONGE_TRAVELERS_BACKPACK;

    public static Block CAKE_TRAVELERS_BACKPACK;

    public static Block CACTUS_TRAVELERS_BACKPACK;
    public static Block HAY_TRAVELERS_BACKPACK;
    public static Block MELON_TRAVELERS_BACKPACK;
    public static Block PUMPKIN_TRAVELERS_BACKPACK;

    public static Block CREEPER_TRAVELERS_BACKPACK;
    public static Block DRAGON_TRAVELERS_BACKPACK;
    public static Block ENDERMAN_TRAVELERS_BACKPACK;
    public static Block BLAZE_TRAVELERS_BACKPACK;
    public static Block GHAST_TRAVELERS_BACKPACK;
    public static Block MAGMA_CUBE_TRAVELERS_BACKPACK;
    public static Block SKELETON_TRAVELERS_BACKPACK;
    public static Block SPIDER_TRAVELERS_BACKPACK;
    public static Block WITHER_TRAVELERS_BACKPACK;

    public static Block BAT_TRAVELERS_BACKPACK;
    public static Block BEE_TRAVELERS_BACKPACK;
    public static Block WOLF_TRAVELERS_BACKPACK;
    public static Block FOX_TRAVELERS_BACKPACK;
    public static Block OCELOT_TRAVELERS_BACKPACK;
    public static Block HORSE_TRAVELERS_BACKPACK;
    public static Block COW_TRAVELERS_BACKPACK;
    public static Block PIG_TRAVELERS_BACKPACK;
    public static Block SHEEP_TRAVELERS_BACKPACK;
    public static Block CHICKEN_TRAVELERS_BACKPACK;
    public static Block SQUID_TRAVELERS_BACKPACK;
    public static Block VILLAGER_TRAVELERS_BACKPACK;
    public static Block IRON_GOLEM_TRAVELERS_BACKPACK;

    public static Block WHITE_SLEEPING_BAG;
    public static Block ORANGE_SLEEPING_BAG;
    public static Block MAGENTA_SLEEPING_BAG;
    public static Block LIGHT_BLUE_SLEEPING_BAG;
    public static Block YELLOW_SLEEPING_BAG;
    public static Block LIME_SLEEPING_BAG;
    public static Block PINK_SLEEPING_BAG;
    public static Block GRAY_SLEEPING_BAG;
    public static Block LIGHT_GRAY_SLEEPING_BAG;
    public static Block CYAN_SLEEPING_BAG;
    public static Block PURPLE_SLEEPING_BAG;
    public static Block BLUE_SLEEPING_BAG;
    public static Block BROWN_SLEEPING_BAG;
    public static Block GREEN_SLEEPING_BAG;
    public static Block RED_SLEEPING_BAG;
    public static Block BLACK_SLEEPING_BAG;

    public static void init()
    {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "standard"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOL)));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "netherite"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.NETHERITE)));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "diamond"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.DIAMOND_BLUE).sounds(BlockSoundGroup.METAL)));
        GOLD_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "gold"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.GOLD).sounds(BlockSoundGroup.METAL).luminance(10)));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "emerald"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.EMERALD_GREEN).sounds(BlockSoundGroup.METAL)));
        IRON_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "iron"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.IRON_GRAY).sounds(BlockSoundGroup.METAL)));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "lapis"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.LAPIS_BLUE).sounds(BlockSoundGroup.STONE)));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "redstone"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BRIGHT_RED).sounds(BlockSoundGroup.METAL).solidBlock((blockState, blockView, pos) -> false)));
        COAL_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "coal"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.STONE)));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "quartz"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.OFF_WHITE).sounds(BlockSoundGroup.STONE)));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOD)));
        END_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "end"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.GLASS).luminance(1)));
        NETHER_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "nether"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.DARK_RED).sounds(BlockSoundGroup.NETHER_BRICKS).luminance(11)));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
        SNOW_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "snow"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.SNOW)));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "sponge"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.GRASS)));

        CAKE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "cake"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.CLEAR).sounds(BlockSoundGroup.WOOL)));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "cactus"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.DARK_GREEN).sounds(BlockSoundGroup.WOOL)));
        HAY_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "hay"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.GRASS)));
        MELON_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "melon"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.LIME).sounds(BlockSoundGroup.WOOD)));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.WOOD)));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "creeper"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.WOOL)));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "dragon"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_BLACK).sounds(BlockSoundGroup.METAL)));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "enderman"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.WOOL)));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "blaze"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_ORANGE).sounds(BlockSoundGroup.METAL)));
        GHAST_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "ghast"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.LIGHT_GRAY).sounds(BlockSoundGroup.WOOL)));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.DARK_RED).sounds(BlockSoundGroup.SLIME)));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.BONE)));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "spider"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.WOOL)));
        WITHER_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "wither"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.BONE)));

        BAT_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "bat"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOL)));
        BEE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "bee"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.WOOL)));
        WOLF_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "wolf"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.WOOL)));
        FOX_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "fox"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_ORANGE).sounds(BlockSoundGroup.WOOL)));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_ORANGE).sounds(BlockSoundGroup.WOOL)));
        HORSE_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "horse"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOL)));
        COW_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "cow"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.SLIME)));
        PIG_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "pig"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.PINK).sounds(BlockSoundGroup.SLIME)));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "sheep"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_WHITE).sounds(BlockSoundGroup.WOOL)));
        SQUID_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "squid"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_BLUE).sounds(BlockSoundGroup.SLIME)));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "chicken"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_WHITE).sounds(BlockSoundGroup.WOOL)));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "villager"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.WOOL)));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.IRON_GRAY).sounds(BlockSoundGroup.METAL)));

        WHITE_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagBlock(DyeColor.WHITE, FabricBlockSettings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        ORANGE_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagBlock(DyeColor.ORANGE, FabricBlockSettings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        MAGENTA_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagBlock(DyeColor.MAGENTA, FabricBlockSettings.create().mapColor(MapColor.MAGENTA).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_BLUE, FabricBlockSettings.create().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        YELLOW_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagBlock(DyeColor.YELLOW, FabricBlockSettings.create().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        LIME_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagBlock(DyeColor.LIME, FabricBlockSettings.create().mapColor(MapColor.LIME).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        PINK_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagBlock(DyeColor.PINK, FabricBlockSettings.create().mapColor(MapColor.PINK).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        GRAY_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagBlock(DyeColor.GRAY, FabricBlockSettings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_GRAY, FabricBlockSettings.create().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        CYAN_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagBlock(DyeColor.CYAN, FabricBlockSettings.create().mapColor(MapColor.CYAN).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        PURPLE_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagBlock(DyeColor.PURPLE, FabricBlockSettings.create().mapColor(MapColor.PURPLE).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        BLUE_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagBlock(DyeColor.BLUE, FabricBlockSettings.create().mapColor(MapColor.BLUE).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        BROWN_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagBlock(DyeColor.BROWN, FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        GREEN_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagBlock(DyeColor.GREEN, FabricBlockSettings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        RED_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagBlock(DyeColor.RED, FabricBlockSettings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
        BLACK_SLEEPING_BAG = Registry.register(Registries.BLOCK, new Identifier(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagBlock(DyeColor.BLACK, FabricBlockSettings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.WOOL).strength(0.2F).pistonBehavior(PistonBehavior.DESTROY)));
    }
}