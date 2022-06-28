package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
    public static final RegistryObject<Item> END_TRAVELERS_BACKPACK = ITEMS.register("end", () -> new TravelersBackpackItem(ModBlocks.END_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> NETHER_TRAVELERS_BACKPACK = ITEMS.register("nether", () -> new TravelersBackpackItem(ModBlocks.NETHER_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SANDSTONE_TRAVELERS_BACKPACK = ITEMS.register("sandstone", () -> new TravelersBackpackItem(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SNOW_TRAVELERS_BACKPACK = ITEMS.register("snow", () -> new TravelersBackpackItem(ModBlocks.SNOW_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SPONGE_TRAVELERS_BACKPACK = ITEMS.register("sponge", () -> new TravelersBackpackItem(ModBlocks.SPONGE_TRAVELERS_BACKPACK.get()));

    //Food
    public static final RegistryObject<Item> CAKE_TRAVELERS_BACKPACK = ITEMS.register("cake", () -> new TravelersBackpackItem(ModBlocks.CAKE_TRAVELERS_BACKPACK.get()));

    //Plants
    public static final RegistryObject<Item> CACTUS_TRAVELERS_BACKPACK = ITEMS.register("cactus", () -> new TravelersBackpackItem(ModBlocks.CACTUS_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> HAY_TRAVELERS_BACKPACK = ITEMS.register("hay", () -> new TravelersBackpackItem(ModBlocks.HAY_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> MELON_TRAVELERS_BACKPACK = ITEMS.register("melon", () -> new TravelersBackpackItem(ModBlocks.MELON_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> PUMPKIN_TRAVELERS_BACKPACK = ITEMS.register("pumpkin", () -> new TravelersBackpackItem(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get()));

    //Mobs
    public static final RegistryObject<Item> CREEPER_TRAVELERS_BACKPACK = ITEMS.register("creeper", () -> new TravelersBackpackItem(ModBlocks.CREEPER_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> DRAGON_TRAVELERS_BACKPACK = ITEMS.register("dragon", () -> new TravelersBackpackItem(ModBlocks.DRAGON_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> ENDERMAN_TRAVELERS_BACKPACK = ITEMS.register("enderman", () -> new TravelersBackpackItem(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> BLAZE_TRAVELERS_BACKPACK = ITEMS.register("blaze", () -> new TravelersBackpackItem(ModBlocks.BLAZE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> GHAST_TRAVELERS_BACKPACK = ITEMS.register("ghast", () -> new TravelersBackpackItem(ModBlocks.GHAST_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> MAGMA_CUBE_TRAVELERS_BACKPACK = ITEMS.register("magma_cube", () -> new TravelersBackpackItem(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SKELETON_TRAVELERS_BACKPACK = ITEMS.register("skeleton", () -> new TravelersBackpackItem(ModBlocks.SKELETON_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SPIDER_TRAVELERS_BACKPACK = ITEMS.register("spider", () -> new TravelersBackpackItem(ModBlocks.SPIDER_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> WITHER_TRAVELERS_BACKPACK = ITEMS.register("wither", () -> new TravelersBackpackItem(ModBlocks.WITHER_TRAVELERS_BACKPACK.get()));

    //Friendly Mobs
    public static final RegistryObject<Item> BAT_TRAVELERS_BACKPACK = ITEMS.register("bat", () -> new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> BEE_TRAVELERS_BACKPACK = ITEMS.register("bee", () -> new TravelersBackpackItem(ModBlocks.BEE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> WOLF_TRAVELERS_BACKPACK = ITEMS.register("wolf", () -> new TravelersBackpackItem(ModBlocks.WOLF_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> FOX_TRAVELERS_BACKPACK = ITEMS.register("fox", () -> new TravelersBackpackItem(ModBlocks.FOX_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> OCELOT_TRAVELERS_BACKPACK = ITEMS.register("ocelot", () -> new TravelersBackpackItem(ModBlocks.OCELOT_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> HORSE_TRAVELERS_BACKPACK = ITEMS.register("horse", () -> new TravelersBackpackItem(ModBlocks.HORSE_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> COW_TRAVELERS_BACKPACK = ITEMS.register("cow", () -> new TravelersBackpackItem(ModBlocks.COW_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> PIG_TRAVELERS_BACKPACK = ITEMS.register("pig", () -> new TravelersBackpackItem(ModBlocks.PIG_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SHEEP_TRAVELERS_BACKPACK = ITEMS.register("sheep", () -> new TravelersBackpackItem(ModBlocks.SHEEP_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> CHICKEN_TRAVELERS_BACKPACK = ITEMS.register("chicken", () -> new TravelersBackpackItem(ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SQUID_TRAVELERS_BACKPACK = ITEMS.register("squid", () -> new TravelersBackpackItem(ModBlocks.SQUID_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> VILLAGER_TRAVELERS_BACKPACK = ITEMS.register("villager", () -> new TravelersBackpackItem(ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> IRON_GOLEM_TRAVELERS_BACKPACK = ITEMS.register("iron_golem", () -> new TravelersBackpackItem(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get()));

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
        add(END_TRAVELERS_BACKPACK);
        add(NETHER_TRAVELERS_BACKPACK);
        add(SANDSTONE_TRAVELERS_BACKPACK);
        add(SNOW_TRAVELERS_BACKPACK);
        add(SPONGE_TRAVELERS_BACKPACK);

        add(CAKE_TRAVELERS_BACKPACK);

        add(CACTUS_TRAVELERS_BACKPACK);
        add(HAY_TRAVELERS_BACKPACK);
        add(MELON_TRAVELERS_BACKPACK);
        add(PUMPKIN_TRAVELERS_BACKPACK);

        add(CREEPER_TRAVELERS_BACKPACK);
        add(DRAGON_TRAVELERS_BACKPACK);
        add(ENDERMAN_TRAVELERS_BACKPACK);
        add(BLAZE_TRAVELERS_BACKPACK);
        add(GHAST_TRAVELERS_BACKPACK);
        add(MAGMA_CUBE_TRAVELERS_BACKPACK);
        add(SKELETON_TRAVELERS_BACKPACK);
        add(SPIDER_TRAVELERS_BACKPACK);
        add(WITHER_TRAVELERS_BACKPACK);

        add(BAT_TRAVELERS_BACKPACK);
        add(BEE_TRAVELERS_BACKPACK);
        add(WOLF_TRAVELERS_BACKPACK);
        add(FOX_TRAVELERS_BACKPACK);
        add(OCELOT_TRAVELERS_BACKPACK);
        add(HORSE_TRAVELERS_BACKPACK);
        add(COW_TRAVELERS_BACKPACK);
        add(PIG_TRAVELERS_BACKPACK);
        add(SHEEP_TRAVELERS_BACKPACK);
        add(CHICKEN_TRAVELERS_BACKPACK);
        add(SQUID_TRAVELERS_BACKPACK);
        add(VILLAGER_TRAVELERS_BACKPACK);
        add(IRON_GOLEM_TRAVELERS_BACKPACK);
    }

    public static void add(Supplier<Item> itemSupplier)
    {
        BACKPACKS.add(itemSupplier.get());
    }
}
