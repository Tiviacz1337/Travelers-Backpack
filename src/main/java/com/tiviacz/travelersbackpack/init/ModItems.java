package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TravelersBackpack.MODID);
    public static final List<Item> BACKPACKS = new ArrayList<>();


    //Standard
    public static final RegistryObject<Item> STANDARD_TRAVELERS_BACKPACK = ITEMS.register("standard", () -> new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK.get()));

    //Blocks
    public static final RegistryObject<Item> NETHERITE_TRAVELERS_BACKPACK = ITEMS.register("netherite", () -> new TravelersBackpackItem(ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> DIAMOND_TRAVELERS_BACKPACK = ITEMS.register("diamond", () -> new TravelersBackpackItem(ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> GOLD_TRAVELERS_BACKPACK = ITEMS.register("gold", () -> new TravelersBackpackItem(ModBlocks.GOLD_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> EMERALD_TRAVELERS_BACKPACK = ITEMS.register("emerald", () -> new TravelersBackpackItem(ModBlocks.EMERALD_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> IRON_TRAVELERS_BACKPACK = ITEMS.register("iron", () -> new TravelersBackpackItem(ModBlocks.IRON_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> LAPIS_TRAVELERS_BACKPACK = ITEMS.register("lapis", () -> new TravelersBackpackItem(ModBlocks.LAPIS_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> REDSTONE_TRAVELERS_BACKPACK = ITEMS.register("redstone", () -> new TravelersBackpackItem(ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> COAL_TRAVELERS_BACKPACK = ITEMS.register("coal", () -> new TravelersBackpackItem(ModBlocks.COAL_TRAVELERS_BACKPACK.get()));

    public static final RegistryObject<Item> QUARTZ_TRAVELERS_BACKPACK = ITEMS.register("quartz", () -> new TravelersBackpackItem(ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> BOOKSHELF_TRAVELERS_BACKPACK = ITEMS.register("bookshelf", () -> new TravelersBackpackItem(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get()));

    //Plants
    public static final RegistryObject<Item> HAY_TRAVELERS_BACKPACK = ITEMS.register("hay", () -> new TravelersBackpackItem(ModBlocks.HAY_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> MELON_TRAVELERS_BACKPACK = ITEMS.register("melon", () -> new TravelersBackpackItem(ModBlocks.MELON_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> PUMPKIN_TRAVELERS_BACKPACK = ITEMS.register("pumpkin", () -> new TravelersBackpackItem(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get()));

    //Mobs
    public static final RegistryObject<Item> BLAZE_TRAVELERS_BACKPACK = ITEMS.register("blaze", () -> new TravelersBackpackItem(ModBlocks.BLAZE_TRAVELERS_BACKPACK.get()));

    //Friendly Mobs
    public static final RegistryObject<Item> BAT_TRAVELERS_BACKPACK = ITEMS.register("bat", () -> new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> WOLF_TRAVELERS_BACKPACK = ITEMS.register("wolf", () -> new TravelersBackpackItem(ModBlocks.WOLF_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> FOX_TRAVELERS_BACKPACK = ITEMS.register("fox", () -> new TravelersBackpackItem(ModBlocks.FOX_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> OCELOT_TRAVELERS_BACKPACK = ITEMS.register("ocelot", () -> new TravelersBackpackItem(ModBlocks.OCELOT_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> COW_TRAVELERS_BACKPACK = ITEMS.register("cow", () -> new TravelersBackpackItem(ModBlocks.COW_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> PIG_TRAVELERS_BACKPACK = ITEMS.register("pig", () -> new TravelersBackpackItem(ModBlocks.PIG_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> CHICKEN_TRAVELERS_BACKPACK = ITEMS.register("chicken", () -> new TravelersBackpackItem(ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> VILLAGER_TRAVELERS_BACKPACK = ITEMS.register("villager", () -> new TravelersBackpackItem(ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get()));

    //public static final RegistryObject<Item> CRYING_OBSIDIAN_TRAVELERS_BACKPACK = ITEMS.register("crying_obsidian", () -> new TravelersBackpackItem(ModBlocks.CRYING_OBSIDIAN_TRAVELERS_BACKPACK.get()));
    //public static final RegistryObject<BlockItem> BAT_TRAVELERS_BACKPACK = ITEMS.register("bat", () -> new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK.get()));

    //Other Items
    public static final RegistryObject<Item> SLEEPING_BAG = ITEMS.register("sleeping_bag", () -> new SleepingBagItem(new Item.Properties().tab(Reference.TAB_TRAVELERS_BACKPACK)));
    public static final RegistryObject<Item> BACKPACK_TANK = ITEMS.register("backpack_tank", () -> new Item(new Item.Properties().tab(Reference.TAB_TRAVELERS_BACKPACK).stacksTo(16)));
    public static final RegistryObject<Item> HOSE = ITEMS.register("hose", () -> new HoseItem(new Item.Properties().tab(Reference.TAB_TRAVELERS_BACKPACK).stacksTo(1)));
    public static final RegistryObject<Item> HOSE_NOZZLE = ITEMS.register("hose_nozzle", () -> new Item(new Item.Properties().tab(Reference.TAB_TRAVELERS_BACKPACK)));

    public static void addBackpacksToList()
    {
        add(STANDARD_TRAVELERS_BACKPACK);
        add(NETHERITE_TRAVELERS_BACKPACK);
        add(DIAMOND_TRAVELERS_BACKPACK);
        add(GOLD_TRAVELERS_BACKPACK);
        add(EMERALD_TRAVELERS_BACKPACK);
        add(IRON_TRAVELERS_BACKPACK);
        add(LAPIS_TRAVELERS_BACKPACK);
        add(REDSTONE_TRAVELERS_BACKPACK);
        add(COAL_TRAVELERS_BACKPACK);

        //add(CRYING_OBSIDIAN_TRAVELERS_BACKPACK);
        add(QUARTZ_TRAVELERS_BACKPACK);
        add(BOOKSHELF_TRAVELERS_BACKPACK);

        add(HAY_TRAVELERS_BACKPACK);
        add(MELON_TRAVELERS_BACKPACK);
        add(PUMPKIN_TRAVELERS_BACKPACK);

        add(BLAZE_TRAVELERS_BACKPACK);

        add(BAT_TRAVELERS_BACKPACK);
        add(WOLF_TRAVELERS_BACKPACK);
        add(FOX_TRAVELERS_BACKPACK);
        add(OCELOT_TRAVELERS_BACKPACK);
        add(COW_TRAVELERS_BACKPACK);
        add(PIG_TRAVELERS_BACKPACK);
        add(CHICKEN_TRAVELERS_BACKPACK);
        add(VILLAGER_TRAVELERS_BACKPACK);
    }

    public static void add(Supplier<Item> itemSupplier)
    {
        BACKPACKS.add(itemSupplier.get());
    }
}
