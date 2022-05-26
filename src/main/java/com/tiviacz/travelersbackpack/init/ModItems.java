package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.TravelersBackpackItemGroup;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ModItems
{
    public static final List<Item> BACKPACKS = new ArrayList<>();

    //Backpacks
    public static Item STANDARD_TRAVELERS_BACKPACK;

    public static Item NETHERITE_TRAVELERS_BACKPACK;
    public static Item DIAMOND_TRAVELERS_BACKPACK;
    public static Item GOLD_TRAVELERS_BACKPACK;
    public static Item EMERALD_TRAVELERS_BACKPACK;
    public static Item IRON_TRAVELERS_BACKPACK;
    public static Item LAPIS_TRAVELERS_BACKPACK;
    public static Item REDSTONE_TRAVELERS_BACKPACK;
    public static Item COAL_TRAVELERS_BACKPACK;

    public static Item QUARTZ_TRAVELERS_BACKPACK;
    public static Item BOOKSHELF_TRAVELERS_BACKPACK;

    public static Item HAY_TRAVELERS_BACKPACK;
    public static Item MELON_TRAVELERS_BACKPACK;
    public static Item PUMPKIN_TRAVELERS_BACKPACK;

    public static Item BLAZE_TRAVELERS_BACKPACK;

    public static Item BAT_TRAVELERS_BACKPACK;
    public static Item WOLF_TRAVELERS_BACKPACK;
    public static Item FOX_TRAVELERS_BACKPACK;
    public static Item OCELOT_TRAVELERS_BACKPACK;
    public static Item COW_TRAVELERS_BACKPACK;
    public static Item PIG_TRAVELERS_BACKPACK;
    public static Item CHICKEN_TRAVELERS_BACKPACK;
    public static Item VILLAGER_TRAVELERS_BACKPACK;

    //Other
    public static Item SLEEPING_BAG;
    public static Item BACKPACK_TANK;
    public static Item HOSE;
    public static Item HOSE_NOZZLE;

    public static void init()
    {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "standard"), new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "netherite"), new TravelersBackpackItem(ModBlocks.NETHERITE_TRAVELERS_BACKPACK));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "diamond"), new TravelersBackpackItem(ModBlocks.DIAMOND_TRAVELERS_BACKPACK));
        GOLD_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "gold"), new TravelersBackpackItem(ModBlocks.GOLD_TRAVELERS_BACKPACK));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "emerald"), new TravelersBackpackItem(ModBlocks.EMERALD_TRAVELERS_BACKPACK));
        IRON_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "iron"), new TravelersBackpackItem(ModBlocks.IRON_TRAVELERS_BACKPACK));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "lapis"), new TravelersBackpackItem(ModBlocks.LAPIS_TRAVELERS_BACKPACK));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "redstone"), new TravelersBackpackItem(ModBlocks.REDSTONE_TRAVELERS_BACKPACK));
        COAL_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "coal"), new TravelersBackpackItem(ModBlocks.COAL_TRAVELERS_BACKPACK));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "quartz"), new TravelersBackpackItem(ModBlocks.QUARTZ_TRAVELERS_BACKPACK));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackItem(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK));

        HAY_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "hay"), new TravelersBackpackItem(ModBlocks.HAY_TRAVELERS_BACKPACK));
        MELON_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "melon"), new TravelersBackpackItem(ModBlocks.MELON_TRAVELERS_BACKPACK));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackItem(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK));

        BLAZE_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "blaze"), new TravelersBackpackItem(ModBlocks.BLAZE_TRAVELERS_BACKPACK));

        BAT_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "bat"), new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK));
        WOLF_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "wolf"), new TravelersBackpackItem(ModBlocks.WOLF_TRAVELERS_BACKPACK));
        FOX_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "fox"), new TravelersBackpackItem(ModBlocks.FOX_TRAVELERS_BACKPACK));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackItem(ModBlocks.OCELOT_TRAVELERS_BACKPACK));
        COW_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "cow"), new TravelersBackpackItem(ModBlocks.COW_TRAVELERS_BACKPACK));
        PIG_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "pig"), new TravelersBackpackItem(ModBlocks.PIG_TRAVELERS_BACKPACK));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "chicken"), new TravelersBackpackItem(ModBlocks.CHICKEN_TRAVELERS_BACKPACK));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "villager"), new TravelersBackpackItem(ModBlocks.VILLAGER_TRAVELERS_BACKPACK));

        SLEEPING_BAG = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "sleeping_bag"), new SleepingBagItem(new FabricItemSettings().group(TravelersBackpackItemGroup.INSTANCE)));
        BACKPACK_TANK = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "backpack_tank"), new Item(new FabricItemSettings().group(TravelersBackpackItemGroup.INSTANCE).maxCount(16)));
        HOSE_NOZZLE = Registry.register(Registry.ITEM, new Identifier(TravelersBackpack.MODID, "hose_nozzle"), new Item(new FabricItemSettings().group(TravelersBackpackItemGroup.INSTANCE)));
    }

 /*   private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

    //public static final Item STANDARD_TRAVELERS_BACKPACK = BACKPACKS.add("standard", new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK));

    private static <I extends Item> I BACKPACKS.add(String name, I item) {
        ITEMS.put(new Identifier(TravelersBackpack.MODID, name), item);
        return item;
    }

    public static void register() {
        for (Identifier id : ITEMS.keySet()) {
            Registry.register(Registry.ITEM, id, ITEMS.get(id));
        }
    } */

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

        //BACKPACKS.add(CRYING_OBSIDIAN_TRAVELERS_BACKPACK);
        BACKPACKS.add(QUARTZ_TRAVELERS_BACKPACK);
        BACKPACKS.add(BOOKSHELF_TRAVELERS_BACKPACK);

        BACKPACKS.add(HAY_TRAVELERS_BACKPACK);
        BACKPACKS.add(MELON_TRAVELERS_BACKPACK);
        BACKPACKS.add(PUMPKIN_TRAVELERS_BACKPACK);

        BACKPACKS.add(BLAZE_TRAVELERS_BACKPACK);

        BACKPACKS.add(BAT_TRAVELERS_BACKPACK);
        BACKPACKS.add(WOLF_TRAVELERS_BACKPACK);
        BACKPACKS.add(FOX_TRAVELERS_BACKPACK);
        BACKPACKS.add(OCELOT_TRAVELERS_BACKPACK);
        BACKPACKS.add(COW_TRAVELERS_BACKPACK);
        BACKPACKS.add(PIG_TRAVELERS_BACKPACK);
        BACKPACKS.add(CHICKEN_TRAVELERS_BACKPACK);
        BACKPACKS.add(VILLAGER_TRAVELERS_BACKPACK);
    }
}
