package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TravelersBackpack.MODID);

    //Standard
    public static final RegistryObject<Block> STANDARD_TRAVELERS_BACKPACK = BLOCKS.register("standard", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BROWN).build()).sound(SoundType.WOOL)));

    //Blocks
    public static final RegistryObject<Block> NETHERITE_TRAVELERS_BACKPACK = BLOCKS.register("netherite", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).sound(SoundType.NETHERITE_BLOCK)));
    public static final RegistryObject<Block> DIAMOND_TRAVELERS_BACKPACK = BLOCKS.register("diamond", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.DIAMOND).build()).sound(SoundType.METAL)));
    public static final RegistryObject<Block> GOLD_TRAVELERS_BACKPACK = BLOCKS.register("gold", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.GOLD).build()).sound(SoundType.METAL).lightLevel(f -> 10)));
    public static final RegistryObject<Block> EMERALD_TRAVELERS_BACKPACK = BLOCKS.register("emerald", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.EMERALD).build()).sound(SoundType.METAL)));
    public static final RegistryObject<Block> IRON_TRAVELERS_BACKPACK = BLOCKS.register("iron", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.METAL).build()).sound(SoundType.METAL)));
    public static final RegistryObject<Block> LAPIS_TRAVELERS_BACKPACK = BLOCKS.register("lapis", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.LAPIS).build()).sound(SoundType.STONE)));
    public static final RegistryObject<Block> REDSTONE_TRAVELERS_BACKPACK = BLOCKS.register("redstone", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.FIRE).build()).sound(SoundType.METAL).isRedstoneConductor((blockState, blockGetter, pos) -> false)));
    public static final RegistryObject<Block> COAL_TRAVELERS_BACKPACK = BLOCKS.register("coal", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).sound(SoundType.STONE)));

    public static final RegistryObject<Block> QUARTZ_TRAVELERS_BACKPACK = BLOCKS.register("quartz", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.QUARTZ).build()).sound(SoundType.STONE)));
    public static final RegistryObject<Block> BOOKSHELF_TRAVELERS_BACKPACK = BLOCKS.register("bookshelf", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BROWN).build()).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> END_TRAVELERS_BACKPACK = BLOCKS.register("end", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_GREEN).build()).sound(SoundType.GLASS).lightLevel(f -> 1)));
    public static final RegistryObject<Block> NETHER_TRAVELERS_BACKPACK = BLOCKS.register("nether", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.NETHER).build()).sound(SoundType.NETHER_BRICKS).lightLevel(f -> 11)));
    public static final RegistryObject<Block> SANDSTONE_TRAVELERS_BACKPACK = BLOCKS.register("sandstone", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.SAND).build()).sound(SoundType.STONE)));
    public static final RegistryObject<Block> SNOW_TRAVELERS_BACKPACK = BLOCKS.register("snow", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.SNOW).build()).sound(SoundType.SNOW)));
    public static final RegistryObject<Block> SPONGE_TRAVELERS_BACKPACK = BLOCKS.register("sponge", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_YELLOW).build()).sound(SoundType.GRASS)));

    //Food
    public static final RegistryObject<Block> CAKE_TRAVELERS_BACKPACK = BLOCKS.register("cake", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.NONE).build()).sound(SoundType.WOOL)));

    //Plants
    public static final RegistryObject<Block> CACTUS_TRAVELERS_BACKPACK = BLOCKS.register("cactus", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.PLANT).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> HAY_TRAVELERS_BACKPACK = BLOCKS.register("hay", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_YELLOW).build()).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> MELON_TRAVELERS_BACKPACK = BLOCKS.register("melon", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_LIGHT_GREEN).build()).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PUMPKIN_TRAVELERS_BACKPACK = BLOCKS.register("pumpkin", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_ORANGE).build()).sound(SoundType.WOOD)));

    //Mobs
    public static final RegistryObject<Block> CREEPER_TRAVELERS_BACKPACK = BLOCKS.register("creeper", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_GREEN).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DRAGON_TRAVELERS_BACKPACK = BLOCKS.register("dragon", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_BLACK).build()).sound(SoundType.METAL)));
    public static final RegistryObject<Block> ENDERMAN_TRAVELERS_BACKPACK = BLOCKS.register("enderman", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BLAZE_TRAVELERS_BACKPACK = BLOCKS.register("blaze", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_ORANGE).build()).sound(SoundType.METAL)));
    public static final RegistryObject<Block> GHAST_TRAVELERS_BACKPACK = BLOCKS.register("ghast", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_LIGHT_GRAY).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MAGMA_CUBE_TRAVELERS_BACKPACK = BLOCKS.register("magma_cube", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.NETHER).build()).sound(SoundType.SLIME_BLOCK)));
    public static final RegistryObject<Block> SKELETON_TRAVELERS_BACKPACK = BLOCKS.register("skeleton", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.WOOL).build()).sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> SPIDER_TRAVELERS_BACKPACK = BLOCKS.register("spider", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> WITHER_TRAVELERS_BACKPACK = BLOCKS.register("wither", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).sound(SoundType.BONE_BLOCK)));

    //Friendly Mobs
    public static final RegistryObject<Block> BAT_TRAVELERS_BACKPACK = BLOCKS.register("bat", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BROWN).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BEE_TRAVELERS_BACKPACK = BLOCKS.register("bee", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_YELLOW).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> WOLF_TRAVELERS_BACKPACK = BLOCKS.register("wolf", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_GRAY).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> FOX_TRAVELERS_BACKPACK = BLOCKS.register("fox", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_ORANGE).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> OCELOT_TRAVELERS_BACKPACK = BLOCKS.register("ocelot", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_ORANGE).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> HORSE_TRAVELERS_BACKPACK = BLOCKS.register("horse", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BROWN).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> COW_TRAVELERS_BACKPACK = BLOCKS.register("cow", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BROWN).build()).sound(SoundType.SLIME_BLOCK)));
    public static final RegistryObject<Block> PIG_TRAVELERS_BACKPACK = BLOCKS.register("pig", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_PINK).build()).sound(SoundType.SLIME_BLOCK)));
    public static final RegistryObject<Block> SHEEP_TRAVELERS_BACKPACK = BLOCKS.register("sheep", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_WHITE).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CHICKEN_TRAVELERS_BACKPACK = BLOCKS.register("chicken", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_WHITE).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> SQUID_TRAVELERS_BACKPACK = BLOCKS.register("squid", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_BLUE).build()).sound(SoundType.SLIME_BLOCK)));
    public static final RegistryObject<Block> VILLAGER_TRAVELERS_BACKPACK = BLOCKS.register("villager", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.TERRACOTTA_GRAY).build()).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> IRON_GOLEM_TRAVELERS_BACKPACK = BLOCKS.register("iron_golem", () -> new TravelersBackpackBlock(Block.Properties.of(new Material.Builder(MaterialColor.METAL).build()).sound(SoundType.METAL)));

    //Other Blocks
    public static final RegistryObject<Block> WHITE_SLEEPING_BAG = BLOCKS.register("white_sleeping_bag", () -> new SleepingBagBlock(DyeColor.WHITE, Block.Properties.of(new Material.Builder(MaterialColor.SNOW).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> ORANGE_SLEEPING_BAG = BLOCKS.register("orange_sleeping_bag", () -> new SleepingBagBlock(DyeColor.ORANGE, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_ORANGE).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_SLEEPING_BAG = BLOCKS.register("magenta_sleeping_bag", () -> new SleepingBagBlock(DyeColor.MAGENTA, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_MAGENTA).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_SLEEPING_BAG = BLOCKS.register("light_blue_sleeping_bag", () -> new SleepingBagBlock(DyeColor.LIGHT_BLUE, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_LIGHT_BLUE).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> YELLOW_SLEEPING_BAG = BLOCKS.register("yellow_sleeping_bag", () -> new SleepingBagBlock(DyeColor.YELLOW, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_YELLOW).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> LIME_SLEEPING_BAG = BLOCKS.register("lime_sleeping_bag", () -> new SleepingBagBlock(DyeColor.LIME, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_LIGHT_GREEN).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> PINK_SLEEPING_BAG = BLOCKS.register("pink_sleeping_bag", () -> new SleepingBagBlock(DyeColor.PINK, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_PINK).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> GRAY_SLEEPING_BAG = BLOCKS.register("gray_sleeping_bag", () -> new SleepingBagBlock(DyeColor.GRAY, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_GRAY).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_SLEEPING_BAG = BLOCKS.register("light_gray_sleeping_bag", () -> new SleepingBagBlock(DyeColor.LIGHT_GRAY, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_LIGHT_GRAY).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> CYAN_SLEEPING_BAG = BLOCKS.register("cyan_sleeping_bag", () -> new SleepingBagBlock(DyeColor.CYAN, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_CYAN).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> PURPLE_SLEEPING_BAG = BLOCKS.register("purple_sleeping_bag", () -> new SleepingBagBlock(DyeColor.PURPLE, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_PURPLE).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> BLUE_SLEEPING_BAG = BLOCKS.register("blue_sleeping_bag", () -> new SleepingBagBlock(DyeColor.BLUE, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLUE).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> BROWN_SLEEPING_BAG = BLOCKS.register("brown_sleeping_bag", () -> new SleepingBagBlock(DyeColor.BROWN, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BROWN).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> GREEN_SLEEPING_BAG = BLOCKS.register("green_sleeping_bag", () -> new SleepingBagBlock(DyeColor.GREEN, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_GREEN).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> RED_SLEEPING_BAG = BLOCKS.register("red_sleeping_bag", () -> new SleepingBagBlock(DyeColor.RED, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_RED).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
    public static final RegistryObject<Block> BLACK_SLEEPING_BAG = BLOCKS.register("black_sleeping_bag", () -> new SleepingBagBlock(DyeColor.BLACK, Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).sound(SoundType.WOOL).strength(0.2F).noOcclusion()));
}