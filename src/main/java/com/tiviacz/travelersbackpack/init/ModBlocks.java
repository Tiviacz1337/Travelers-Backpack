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

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TravelersBackpack.MODID);

    //Standard
    public static final DeferredBlock<TravelersBackpackBlock> STANDARD_TRAVELERS_BACKPACK = BLOCKS.registerBlock("standard", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL));

    //Blocks
    public static final DeferredBlock<TravelersBackpackBlock> NETHERITE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("netherite", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> DIAMOND_TRAVELERS_BACKPACK = BLOCKS.registerBlock("diamond", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.DIAMOND).sound(SoundType.METAL));
    public static final DeferredBlock<TravelersBackpackBlock> GOLD_TRAVELERS_BACKPACK = BLOCKS.registerBlock("gold", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).lightLevel(f -> 10));
    public static final DeferredBlock<TravelersBackpackBlock> EMERALD_TRAVELERS_BACKPACK = BLOCKS.registerBlock("emerald", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.EMERALD).sound(SoundType.METAL));
    public static final DeferredBlock<TravelersBackpackBlock> IRON_TRAVELERS_BACKPACK = BLOCKS.registerBlock("iron", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL));
    public static final DeferredBlock<TravelersBackpackBlock> LAPIS_TRAVELERS_BACKPACK = BLOCKS.registerBlock("lapis", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.LAPIS).sound(SoundType.STONE));
    public static final DeferredBlock<TravelersBackpackBlock> REDSTONE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("redstone", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.FIRE).sound(SoundType.METAL).isRedstoneConductor((blockState, blockGetter, pos) -> false));
    public static final DeferredBlock<TravelersBackpackBlock> COAL_TRAVELERS_BACKPACK = BLOCKS.registerBlock("coal", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE));

    public static final DeferredBlock<TravelersBackpackBlock> QUARTZ_TRAVELERS_BACKPACK = BLOCKS.registerBlock("quartz", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.QUARTZ).sound(SoundType.STONE));
    public static final DeferredBlock<TravelersBackpackBlock> BOOKSHELF_TRAVELERS_BACKPACK = BLOCKS.registerBlock("bookshelf", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
    public static final DeferredBlock<TravelersBackpackBlock> END_TRAVELERS_BACKPACK = BLOCKS.registerBlock("end", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.GLASS).lightLevel(f -> 1));
    public static final DeferredBlock<TravelersBackpackBlock> NETHER_TRAVELERS_BACKPACK = BLOCKS.registerBlock("nether", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.NETHER).sound(SoundType.NETHER_BRICKS).lightLevel(f -> 11));
    public static final DeferredBlock<TravelersBackpackBlock> SANDSTONE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("sandstone", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.SAND).sound(SoundType.STONE));
    public static final DeferredBlock<TravelersBackpackBlock> SNOW_TRAVELERS_BACKPACK = BLOCKS.registerBlock("snow", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.SNOW));
    public static final DeferredBlock<TravelersBackpackBlock> SPONGE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("sponge", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS));

    //Food
    public static final DeferredBlock<TravelersBackpackBlock> CAKE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("cake", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.NONE).sound(SoundType.WOOL));

    //Plants
    public static final DeferredBlock<TravelersBackpackBlock> CACTUS_TRAVELERS_BACKPACK = BLOCKS.registerBlock("cactus", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> HAY_TRAVELERS_BACKPACK = BLOCKS.registerBlock("hay", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS));
    public static final DeferredBlock<TravelersBackpackBlock> MELON_TRAVELERS_BACKPACK = BLOCKS.registerBlock("melon", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOD));
    public static final DeferredBlock<TravelersBackpackBlock> PUMPKIN_TRAVELERS_BACKPACK = BLOCKS.registerBlock("pumpkin", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOD));

    //Mobs
    public static final DeferredBlock<TravelersBackpackBlock> CREEPER_TRAVELERS_BACKPACK = BLOCKS.registerBlock("creeper", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> DRAGON_TRAVELERS_BACKPACK = BLOCKS.registerBlock("dragon", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).sound(SoundType.METAL));
    public static final DeferredBlock<TravelersBackpackBlock> ENDERMAN_TRAVELERS_BACKPACK = BLOCKS.registerBlock("enderman", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> BLAZE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("blaze", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.METAL));
    public static final DeferredBlock<TravelersBackpackBlock> GHAST_TRAVELERS_BACKPACK = BLOCKS.registerBlock("ghast", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> MAGMA_CUBE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("magma_cube", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.NETHER).sound(SoundType.SLIME_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> SKELETON_TRAVELERS_BACKPACK = BLOCKS.registerBlock("skeleton", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.WOOL).sound(SoundType.BONE_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> SPIDER_TRAVELERS_BACKPACK = BLOCKS.registerBlock("spider", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> WITHER_TRAVELERS_BACKPACK = BLOCKS.registerBlock("wither", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.BONE_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> WARDEN_TRAVELERS_BACKPACK = BLOCKS.registerBlock("warden", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.SCULK_SHRIEKER));

    //Friendly Mobs
    public static final DeferredBlock<TravelersBackpackBlock> BAT_TRAVELERS_BACKPACK = BLOCKS.registerBlock("bat", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> BEE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("bee", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> WOLF_TRAVELERS_BACKPACK = BLOCKS.registerBlock("wolf", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> FOX_TRAVELERS_BACKPACK = BLOCKS.registerBlock("fox", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> OCELOT_TRAVELERS_BACKPACK = BLOCKS.registerBlock("ocelot", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> HORSE_TRAVELERS_BACKPACK = BLOCKS.registerBlock("horse", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> COW_TRAVELERS_BACKPACK = BLOCKS.registerBlock("cow", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.SLIME_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> PIG_TRAVELERS_BACKPACK = BLOCKS.registerBlock("pig", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> SHEEP_TRAVELERS_BACKPACK = BLOCKS.registerBlock("sheep", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> CHICKEN_TRAVELERS_BACKPACK = BLOCKS.registerBlock("chicken", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> SQUID_TRAVELERS_BACKPACK = BLOCKS.registerBlock("squid", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.SLIME_BLOCK));
    public static final DeferredBlock<TravelersBackpackBlock> VILLAGER_TRAVELERS_BACKPACK = BLOCKS.registerBlock("villager", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).sound(SoundType.WOOL));
    public static final DeferredBlock<TravelersBackpackBlock> IRON_GOLEM_TRAVELERS_BACKPACK = BLOCKS.registerBlock("iron_golem", TravelersBackpackBlock::new, () -> Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL));

    //Other Blocks
    public static final DeferredBlock<SleepingBagBlock> WHITE_SLEEPING_BAG = BLOCKS.registerBlock("white_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.WHITE, props), () -> Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> ORANGE_SLEEPING_BAG = BLOCKS.registerBlock("orange_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.ORANGE, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> MAGENTA_SLEEPING_BAG = BLOCKS.registerBlock("magenta_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.MAGENTA, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> LIGHT_BLUE_SLEEPING_BAG = BLOCKS.registerBlock("light_blue_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.LIGHT_BLUE, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> YELLOW_SLEEPING_BAG = BLOCKS.registerBlock("yellow_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.YELLOW, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> LIME_SLEEPING_BAG = BLOCKS.registerBlock("lime_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.LIME, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> PINK_SLEEPING_BAG = BLOCKS.registerBlock("pink_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.PINK, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> GRAY_SLEEPING_BAG = BLOCKS.registerBlock("gray_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.GRAY, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> LIGHT_GRAY_SLEEPING_BAG = BLOCKS.registerBlock("light_gray_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.LIGHT_GRAY, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> CYAN_SLEEPING_BAG = BLOCKS.registerBlock("cyan_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.CYAN, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> PURPLE_SLEEPING_BAG = BLOCKS.registerBlock("purple_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.PURPLE, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> BLUE_SLEEPING_BAG = BLOCKS.registerBlock("blue_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.BLUE, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> BROWN_SLEEPING_BAG = BLOCKS.registerBlock("brown_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.BROWN, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> GREEN_SLEEPING_BAG = BLOCKS.registerBlock("green_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.GREEN, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> RED_SLEEPING_BAG = BLOCKS.registerBlock("red_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.RED, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> BLACK_SLEEPING_BAG = BLOCKS.registerBlock("black_sleeping_bag", (props) -> new SleepingBagBlock(DyeColor.BLACK, props), () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
}