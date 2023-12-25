package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TravelersBackpack.MODID);

    //Standard
    public static final DeferredBlock<TravelersBackpackBlock> STANDARD_TRAVELERS_BACKPACK = BLOCKS.register("standard", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));

    //Blocks
    public static final DeferredBlock<TravelersBackpackBlock> NETHERITE_TRAVELERS_BACKPACK = BLOCKS.register("netherite", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK)));
    public static final DeferredBlock<TravelersBackpackBlock> DIAMOND_TRAVELERS_BACKPACK = BLOCKS.register("diamond", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.DIAMOND).sound(SoundType.METAL)));
    public static final DeferredBlock<TravelersBackpackBlock> GOLD_TRAVELERS_BACKPACK = BLOCKS.register("gold", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).lightLevel(f -> 10)));
    public static final DeferredBlock<TravelersBackpackBlock> EMERALD_TRAVELERS_BACKPACK = BLOCKS.register("emerald", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.EMERALD).sound(SoundType.METAL)));
    public static final DeferredBlock<TravelersBackpackBlock> IRON_TRAVELERS_BACKPACK = BLOCKS.register("iron", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)));
    public static final DeferredBlock<TravelersBackpackBlock> LAPIS_TRAVELERS_BACKPACK = BLOCKS.register("lapis", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.LAPIS).sound(SoundType.STONE)));
    public static final DeferredBlock<TravelersBackpackBlock> REDSTONE_TRAVELERS_BACKPACK = BLOCKS.register("redstone", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.FIRE).sound(SoundType.METAL).isRedstoneConductor((blockState, blockGetter, pos) -> false)));
    public static final DeferredBlock<TravelersBackpackBlock> COAL_TRAVELERS_BACKPACK = BLOCKS.register("coal", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE)));

    public static final DeferredBlock<TravelersBackpackBlock> QUARTZ_TRAVELERS_BACKPACK = BLOCKS.register("quartz", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.QUARTZ).sound(SoundType.STONE)));
    public static final DeferredBlock<TravelersBackpackBlock> BOOKSHELF_TRAVELERS_BACKPACK = BLOCKS.register("bookshelf", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD)));
    public static final DeferredBlock<TravelersBackpackBlock> END_TRAVELERS_BACKPACK = BLOCKS.register("end", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.GLASS).lightLevel(f -> 1)));
    public static final DeferredBlock<TravelersBackpackBlock> NETHER_TRAVELERS_BACKPACK = BLOCKS.register("nether", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.NETHER).sound(SoundType.NETHER_BRICKS).lightLevel(f -> 11)));
    public static final DeferredBlock<TravelersBackpackBlock> SANDSTONE_TRAVELERS_BACKPACK = BLOCKS.register("sandstone", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.SAND).sound(SoundType.STONE)));
    public static final DeferredBlock<TravelersBackpackBlock> SNOW_TRAVELERS_BACKPACK = BLOCKS.register("snow", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.SNOW)));
    public static final DeferredBlock<TravelersBackpackBlock> SPONGE_TRAVELERS_BACKPACK = BLOCKS.register("sponge", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS)));

    //Food
    public static final DeferredBlock<TravelersBackpackBlock> CAKE_TRAVELERS_BACKPACK = BLOCKS.register("cake", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.NONE).sound(SoundType.WOOL)));

    //Plants
    public static final DeferredBlock<TravelersBackpackBlock> CACTUS_TRAVELERS_BACKPACK = BLOCKS.register("cactus", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> HAY_TRAVELERS_BACKPACK = BLOCKS.register("hay", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS)));
    public static final DeferredBlock<TravelersBackpackBlock> MELON_TRAVELERS_BACKPACK = BLOCKS.register("melon", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOD)));
    public static final DeferredBlock<TravelersBackpackBlock> PUMPKIN_TRAVELERS_BACKPACK = BLOCKS.register("pumpkin", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOD)));

    //Mobs
    public static final DeferredBlock<TravelersBackpackBlock> CREEPER_TRAVELERS_BACKPACK = BLOCKS.register("creeper", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> DRAGON_TRAVELERS_BACKPACK = BLOCKS.register("dragon", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).sound(SoundType.METAL)));
    public static final DeferredBlock<TravelersBackpackBlock> ENDERMAN_TRAVELERS_BACKPACK = BLOCKS.register("enderman", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> BLAZE_TRAVELERS_BACKPACK = BLOCKS.register("blaze", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.METAL)));
    public static final DeferredBlock<TravelersBackpackBlock> GHAST_TRAVELERS_BACKPACK = BLOCKS.register("ghast", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> MAGMA_CUBE_TRAVELERS_BACKPACK = BLOCKS.register("magma_cube", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.NETHER).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<TravelersBackpackBlock> SKELETON_TRAVELERS_BACKPACK = BLOCKS.register("skeleton", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.WOOL).sound(SoundType.BONE_BLOCK)));
    public static final DeferredBlock<TravelersBackpackBlock> SPIDER_TRAVELERS_BACKPACK = BLOCKS.register("spider", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> WITHER_TRAVELERS_BACKPACK = BLOCKS.register("wither", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.BONE_BLOCK)));

    //Friendly Mobs
    public static final DeferredBlock<TravelersBackpackBlock> BAT_TRAVELERS_BACKPACK = BLOCKS.register("bat", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> BEE_TRAVELERS_BACKPACK = BLOCKS.register("bee", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> WOLF_TRAVELERS_BACKPACK = BLOCKS.register("wolf", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> FOX_TRAVELERS_BACKPACK = BLOCKS.register("fox", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> OCELOT_TRAVELERS_BACKPACK = BLOCKS.register("ocelot", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> HORSE_TRAVELERS_BACKPACK = BLOCKS.register("horse", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> COW_TRAVELERS_BACKPACK = BLOCKS.register("cow", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<TravelersBackpackBlock> PIG_TRAVELERS_BACKPACK = BLOCKS.register("pig", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<TravelersBackpackBlock> SHEEP_TRAVELERS_BACKPACK = BLOCKS.register("sheep", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> CHICKEN_TRAVELERS_BACKPACK = BLOCKS.register("chicken", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> SQUID_TRAVELERS_BACKPACK = BLOCKS.register("squid", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<TravelersBackpackBlock> VILLAGER_TRAVELERS_BACKPACK = BLOCKS.register("villager", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).sound(SoundType.WOOL)));
    public static final DeferredBlock<TravelersBackpackBlock> IRON_GOLEM_TRAVELERS_BACKPACK = BLOCKS.register("iron_golem", () -> new TravelersBackpackBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)));

    //Other Blocks
    public static final DeferredBlock<SleepingBagBlock> WHITE_SLEEPING_BAG = BLOCKS.register("white_sleeping_bag", () -> new SleepingBagBlock(DyeColor.WHITE, Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> ORANGE_SLEEPING_BAG = BLOCKS.register("orange_sleeping_bag", () -> new SleepingBagBlock(DyeColor.ORANGE, Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> MAGENTA_SLEEPING_BAG = BLOCKS.register("magenta_sleeping_bag", () -> new SleepingBagBlock(DyeColor.MAGENTA, Block.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> LIGHT_BLUE_SLEEPING_BAG = BLOCKS.register("light_blue_sleeping_bag", () -> new SleepingBagBlock(DyeColor.LIGHT_BLUE, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> YELLOW_SLEEPING_BAG = BLOCKS.register("yellow_sleeping_bag", () -> new SleepingBagBlock(DyeColor.YELLOW, Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> LIME_SLEEPING_BAG = BLOCKS.register("lime_sleeping_bag", () -> new SleepingBagBlock(DyeColor.LIME, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> PINK_SLEEPING_BAG = BLOCKS.register("pink_sleeping_bag", () -> new SleepingBagBlock(DyeColor.PINK, Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> GRAY_SLEEPING_BAG = BLOCKS.register("gray_sleeping_bag", () -> new SleepingBagBlock(DyeColor.GRAY, Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> LIGHT_GRAY_SLEEPING_BAG = BLOCKS.register("light_gray_sleeping_bag", () -> new SleepingBagBlock(DyeColor.LIGHT_GRAY, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> CYAN_SLEEPING_BAG = BLOCKS.register("cyan_sleeping_bag", () -> new SleepingBagBlock(DyeColor.CYAN, Block.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> PURPLE_SLEEPING_BAG = BLOCKS.register("purple_sleeping_bag", () -> new SleepingBagBlock(DyeColor.PURPLE, Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> BLUE_SLEEPING_BAG = BLOCKS.register("blue_sleeping_bag", () -> new SleepingBagBlock(DyeColor.BLUE, Block.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> BROWN_SLEEPING_BAG = BLOCKS.register("brown_sleeping_bag", () -> new SleepingBagBlock(DyeColor.BROWN, Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> GREEN_SLEEPING_BAG = BLOCKS.register("green_sleeping_bag", () -> new SleepingBagBlock(DyeColor.GREEN, Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> RED_SLEEPING_BAG = BLOCKS.register("red_sleeping_bag", () -> new SleepingBagBlock(DyeColor.RED, Block.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<SleepingBagBlock> BLACK_SLEEPING_BAG = BLOCKS.register("black_sleeping_bag", () -> new SleepingBagBlock(DyeColor.BLACK, Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)));
}