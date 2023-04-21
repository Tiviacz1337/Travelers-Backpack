package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModItems
{
    public static final List<TravelersBackpackItem> BACKPACKS = new ArrayList<>();

    //Backpacks
    public static TravelersBackpackItem STANDARD_TRAVELERS_BACKPACK;

    public static TravelersBackpackItem NETHERITE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem DIAMOND_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem GOLD_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem EMERALD_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem IRON_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem LAPIS_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem REDSTONE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem COAL_TRAVELERS_BACKPACK;

    public static TravelersBackpackItem QUARTZ_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem BOOKSHELF_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem END_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem NETHER_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SANDSTONE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SNOW_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SPONGE_TRAVELERS_BACKPACK;

    public static TravelersBackpackItem CAKE_TRAVELERS_BACKPACK;

    public static TravelersBackpackItem CACTUS_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem HAY_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem MELON_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem PUMPKIN_TRAVELERS_BACKPACK;

    public static TravelersBackpackItem CREEPER_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem DRAGON_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem ENDERMAN_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem BLAZE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem GHAST_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem MAGMA_CUBE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SKELETON_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SPIDER_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem WITHER_TRAVELERS_BACKPACK;

    public static TravelersBackpackItem BAT_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem BEE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem WOLF_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem FOX_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem OCELOT_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem HORSE_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem COW_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem PIG_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SHEEP_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem CHICKEN_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem SQUID_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem VILLAGER_TRAVELERS_BACKPACK;
    public static TravelersBackpackItem IRON_GOLEM_TRAVELERS_BACKPACK;

    //Other
    public static Item WHITE_SLEEPING_BAG;
    public static Item ORANGE_SLEEPING_BAG;
    public static Item MAGENTA_SLEEPING_BAG;
    public static Item LIGHT_BLUE_SLEEPING_BAG;
    public static Item YELLOW_SLEEPING_BAG;
    public static Item LIME_SLEEPING_BAG;
    public static Item PINK_SLEEPING_BAG;
    public static Item GRAY_SLEEPING_BAG;
    public static Item LIGHT_GRAY_SLEEPING_BAG;
    public static Item CYAN_SLEEPING_BAG;
    public static Item PURPLE_SLEEPING_BAG;
    public static Item BLUE_SLEEPING_BAG;
    public static Item BROWN_SLEEPING_BAG;
    public static Item GREEN_SLEEPING_BAG;
    public static Item RED_SLEEPING_BAG;
    public static Item BLACK_SLEEPING_BAG;
    public static Item BACKPACK_TANK;
    public static Item HOSE;
    public static Item HOSE_NOZZLE;

    public static void init()
    {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "standard"), new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "netherite"), new TravelersBackpackItem(ModBlocks.NETHERITE_TRAVELERS_BACKPACK));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "diamond"), new TravelersBackpackItem(ModBlocks.DIAMOND_TRAVELERS_BACKPACK));
        GOLD_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "gold"), new TravelersBackpackItem(ModBlocks.GOLD_TRAVELERS_BACKPACK));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "emerald"), new TravelersBackpackItem(ModBlocks.EMERALD_TRAVELERS_BACKPACK));
        IRON_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "iron"), new TravelersBackpackItem(ModBlocks.IRON_TRAVELERS_BACKPACK));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "lapis"), new TravelersBackpackItem(ModBlocks.LAPIS_TRAVELERS_BACKPACK));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "redstone"), new TravelersBackpackItem(ModBlocks.REDSTONE_TRAVELERS_BACKPACK));
        COAL_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "coal"), new TravelersBackpackItem(ModBlocks.COAL_TRAVELERS_BACKPACK));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "quartz"), new TravelersBackpackItem(ModBlocks.QUARTZ_TRAVELERS_BACKPACK));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackItem(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK));
        END_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "end"), new TravelersBackpackItem(ModBlocks.END_TRAVELERS_BACKPACK));
        NETHER_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "nether"), new TravelersBackpackItem(ModBlocks.NETHER_TRAVELERS_BACKPACK));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackItem(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK));
        SNOW_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "snow"), new TravelersBackpackItem(ModBlocks.SNOW_TRAVELERS_BACKPACK));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "sponge"), new TravelersBackpackItem(ModBlocks.SPONGE_TRAVELERS_BACKPACK));

        CAKE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "cake"), new TravelersBackpackItem(ModBlocks.CAKE_TRAVELERS_BACKPACK));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "cactus"), new TravelersBackpackItem(ModBlocks.CACTUS_TRAVELERS_BACKPACK));
        HAY_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "hay"), new TravelersBackpackItem(ModBlocks.HAY_TRAVELERS_BACKPACK));
        MELON_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "melon"), new TravelersBackpackItem(ModBlocks.MELON_TRAVELERS_BACKPACK));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackItem(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "creeper"), new TravelersBackpackItem(ModBlocks.CREEPER_TRAVELERS_BACKPACK));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "dragon"), new TravelersBackpackItem(ModBlocks.DRAGON_TRAVELERS_BACKPACK));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "enderman"), new TravelersBackpackItem(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "blaze"), new TravelersBackpackItem(ModBlocks.BLAZE_TRAVELERS_BACKPACK));
        GHAST_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "ghast"), new TravelersBackpackItem(ModBlocks.GHAST_TRAVELERS_BACKPACK));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackItem(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackItem(ModBlocks.SKELETON_TRAVELERS_BACKPACK));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "spider"), new TravelersBackpackItem(ModBlocks.SPIDER_TRAVELERS_BACKPACK));
        WITHER_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "wither"), new TravelersBackpackItem(ModBlocks.WITHER_TRAVELERS_BACKPACK));

        BAT_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "bat"), new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK));
        BEE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "bee"), new TravelersBackpackItem(ModBlocks.BEE_TRAVELERS_BACKPACK));
        WOLF_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "wolf"), new TravelersBackpackItem(ModBlocks.WOLF_TRAVELERS_BACKPACK));
        FOX_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "fox"), new TravelersBackpackItem(ModBlocks.FOX_TRAVELERS_BACKPACK));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackItem(ModBlocks.OCELOT_TRAVELERS_BACKPACK));
        HORSE_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "horse"), new TravelersBackpackItem(ModBlocks.HORSE_TRAVELERS_BACKPACK));
        COW_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "cow"), new TravelersBackpackItem(ModBlocks.COW_TRAVELERS_BACKPACK));
        PIG_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "pig"), new TravelersBackpackItem(ModBlocks.PIG_TRAVELERS_BACKPACK));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "sheep"), new TravelersBackpackItem(ModBlocks.SHEEP_TRAVELERS_BACKPACK));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "chicken"), new TravelersBackpackItem(ModBlocks.CHICKEN_TRAVELERS_BACKPACK));
        SQUID_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "squid"), new TravelersBackpackItem(ModBlocks.SQUID_TRAVELERS_BACKPACK));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "villager"), new TravelersBackpackItem(ModBlocks.VILLAGER_TRAVELERS_BACKPACK));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackItem(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK));

        WHITE_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagItem(ModBlocks.WHITE_SLEEPING_BAG, new FabricItemSettings()));
        ORANGE_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagItem(ModBlocks.ORANGE_SLEEPING_BAG, new FabricItemSettings()));
        MAGENTA_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagItem(ModBlocks.MAGENTA_SLEEPING_BAG, new FabricItemSettings()));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagItem(ModBlocks.LIGHT_BLUE_SLEEPING_BAG, new FabricItemSettings()));
        YELLOW_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagItem(ModBlocks.YELLOW_SLEEPING_BAG, new FabricItemSettings()));
        LIME_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagItem(ModBlocks.LIME_SLEEPING_BAG, new FabricItemSettings()));
        PINK_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagItem(ModBlocks.PINK_SLEEPING_BAG, new FabricItemSettings()));
        GRAY_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagItem(ModBlocks.GRAY_SLEEPING_BAG, new FabricItemSettings()));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagItem(ModBlocks.LIGHT_GRAY_SLEEPING_BAG, new FabricItemSettings()));
        CYAN_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagItem(ModBlocks.CYAN_SLEEPING_BAG, new FabricItemSettings()));
        PURPLE_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagItem(ModBlocks.PURPLE_SLEEPING_BAG, new FabricItemSettings()));
        BLUE_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagItem(ModBlocks.BLUE_SLEEPING_BAG, new FabricItemSettings()));
        BROWN_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagItem(ModBlocks.BROWN_SLEEPING_BAG, new FabricItemSettings()));
        GREEN_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagItem(ModBlocks.GREEN_SLEEPING_BAG, new FabricItemSettings()));
        RED_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagItem(ModBlocks.RED_SLEEPING_BAG, new FabricItemSettings()));
        BLACK_SLEEPING_BAG = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagItem(ModBlocks.BLACK_SLEEPING_BAG, new FabricItemSettings()));
        BACKPACK_TANK = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "backpack_tank"), new Item(new FabricItemSettings().maxCount(16)));
        HOSE = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "hose"), new HoseItem(new FabricItemSettings().maxCount(1)));
        HOSE_NOZZLE = Registry.register(Registries.ITEM, new Identifier(TravelersBackpack.MODID, "hose_nozzle"), new Item(new FabricItemSettings()));
    }

    public static void addBackpacksToList()
    {
        BACKPACKS.add(STANDARD_TRAVELERS_BACKPACK);
        BACKPACKS.add(NETHERITE_TRAVELERS_BACKPACK);
        BACKPACKS.add(DIAMOND_TRAVELERS_BACKPACK);
        BACKPACKS.add(GOLD_TRAVELERS_BACKPACK);
        BACKPACKS.add(EMERALD_TRAVELERS_BACKPACK);
        BACKPACKS.add(IRON_TRAVELERS_BACKPACK);
        BACKPACKS.add(LAPIS_TRAVELERS_BACKPACK);
        BACKPACKS.add(REDSTONE_TRAVELERS_BACKPACK);
        BACKPACKS.add(COAL_TRAVELERS_BACKPACK);

        BACKPACKS.add(QUARTZ_TRAVELERS_BACKPACK);
        BACKPACKS.add(BOOKSHELF_TRAVELERS_BACKPACK);
        BACKPACKS.add(END_TRAVELERS_BACKPACK);
        BACKPACKS.add(NETHER_TRAVELERS_BACKPACK);
        BACKPACKS.add(SANDSTONE_TRAVELERS_BACKPACK);
        BACKPACKS.add(SNOW_TRAVELERS_BACKPACK);
        BACKPACKS.add(SPONGE_TRAVELERS_BACKPACK);

        BACKPACKS.add(CAKE_TRAVELERS_BACKPACK);

        BACKPACKS.add(CACTUS_TRAVELERS_BACKPACK);
        BACKPACKS.add(HAY_TRAVELERS_BACKPACK);
        BACKPACKS.add(MELON_TRAVELERS_BACKPACK);
        BACKPACKS.add(PUMPKIN_TRAVELERS_BACKPACK);

        BACKPACKS.add(CREEPER_TRAVELERS_BACKPACK);
        BACKPACKS.add(DRAGON_TRAVELERS_BACKPACK);
        BACKPACKS.add(ENDERMAN_TRAVELERS_BACKPACK);
        BACKPACKS.add(BLAZE_TRAVELERS_BACKPACK);
        BACKPACKS.add(GHAST_TRAVELERS_BACKPACK);
        BACKPACKS.add(MAGMA_CUBE_TRAVELERS_BACKPACK);
        BACKPACKS.add(SKELETON_TRAVELERS_BACKPACK);
        BACKPACKS.add(SPIDER_TRAVELERS_BACKPACK);
        BACKPACKS.add(WITHER_TRAVELERS_BACKPACK);

        BACKPACKS.add(BAT_TRAVELERS_BACKPACK);
        BACKPACKS.add(BEE_TRAVELERS_BACKPACK);
        BACKPACKS.add(WOLF_TRAVELERS_BACKPACK);
        BACKPACKS.add(FOX_TRAVELERS_BACKPACK);
        BACKPACKS.add(OCELOT_TRAVELERS_BACKPACK);
        BACKPACKS.add(HORSE_TRAVELERS_BACKPACK);
        BACKPACKS.add(COW_TRAVELERS_BACKPACK);
        BACKPACKS.add(PIG_TRAVELERS_BACKPACK);
        BACKPACKS.add(SHEEP_TRAVELERS_BACKPACK);
        BACKPACKS.add(CHICKEN_TRAVELERS_BACKPACK);
        BACKPACKS.add(SQUID_TRAVELERS_BACKPACK);
        BACKPACKS.add(VILLAGER_TRAVELERS_BACKPACK);
        BACKPACKS.add(IRON_GOLEM_TRAVELERS_BACKPACK);

      /*  COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(STANDARD_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(DIAMOND_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(GOLD_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(EMERALD_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(IRON_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(LAPIS_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(REDSTONE_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(COAL_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(BOOKSHELF_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SANDSTONE_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SNOW_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SPONGE_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(CAKE_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(CACTUS_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(HAY_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(MELON_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(PUMPKIN_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(CREEPER_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(ENDERMAN_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SKELETON_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SPIDER_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(BEE_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(WOLF_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(FOX_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(OCELOT_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(HORSE_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(COW_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(PIG_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SHEEP_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(CHICKEN_TRAVELERS_BACKPACK);
        COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.add(SQUID_TRAVELERS_BACKPACK);

        COMPATIBLE_NETHER_BACKPACK_ENTRIES.add(QUARTZ_TRAVELERS_BACKPACK);
        COMPATIBLE_NETHER_BACKPACK_ENTRIES.add(NETHER_TRAVELERS_BACKPACK);
        COMPATIBLE_NETHER_BACKPACK_ENTRIES.add(BLAZE_TRAVELERS_BACKPACK);
        COMPATIBLE_NETHER_BACKPACK_ENTRIES.add(GHAST_TRAVELERS_BACKPACK);
        COMPATIBLE_NETHER_BACKPACK_ENTRIES.add(MAGMA_CUBE_TRAVELERS_BACKPACK);
        COMPATIBLE_NETHER_BACKPACK_ENTRIES.add(WITHER_TRAVELERS_BACKPACK); */
    }

    public static final List<Item> COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES = new ArrayList<>();
    public static final List<Item> COMPATIBLE_NETHER_BACKPACK_ENTRIES = new ArrayList<>();
}