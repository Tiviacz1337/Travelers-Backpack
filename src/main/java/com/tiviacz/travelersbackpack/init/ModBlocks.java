package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {

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
    public static Block WARDEN_TRAVELERS_BACKPACK;

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

    public static void init() {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "standard"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).sounds(SoundType.WOOL)));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sounds(SoundType.NETHERITE_BLOCK)));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.DIAMOND).sounds(SoundType.METAL)));
        GOLD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gold"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.GOLD).sounds(SoundType.METAL).luminance(10)));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "emerald"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.EMERALD).sounds(SoundType.METAL)));
        IRON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.METAL).sounds(SoundType.METAL)));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "lapis"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.LAPIS).sounds(SoundType.STONE)));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "redstone"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.FIRE).sounds(SoundType.METAL).solidBlock((blockState, blockView, pos) -> false)));
        COAL_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "coal"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sounds(SoundType.STONE)));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "quartz"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.QUARTZ).sounds(SoundType.STONE)));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).sounds(SoundType.WOOD)));
        END_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "end"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_GREEN).sounds(SoundType.GLASS).luminance(1)));
        NETHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "nether"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.NETHER).sounds(SoundType.NETHER_BRICKS).luminance(11)));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.SAND).sounds(SoundType.STONE)));
        SNOW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "snow"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.SNOW).sounds(SoundType.SNOW)));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sponge"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_YELLOW).sounds(SoundType.GRASS)));

        CAKE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cake"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.NONE).sounds(SoundType.WOOL)));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cactus"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.PLANT).sounds(SoundType.WOOL)));
        HAY_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hay"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_YELLOW).sounds(SoundType.GRASS)));
        MELON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "melon"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_LIGHT_GREEN).sounds(SoundType.WOOD)));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_ORANGE).sounds(SoundType.WOOD)));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "creeper"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_GREEN).sounds(SoundType.WOOL)));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "dragon"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_BLACK).sounds(SoundType.METAL)));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "enderman"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sounds(SoundType.WOOL)));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blaze"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_ORANGE).sounds(SoundType.METAL)));
        GHAST_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "ghast"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_LIGHT_GRAY).sounds(SoundType.WOOL)));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.NETHER).sounds(SoundType.SLIME_BLOCK)));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.WOOL).sounds(SoundType.BONE_BLOCK)));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "spider"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sounds(SoundType.WOOL)));
        WITHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "wither"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sounds(SoundType.BONE_BLOCK)));
        WARDEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "warden"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sound(SoundType.SCULK_SHRIEKER)));

        BAT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "bat"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).sounds(SoundType.WOOL)));
        BEE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "bee"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_YELLOW).sounds(SoundType.WOOL)));
        WOLF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "wolf"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_GRAY).sounds(SoundType.WOOL)));
        FOX_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "fox"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_ORANGE).sounds(SoundType.WOOL)));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_ORANGE).sounds(SoundType.WOOL)));
        HORSE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "horse"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).sounds(SoundType.WOOL)));
        COW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cow"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).sounds(SoundType.SLIME_BLOCK)));
        PIG_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pig"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_PINK).sounds(SoundType.SLIME_BLOCK)));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sheep"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_WHITE).sounds(SoundType.WOOL)));
        SQUID_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "squid"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_BLUE).sounds(SoundType.SLIME_BLOCK)));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "chicken"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_WHITE).sounds(SoundType.WOOL)));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "villager"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_GRAY).sounds(SoundType.WOOL)));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.METAL).sounds(SoundType.METAL)));

        WHITE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagBlock(DyeColor.WHITE, FabricBlockSettings.create().mapColor(MapColor.SNOW).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        ORANGE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagBlock(DyeColor.ORANGE, FabricBlockSettings.create().mapColor(MapColor.COLOR_ORANGE).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        MAGENTA_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagBlock(DyeColor.MAGENTA, FabricBlockSettings.create().mapColor(MapColor.COLOR_MAGENTA).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_BLUE, FabricBlockSettings.create().mapColor(MapColor.COLOR_LIGHT_BLUE).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        YELLOW_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagBlock(DyeColor.YELLOW, FabricBlockSettings.create().mapColor(MapColor.COLOR_YELLOW).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        LIME_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagBlock(DyeColor.LIME, FabricBlockSettings.create().mapColor(MapColor.COLOR_LIGHT_GREEN).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        PINK_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagBlock(DyeColor.PINK, FabricBlockSettings.create().mapColor(MapColor.COLOR_PINK).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagBlock(DyeColor.GRAY, FabricBlockSettings.create().mapColor(MapColor.COLOR_GRAY).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_GRAY, FabricBlockSettings.create().mapColor(MapColor.COLOR_LIGHT_GRAY).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        CYAN_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagBlock(DyeColor.CYAN, FabricBlockSettings.create().mapColor(MapColor.COLOR_CYAN).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        PURPLE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagBlock(DyeColor.PURPLE, FabricBlockSettings.create().mapColor(MapColor.COLOR_PURPLE).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagBlock(DyeColor.BLUE, FabricBlockSettings.create().mapColor(MapColor.COLOR_BLUE).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        BROWN_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagBlock(DyeColor.BROWN, FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        GREEN_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagBlock(DyeColor.GREEN, FabricBlockSettings.create().mapColor(MapColor.COLOR_GREEN).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        RED_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagBlock(DyeColor.RED, FabricBlockSettings.create().mapColor(MapColor.COLOR_RED).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
        BLACK_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagBlock(DyeColor.BLACK, FabricBlockSettings.create().mapColor(MapColor.COLOR_BLACK).sounds(SoundType.WOOL).strength(0.2F).pistonBehavior(PushReaction.DESTROY)));
    }
}