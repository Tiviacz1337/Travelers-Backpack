package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.UpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TravelersBackpack.MODID);

    //Standard
    public static final RegistryObject<Item> STANDARD_TRAVELERS_BACKPACK = ITEMS.register("standard", () -> new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK.get(), "standard"));

    //Blocks
    public static final RegistryObject<Item> NETHERITE_TRAVELERS_BACKPACK = ITEMS.register("netherite", () -> new TravelersBackpackItem(ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get(), "netherite"));
    public static final RegistryObject<Item> DIAMOND_TRAVELERS_BACKPACK = ITEMS.register("diamond", () -> new TravelersBackpackItem(ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get(), "diamond"));
    public static final RegistryObject<Item> GOLD_TRAVELERS_BACKPACK = ITEMS.register("gold", () -> new TravelersBackpackItem(ModBlocks.GOLD_TRAVELERS_BACKPACK.get(), "gold"));
    public static final RegistryObject<Item> EMERALD_TRAVELERS_BACKPACK = ITEMS.register("emerald", () -> new TravelersBackpackItem(ModBlocks.EMERALD_TRAVELERS_BACKPACK.get(), "emerald"));
    public static final RegistryObject<Item> IRON_TRAVELERS_BACKPACK = ITEMS.register("iron", () -> new TravelersBackpackItem(ModBlocks.IRON_TRAVELERS_BACKPACK.get(), "iron"));
    public static final RegistryObject<Item> LAPIS_TRAVELERS_BACKPACK = ITEMS.register("lapis", () -> new TravelersBackpackItem(ModBlocks.LAPIS_TRAVELERS_BACKPACK.get(), "lapis"));
    public static final RegistryObject<Item> REDSTONE_TRAVELERS_BACKPACK = ITEMS.register("redstone", () -> new TravelersBackpackItem(ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get(), "redstone"));
    public static final RegistryObject<Item> COAL_TRAVELERS_BACKPACK = ITEMS.register("coal", () -> new TravelersBackpackItem(ModBlocks.COAL_TRAVELERS_BACKPACK.get(), "coal"));

    public static final RegistryObject<Item> QUARTZ_TRAVELERS_BACKPACK = ITEMS.register("quartz", () -> new TravelersBackpackItem(ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get(), "quartz"));
    public static final RegistryObject<Item> BOOKSHELF_TRAVELERS_BACKPACK = ITEMS.register("bookshelf", () -> new TravelersBackpackItem(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get(), "bookshelf"));
    public static final RegistryObject<Item> END_TRAVELERS_BACKPACK = ITEMS.register("end", () -> new TravelersBackpackItem(ModBlocks.END_TRAVELERS_BACKPACK.get(), "end"));
    public static final RegistryObject<Item> NETHER_TRAVELERS_BACKPACK = ITEMS.register("nether", () -> new TravelersBackpackItem(ModBlocks.NETHER_TRAVELERS_BACKPACK.get(), "nether"));
    public static final RegistryObject<Item> SANDSTONE_TRAVELERS_BACKPACK = ITEMS.register("sandstone", () -> new TravelersBackpackItem(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get(), "sandstone"));
    public static final RegistryObject<Item> SNOW_TRAVELERS_BACKPACK = ITEMS.register("snow", () -> new TravelersBackpackItem(ModBlocks.SNOW_TRAVELERS_BACKPACK.get(), "snow"));
    public static final RegistryObject<Item> SPONGE_TRAVELERS_BACKPACK = ITEMS.register("sponge", () -> new TravelersBackpackItem(ModBlocks.SPONGE_TRAVELERS_BACKPACK.get(), "sponge"));

    //Food
    public static final RegistryObject<Item> CAKE_TRAVELERS_BACKPACK = ITEMS.register("cake", () -> new TravelersBackpackItem(ModBlocks.CAKE_TRAVELERS_BACKPACK.get(), "cake"));

    //Plants
    public static final RegistryObject<Item> CACTUS_TRAVELERS_BACKPACK = ITEMS.register("cactus", () -> new TravelersBackpackItem(ModBlocks.CACTUS_TRAVELERS_BACKPACK.get(), "cactus"));
    public static final RegistryObject<Item> HAY_TRAVELERS_BACKPACK = ITEMS.register("hay", () -> new TravelersBackpackItem(ModBlocks.HAY_TRAVELERS_BACKPACK.get(), "hay"));
    public static final RegistryObject<Item> MELON_TRAVELERS_BACKPACK = ITEMS.register("melon", () -> new TravelersBackpackItem(ModBlocks.MELON_TRAVELERS_BACKPACK.get(), "melon"));
    public static final RegistryObject<Item> PUMPKIN_TRAVELERS_BACKPACK = ITEMS.register("pumpkin", () -> new TravelersBackpackItem(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get(), "pumpkin"));

    //Mobs
    public static final RegistryObject<Item> CREEPER_TRAVELERS_BACKPACK = ITEMS.register("creeper", () -> new TravelersBackpackItem(ModBlocks.CREEPER_TRAVELERS_BACKPACK.get(), "creeper"));
    public static final RegistryObject<Item> DRAGON_TRAVELERS_BACKPACK = ITEMS.register("dragon", () -> new TravelersBackpackItem(ModBlocks.DRAGON_TRAVELERS_BACKPACK.get(), "dragon"));
    public static final RegistryObject<Item> ENDERMAN_TRAVELERS_BACKPACK = ITEMS.register("enderman", () -> new TravelersBackpackItem(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get(), "enderman"));
    public static final RegistryObject<Item> BLAZE_TRAVELERS_BACKPACK = ITEMS.register("blaze", () -> new TravelersBackpackItem(ModBlocks.BLAZE_TRAVELERS_BACKPACK.get(), "blaze"));
    public static final RegistryObject<Item> GHAST_TRAVELERS_BACKPACK = ITEMS.register("ghast", () -> new TravelersBackpackItem(ModBlocks.GHAST_TRAVELERS_BACKPACK.get(), "ghast"));
    public static final RegistryObject<Item> MAGMA_CUBE_TRAVELERS_BACKPACK = ITEMS.register("magma_cube", () -> new TravelersBackpackItem(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get(), "magma_cube"));
    public static final RegistryObject<Item> SKELETON_TRAVELERS_BACKPACK = ITEMS.register("skeleton", () -> new TravelersBackpackItem(ModBlocks.SKELETON_TRAVELERS_BACKPACK.get(), "skeleton"));
    public static final RegistryObject<Item> SPIDER_TRAVELERS_BACKPACK = ITEMS.register("spider", () -> new TravelersBackpackItem(ModBlocks.SPIDER_TRAVELERS_BACKPACK.get(), "spider"));
    public static final RegistryObject<Item> WITHER_TRAVELERS_BACKPACK = ITEMS.register("wither", () -> new TravelersBackpackItem(ModBlocks.WITHER_TRAVELERS_BACKPACK.get(), "wither"));

    //Friendly Mobs
    public static final RegistryObject<Item> BAT_TRAVELERS_BACKPACK = ITEMS.register("bat", () -> new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK.get(), "bat"));
    public static final RegistryObject<Item> BEE_TRAVELERS_BACKPACK = ITEMS.register("bee", () -> new TravelersBackpackItem(ModBlocks.BEE_TRAVELERS_BACKPACK.get(), "bee"));
    public static final RegistryObject<Item> WOLF_TRAVELERS_BACKPACK = ITEMS.register("wolf", () -> new TravelersBackpackItem(ModBlocks.WOLF_TRAVELERS_BACKPACK.get(), "wolf"));
    public static final RegistryObject<Item> FOX_TRAVELERS_BACKPACK = ITEMS.register("fox", () -> new TravelersBackpackItem(ModBlocks.FOX_TRAVELERS_BACKPACK.get(), "fox"));
    public static final RegistryObject<Item> OCELOT_TRAVELERS_BACKPACK = ITEMS.register("ocelot", () -> new TravelersBackpackItem(ModBlocks.OCELOT_TRAVELERS_BACKPACK.get(), "ocelot"));
    public static final RegistryObject<Item> HORSE_TRAVELERS_BACKPACK = ITEMS.register("horse", () -> new TravelersBackpackItem(ModBlocks.HORSE_TRAVELERS_BACKPACK.get(), "horse"));
    public static final RegistryObject<Item> COW_TRAVELERS_BACKPACK = ITEMS.register("cow", () -> new TravelersBackpackItem(ModBlocks.COW_TRAVELERS_BACKPACK.get(), "cow"));
    public static final RegistryObject<Item> PIG_TRAVELERS_BACKPACK = ITEMS.register("pig", () -> new TravelersBackpackItem(ModBlocks.PIG_TRAVELERS_BACKPACK.get(), "pig"));
    public static final RegistryObject<Item> SHEEP_TRAVELERS_BACKPACK = ITEMS.register("sheep", () -> new TravelersBackpackItem(ModBlocks.SHEEP_TRAVELERS_BACKPACK.get(), "sheep"));
    public static final RegistryObject<Item> CHICKEN_TRAVELERS_BACKPACK = ITEMS.register("chicken", () -> new TravelersBackpackItem(ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get(), "chicken"));
    public static final RegistryObject<Item> SQUID_TRAVELERS_BACKPACK = ITEMS.register("squid", () -> new TravelersBackpackItem(ModBlocks.SQUID_TRAVELERS_BACKPACK.get(), "squid"));
    public static final RegistryObject<Item> VILLAGER_TRAVELERS_BACKPACK = ITEMS.register("villager", () -> new TravelersBackpackItem(ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get(), "villager"));
    public static final RegistryObject<Item> IRON_GOLEM_TRAVELERS_BACKPACK = ITEMS.register("iron_golem", () -> new TravelersBackpackItem(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get(), "iron_golem"));

    //Other Items
    public static final RegistryObject<Item> WHITE_SLEEPING_BAG = ITEMS.register("white_sleeping_bag", () -> new SleepingBagItem(ModBlocks.WHITE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_SLEEPING_BAG = ITEMS.register("orange_sleeping_bag", () -> new SleepingBagItem(ModBlocks.ORANGE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAGENTA_SLEEPING_BAG = ITEMS.register("magenta_sleeping_bag", () -> new SleepingBagItem(ModBlocks.MAGENTA_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_BLUE_SLEEPING_BAG = ITEMS.register("light_blue_sleeping_bag", () -> new SleepingBagItem(ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_SLEEPING_BAG = ITEMS.register("yellow_sleeping_bag", () -> new SleepingBagItem(ModBlocks.YELLOW_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIME_SLEEPING_BAG = ITEMS.register("lime_sleeping_bag", () -> new SleepingBagItem(ModBlocks.LIME_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> PINK_SLEEPING_BAG = ITEMS.register("pink_sleeping_bag", () -> new SleepingBagItem(ModBlocks.PINK_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> GRAY_SLEEPING_BAG = ITEMS.register("gray_sleeping_bag", () -> new SleepingBagItem(ModBlocks.GRAY_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_GRAY_SLEEPING_BAG = ITEMS.register("light_gray_sleeping_bag", () -> new SleepingBagItem(ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> CYAN_SLEEPING_BAG = ITEMS.register("cyan_sleeping_bag", () -> new SleepingBagItem(ModBlocks.CYAN_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> PURPLE_SLEEPING_BAG = ITEMS.register("purple_sleeping_bag", () -> new SleepingBagItem(ModBlocks.PURPLE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUE_SLEEPING_BAG = ITEMS.register("blue_sleeping_bag", () -> new SleepingBagItem(ModBlocks.BLUE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROWN_SLEEPING_BAG = ITEMS.register("brown_sleeping_bag", () -> new SleepingBagItem(ModBlocks.BROWN_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREEN_SLEEPING_BAG = ITEMS.register("green_sleeping_bag", () -> new SleepingBagItem(ModBlocks.GREEN_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_SLEEPING_BAG = ITEMS.register("red_sleeping_bag", () -> new SleepingBagItem(ModBlocks.RED_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLACK_SLEEPING_BAG = ITEMS.register("black_sleeping_bag", () -> new SleepingBagItem(ModBlocks.BLACK_SLEEPING_BAG.get(), new Item.Properties()));
    public static final RegistryObject<Item> BACKPACK_TANK = ITEMS.register("backpack_tank", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HOSE = ITEMS.register("hose", () -> new HoseItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HOSE_NOZZLE = ITEMS.register("hose_nozzle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLANK_UPGRADE = ITEMS.register("blank_upgrade", () -> new UpgradeItem(new Item.Properties(), UpgradeItem.Upgrade.BLANK_UPGRADE));
    public static final RegistryObject<Item> IRON_TIER_UPGRADE = ITEMS.register("iron_tier_upgrade", () -> new UpgradeItem(new Item.Properties().stacksTo(16), UpgradeItem.Upgrade.IRON_TIER_UPGRADE));
    public static final RegistryObject<Item> GOLD_TIER_UPGRADE = ITEMS.register("gold_tier_upgrade", () -> new UpgradeItem(new Item.Properties().stacksTo(16), UpgradeItem.Upgrade.GOLD_TIER_UPGRADE));
    public static final RegistryObject<Item> DIAMOND_TIER_UPGRADE = ITEMS.register("diamond_tier_upgrade", () -> new UpgradeItem(new Item.Properties().stacksTo(16), UpgradeItem.Upgrade.DIAMOND_TIER_UPGRADE));
    public static final RegistryObject<Item> NETHERITE_TIER_UPGRADE = ITEMS.register("netherite_tier_upgrade", () -> new UpgradeItem(new Item.Properties().stacksTo(16), UpgradeItem.Upgrade.NETHERITE_TIER_UPGRADE));
    public static final RegistryObject<Item> CRAFTING_UPGRADE = ITEMS.register("crafting_upgrade", () -> new UpgradeItem(new Item.Properties().stacksTo(16), UpgradeItem.Upgrade.CRAFTING_UPGRADE));

    public static final List<Item> COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES = new ArrayList<>();
    public static final List<Item> COMPATIBLE_NETHER_BACKPACK_ENTRIES = new ArrayList<>();
}