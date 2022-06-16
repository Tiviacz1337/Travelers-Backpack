package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

    public static Block HAY_TRAVELERS_BACKPACK;
    public static Block MELON_TRAVELERS_BACKPACK;
    public static Block PUMPKIN_TRAVELERS_BACKPACK;

    public static Block BLAZE_TRAVELERS_BACKPACK;

    public static Block BAT_TRAVELERS_BACKPACK;
    public static Block WOLF_TRAVELERS_BACKPACK;
    public static Block FOX_TRAVELERS_BACKPACK;
    public static Block OCELOT_TRAVELERS_BACKPACK;
    public static Block COW_TRAVELERS_BACKPACK;
    public static Block PIG_TRAVELERS_BACKPACK;
    public static Block CHICKEN_TRAVELERS_BACKPACK;
    public static Block VILLAGER_TRAVELERS_BACKPACK;

    public static Block SLEEPING_BAG;

    public static void init()
    {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "standard"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL)));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "netherite"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.NETHERITE)));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "diamond"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.DIAMOND_BLUE).build()).sounds(BlockSoundGroup.METAL)));
        GOLD_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "gold"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GOLD).build()).sounds(BlockSoundGroup.METAL)));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "emerald"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.EMERALD_GREEN).build()).sounds(BlockSoundGroup.METAL)));
        IRON_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "iron"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.IRON_GRAY).build()).sounds(BlockSoundGroup.METAL)));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "lapis"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.LAPIS_BLUE).build()).sounds(BlockSoundGroup.STONE)));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "redstone"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BRIGHT_RED).build()).sounds(BlockSoundGroup.METAL)));
        COAL_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "coal"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.STONE)));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "quartz"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.OFF_WHITE).build()).sounds(BlockSoundGroup.STONE)));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOD)));

        HAY_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "hay"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.YELLOW).build()).sounds(BlockSoundGroup.GRASS)));
        MELON_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "melon"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.LIME).build()).sounds(BlockSoundGroup.WOOD)));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.ORANGE).build()).sounds(BlockSoundGroup.WOOD)));

        BLAZE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "blaze"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_ORANGE).build()).sounds(BlockSoundGroup.METAL)));

        BAT_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "bat"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL)));
        WOLF_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "wolf"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GRAY).build()).sounds(BlockSoundGroup.WOOL)));
        FOX_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "fox"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_ORANGE).build()).sounds(BlockSoundGroup.WOOL)));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_ORANGE).build()).sounds(BlockSoundGroup.WOOL)));
        COW_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "cow"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.SLIME)));
        PIG_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "pig"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.PINK).build()).sounds(BlockSoundGroup.SLIME)));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "chicken"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_WHITE).build()).sounds(BlockSoundGroup.WOOL)));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "villager"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GRAY).build()).sounds(BlockSoundGroup.WOOL)));

        SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "sleeping_bag"), new SleepingBagBlock(FabricBlockSettings.of(new Material.Builder(MapColor.RED).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
    }

   // private static final Map<Identifier, BlockItem> ITEMS = new LinkedHashMap<>();
    //private static final Map<Identifier, Block> BLOCKS = new LinkedHashMap<>();

    //public static final Block STANDARD_TRAVELERS_BACKPACK = add("standard", new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL)));

  /*  private static <B extends Block> B add(String name, B block, ItemGroup tab) {
        Item.Settings settings = new Item.Settings();
        if (tab != null) {
            settings.group(tab);
        }
        return add(name, block, new TravelersBackpackItem(block));
    }

    private static <B extends Block> B add(String name, B block, BlockItem item) {
        add(name, block);
        if (item != null) {
            item.appendBlocks(Item.BLOCK_ITEMS, item);
            ITEMS.put(new Identifier(TravelersBackpack.MODID, name), item);
        }
        return block;
    }

    private static <B extends Block> B add(String name, B block) {
        BLOCKS.put(new Identifier(TravelersBackpack.MODID, name), block);
        return block;
    }

    private static <I extends BlockItem> I add(String name, I item) {
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        ITEMS.put(new Identifier(TravelersBackpack.MODID, name), item);
        return item;
    }

    public static void register() {
        for (Identifier id : ITEMS.keySet()) {
            Registry.register(Registry.ITEM, id, ITEMS.get(id));
        }
        for (Identifier id : BLOCKS.keySet()) {
            Registry.register(Registry.BLOCK, id, BLOCKS.get(id));
        }
    }

    public static Map<Identifier, Block> getBlocks()
    {
        return BLOCKS;
    } */
}