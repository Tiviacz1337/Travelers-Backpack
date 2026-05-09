package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.block.SleepingBagBlock;
import com.tiviacz.travelersbackpack.block.TravelersBackpackBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
        STANDARD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "standard"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).setId(resourceKey("standard"))));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).setId(resourceKey("netherite"))));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.DIAMOND).sound(SoundType.METAL).setId(resourceKey("diamond"))));
        GOLD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "gold"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).setId(resourceKey("gold")).lightLevel(f -> 10)));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "emerald"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.EMERALD).sound(SoundType.METAL).setId(resourceKey("emerald"))));
        IRON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "iron"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).setId(resourceKey("iron"))));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "lapis"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.LAPIS).sound(SoundType.STONE).setId(resourceKey("lapis"))));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "redstone"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.FIRE).sound(SoundType.METAL).setId(resourceKey("redstone")).isRedstoneConductor((blockState, blockView, pos) -> false)));
        COAL_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "coal"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).setId(resourceKey("coal"))));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "quartz"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.QUARTZ).sound(SoundType.STONE).setId(resourceKey("quartz"))));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).setId(resourceKey("bookshelf"))));
        END_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "end"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.GLASS).setId(resourceKey("end")).lightLevel(f -> 1)));
        NETHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "nether"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.NETHER).sound(SoundType.NETHER_BRICKS).setId(resourceKey("nether")).lightLevel(f -> 11)));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.SAND).sound(SoundType.STONE).setId(resourceKey("sandstone"))));
        SNOW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "snow"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.SNOW).setId(resourceKey("snow"))));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sponge"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS).setId(resourceKey("sponge"))));

        CAKE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cake"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.NONE).sound(SoundType.WOOL).setId(resourceKey("cake"))));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cactus"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.WOOL).setId(resourceKey("cactus"))));
        HAY_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "hay"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS).setId(resourceKey("hay"))));
        MELON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "melon"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOD).setId(resourceKey("melon"))));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOD).setId(resourceKey("pumpkin"))));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "creeper"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).setId(resourceKey("creeper"))));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "dragon"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).sound(SoundType.METAL).setId(resourceKey("dragon"))));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "enderman"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).setId(resourceKey("enderman"))));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "blaze"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.METAL).setId(resourceKey("blaze"))));
        GHAST_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "ghast"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).setId(resourceKey("ghast"))));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.NETHER).sound(SoundType.SLIME_BLOCK).setId(resourceKey("magma_cube"))));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.WOOL).sound(SoundType.BONE_BLOCK).setId(resourceKey("skeleton"))));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "spider"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).setId(resourceKey("spider"))));
        WITHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "wither"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.BONE_BLOCK).setId(resourceKey("wither"))));
        WARDEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "warden"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.SCULK_SHRIEKER).setId(resourceKey("warden"))));

        BAT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "bat"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).setId(resourceKey("bat"))));
        BEE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "bee"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).setId(resourceKey("bee"))));
        WOLF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "wolf"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).setId(resourceKey("wolf"))));
        FOX_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "fox"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.WOOL).setId(resourceKey("fox"))));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.WOOL).setId(resourceKey("ocelot"))));
        HORSE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "horse"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).setId(resourceKey("horse"))));
        COW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cow"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.SLIME_BLOCK).setId(resourceKey("cow"))));
        PIG_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pig"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).setId(resourceKey("pig"))));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sheep"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.WOOL).setId(resourceKey("sheep"))));
        SQUID_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "squid"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.SLIME_BLOCK).setId(resourceKey("squid"))));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "chicken"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.WOOL).setId(resourceKey("chicken"))));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "villager"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).setId(resourceKey("villager"))));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).setId(resourceKey("iron_golem"))));

        WHITE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagBlock(DyeColor.WHITE, Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.WOOL).setId(resourceKey("white_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        ORANGE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagBlock(DyeColor.ORANGE, Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).setId(resourceKey("orange_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        MAGENTA_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagBlock(DyeColor.MAGENTA, Block.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).setId(resourceKey("magenta_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_BLUE, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).setId(resourceKey("light_blue_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        YELLOW_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagBlock(DyeColor.YELLOW, Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).setId(resourceKey("yellow_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        LIME_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagBlock(DyeColor.LIME, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).setId(resourceKey("lime_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        PINK_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagBlock(DyeColor.PINK, Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).setId(resourceKey("pink_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagBlock(DyeColor.GRAY, Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).setId(resourceKey("gray_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_GRAY, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).setId(resourceKey("light_gray_sleeping_bag")).sound(SoundType.WOOL).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        CYAN_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagBlock(DyeColor.CYAN, Block.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOL).setId(resourceKey("cyan_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        PURPLE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagBlock(DyeColor.PURPLE, Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).setId(resourceKey("purple_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagBlock(DyeColor.BLUE, Block.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).setId(resourceKey("blue_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        BROWN_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagBlock(DyeColor.BROWN, Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).setId(resourceKey("brown_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        GREEN_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagBlock(DyeColor.GREEN, Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).setId(resourceKey("green_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        RED_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagBlock(DyeColor.RED, Block.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).setId(resourceKey("red_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
        BLACK_SLEEPING_BAG = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagBlock(DyeColor.BLACK, Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).setId(resourceKey("black_sleeping_bag")).strength(0.2F).pushReaction(PushReaction.DESTROY)));
    }

    public static Identifier resourceLocation(String name) {
        return Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, name);
    }

    public static ResourceKey<Block> resourceKey(String name) {
        return ResourceKey.create(Registries.BLOCK, resourceLocation(name));
    }
}