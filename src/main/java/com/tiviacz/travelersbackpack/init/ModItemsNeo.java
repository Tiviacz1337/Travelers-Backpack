package com.tiviacz.travelersbackpack.init;

public class ModItemsNeo { /*
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TravelersBackpack.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TravelersBackpack.MODID);

    //Standard
    public static final DeferredItem<TravelersBackpackItem> STANDARD_TRAVELERS_BACKPACK = ITEMS.register("standard", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.STANDARD_TRAVELERS_BACKPACK.get(), "standard"));

    //Blocks
    public static final DeferredItem<TravelersBackpackItem> NETHERITE_TRAVELERS_BACKPACK = ITEMS.register("netherite", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get(), "netherite"));
    public static final DeferredItem<TravelersBackpackItem> DIAMOND_TRAVELERS_BACKPACK = ITEMS.register("diamond", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get(), "diamond"));
    public static final DeferredItem<TravelersBackpackItem> GOLD_TRAVELERS_BACKPACK = ITEMS.register("gold", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.GOLD_TRAVELERS_BACKPACK.get(), "gold"));
    public static final DeferredItem<TravelersBackpackItem> EMERALD_TRAVELERS_BACKPACK = ITEMS.register("emerald", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.EMERALD_TRAVELERS_BACKPACK.get(), "emerald"));
    public static final DeferredItem<TravelersBackpackItem> IRON_TRAVELERS_BACKPACK = ITEMS.register("iron", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.IRON_TRAVELERS_BACKPACK.get(), "iron"));
    public static final DeferredItem<TravelersBackpackItem> LAPIS_TRAVELERS_BACKPACK = ITEMS.register("lapis", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.LAPIS_TRAVELERS_BACKPACK.get(), "lapis"));
    public static final DeferredItem<TravelersBackpackItem> REDSTONE_TRAVELERS_BACKPACK = ITEMS.register("redstone", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get(), "redstone"));
    public static final DeferredItem<TravelersBackpackItem> COAL_TRAVELERS_BACKPACK = ITEMS.register("coal", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.COAL_TRAVELERS_BACKPACK.get(), "coal"));

    public static final DeferredItem<TravelersBackpackItem> QUARTZ_TRAVELERS_BACKPACK = ITEMS.register("quartz", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get(), "quartz"));
    public static final DeferredItem<TravelersBackpackItem> BOOKSHELF_TRAVELERS_BACKPACK = ITEMS.register("bookshelf", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get(), "bookshelf"));
    public static final DeferredItem<TravelersBackpackItem> END_TRAVELERS_BACKPACK = ITEMS.register("end", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.END_TRAVELERS_BACKPACK.get(), "end"));
    public static final DeferredItem<TravelersBackpackItem> NETHER_TRAVELERS_BACKPACK = ITEMS.register("nether", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.NETHER_TRAVELERS_BACKPACK.get(), "nether"));
    public static final DeferredItem<TravelersBackpackItem> SANDSTONE_TRAVELERS_BACKPACK = ITEMS.register("sandstone", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get(), "sandstone"));
    public static final DeferredItem<TravelersBackpackItem> SNOW_TRAVELERS_BACKPACK = ITEMS.register("snow", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SNOW_TRAVELERS_BACKPACK.get(), "snow"));
    public static final DeferredItem<TravelersBackpackItem> SPONGE_TRAVELERS_BACKPACK = ITEMS.register("sponge", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SPONGE_TRAVELERS_BACKPACK.get(), "sponge"));

    //Food
    public static final DeferredItem<TravelersBackpackItem> CAKE_TRAVELERS_BACKPACK = ITEMS.register("cake", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.CAKE_TRAVELERS_BACKPACK.get(), "cake"));

    //Plants
    public static final DeferredItem<TravelersBackpackItem> CACTUS_TRAVELERS_BACKPACK = ITEMS.register("cactus", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.CACTUS_TRAVELERS_BACKPACK.get(), "cactus"));
    public static final DeferredItem<TravelersBackpackItem> HAY_TRAVELERS_BACKPACK = ITEMS.register("hay", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.HAY_TRAVELERS_BACKPACK.get(), "hay"));
    public static final DeferredItem<TravelersBackpackItem> MELON_TRAVELERS_BACKPACK = ITEMS.register("melon", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.MELON_TRAVELERS_BACKPACK.get(), "melon"));
    public static final DeferredItem<TravelersBackpackItem> PUMPKIN_TRAVELERS_BACKPACK = ITEMS.register("pumpkin", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get(), "pumpkin"));

    //Mobs
    public static final DeferredItem<TravelersBackpackItem> CREEPER_TRAVELERS_BACKPACK = ITEMS.register("creeper", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.CREEPER_TRAVELERS_BACKPACK.get(), "creeper"));
    public static final DeferredItem<TravelersBackpackItem> DRAGON_TRAVELERS_BACKPACK = ITEMS.register("dragon", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.DRAGON_TRAVELERS_BACKPACK.get(), "dragon"));
    public static final DeferredItem<TravelersBackpackItem> ENDERMAN_TRAVELERS_BACKPACK = ITEMS.register("enderman", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get(), "enderman"));
    public static final DeferredItem<TravelersBackpackItem> BLAZE_TRAVELERS_BACKPACK = ITEMS.register("blaze", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.BLAZE_TRAVELERS_BACKPACK.get(), "blaze"));
    public static final DeferredItem<TravelersBackpackItem> GHAST_TRAVELERS_BACKPACK = ITEMS.register("ghast", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.GHAST_TRAVELERS_BACKPACK.get(), "ghast"));
    public static final DeferredItem<TravelersBackpackItem> MAGMA_CUBE_TRAVELERS_BACKPACK = ITEMS.register("magma_cube", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get(), "magma_cube"));
    public static final DeferredItem<TravelersBackpackItem> SKELETON_TRAVELERS_BACKPACK = ITEMS.register("skeleton", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SKELETON_TRAVELERS_BACKPACK.get(), "skeleton"));
    public static final DeferredItem<TravelersBackpackItem> SPIDER_TRAVELERS_BACKPACK = ITEMS.register("spider", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SPIDER_TRAVELERS_BACKPACK.get(), "spider"));
    public static final DeferredItem<TravelersBackpackItem> WITHER_TRAVELERS_BACKPACK = ITEMS.register("wither", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.WITHER_TRAVELERS_BACKPACK.get(), "wither"));
    public static final DeferredItem<TravelersBackpackItem> WARDEN_TRAVELERS_BACKPACK = ITEMS.register("warden", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.WARDEN_TRAVELERS_BACKPACK.get(), "warden"));

    //Friendly Mobs
    public static final DeferredItem<TravelersBackpackItem> BAT_TRAVELERS_BACKPACK = ITEMS.register("bat", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.BAT_TRAVELERS_BACKPACK.get(), "bat"));
    public static final DeferredItem<TravelersBackpackItem> BEE_TRAVELERS_BACKPACK = ITEMS.register("bee", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.BEE_TRAVELERS_BACKPACK.get(), "bee"));
    public static final DeferredItem<TravelersBackpackItem> WOLF_TRAVELERS_BACKPACK = ITEMS.register("wolf", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.WOLF_TRAVELERS_BACKPACK.get(), "wolf"));
    public static final DeferredItem<TravelersBackpackItem> FOX_TRAVELERS_BACKPACK = ITEMS.register("fox", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.FOX_TRAVELERS_BACKPACK.get(), "fox"));
    public static final DeferredItem<TravelersBackpackItem> OCELOT_TRAVELERS_BACKPACK = ITEMS.register("ocelot", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.OCELOT_TRAVELERS_BACKPACK.get(), "ocelot"));
    public static final DeferredItem<TravelersBackpackItem> HORSE_TRAVELERS_BACKPACK = ITEMS.register("horse", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.HORSE_TRAVELERS_BACKPACK.get(), "horse"));
    public static final DeferredItem<TravelersBackpackItem> COW_TRAVELERS_BACKPACK = ITEMS.register("cow", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.COW_TRAVELERS_BACKPACK.get(), "cow"));
    public static final DeferredItem<TravelersBackpackItem> PIG_TRAVELERS_BACKPACK = ITEMS.register("pig", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.PIG_TRAVELERS_BACKPACK.get(), "pig"));
    public static final DeferredItem<TravelersBackpackItem> SHEEP_TRAVELERS_BACKPACK = ITEMS.register("sheep", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SHEEP_TRAVELERS_BACKPACK.get(), "sheep"));
    public static final DeferredItem<TravelersBackpackItem> CHICKEN_TRAVELERS_BACKPACK = ITEMS.register("chicken", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get(), "chicken"));
    public static final DeferredItem<TravelersBackpackItem> SQUID_TRAVELERS_BACKPACK = ITEMS.register("squid", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.SQUID_TRAVELERS_BACKPACK.get(), "squid"));
    public static final DeferredItem<TravelersBackpackItem> VILLAGER_TRAVELERS_BACKPACK = ITEMS.register("villager", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get(), "villager"));
    public static final DeferredItem<TravelersBackpackItem> IRON_GOLEM_TRAVELERS_BACKPACK = ITEMS.register("iron_golem", () -> new TravelersBackpackItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get(), "iron_golem"));

    //Other Items
    public static final DeferredItem<SleepingBagItem> WHITE_SLEEPING_BAG = ITEMS.register("white_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.WHITE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> ORANGE_SLEEPING_BAG = ITEMS.register("orange_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.ORANGE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> MAGENTA_SLEEPING_BAG = ITEMS.register("magenta_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.MAGENTA_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> LIGHT_BLUE_SLEEPING_BAG = ITEMS.register("light_blue_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> YELLOW_SLEEPING_BAG = ITEMS.register("yellow_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.YELLOW_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> LIME_SLEEPING_BAG = ITEMS.register("lime_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.LIME_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> PINK_SLEEPING_BAG = ITEMS.register("pink_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.PINK_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> GRAY_SLEEPING_BAG = ITEMS.register("gray_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.GRAY_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> LIGHT_GRAY_SLEEPING_BAG = ITEMS.register("light_gray_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> CYAN_SLEEPING_BAG = ITEMS.register("cyan_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.CYAN_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> PURPLE_SLEEPING_BAG = ITEMS.register("purple_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.PURPLE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> BLUE_SLEEPING_BAG = ITEMS.register("blue_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.BLUE_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> BROWN_SLEEPING_BAG = ITEMS.register("brown_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.BROWN_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> GREEN_SLEEPING_BAG = ITEMS.register("green_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.GREEN_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> RED_SLEEPING_BAG = ITEMS.register("red_sleeping_bag", () -> new SleepingBagItem(com.tiviacz.travelersbackpackneo.initold.ModBlocks.RED_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<SleepingBagItem> BLACK_SLEEPING_BAG = ITEMS.register("black_sleeping_bag", () -> new SleepingBagItem(ModBlocks.BLACK_SLEEPING_BAG.get(), new Item.Properties()));
    public static final DeferredItem<Item> BACKPACK_TANK = ITEMS.register("backpack_tank", () -> new BackpackTankItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<HoseItem> HOSE = ITEMS.register("hose", () -> new HoseItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> HOSE_NOZZLE = ITEMS.register("hose_nozzle", () -> new Item(new Item.Properties()));
    public static final DeferredItem<TierUpgrade> BLANK_UPGRADE = ITEMS.register("blank_upgrade", () -> new TierUpgrade(new Item.Properties(), TierUpgrade.Upgrade.BLANK_UPGRADE));
    public static final DeferredItem<TierUpgrade> IRON_TIER_UPGRADE = ITEMS.register("iron_tier_upgrade", () -> new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.IRON_TIER_UPGRADE));
    public static final DeferredItem<TierUpgrade> GOLD_TIER_UPGRADE = ITEMS.register("gold_tier_upgrade", () -> new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.GOLD_TIER_UPGRADE));
    public static final DeferredItem<TierUpgrade> DIAMOND_TIER_UPGRADE = ITEMS.register("diamond_tier_upgrade", () -> new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.DIAMOND_TIER_UPGRADE));
    public static final DeferredItem<TierUpgrade> NETHERITE_TIER_UPGRADE = ITEMS.register("netherite_tier_upgrade", () -> new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.NETHERITE_TIER_UPGRADE));
    public static final DeferredItem<TanksUpgradeItem> TANKS_UPGRADE = ITEMS.register("tanks_upgrade", () -> new TanksUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<CraftingUpgradeItem> CRAFTING_UPGRADE = ITEMS.register("crafting_upgrade", () -> new CraftingUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<PickupUpgradeItem> PICKUP_UPGRADE = ITEMS.register("pickup_upgrade", () -> new PickupUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<MagnetUpgradeItem> MAGNET_UPGRADE = ITEMS.register("magnet_upgrade", () -> new MagnetUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<JukeboxUpgradeItem> JUKEBOX_UPGRADE = ITEMS.register("jukebox_upgrade", () -> new JukeboxUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<VoidUpgradeItem> VOID_UPGRADE = ITEMS.register("void_upgrade", () -> new VoidUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<FeedingUpgradeItem> FEEDING_UPGRADE = ITEMS.register("feeding_upgrade", () -> new FeedingUpgradeItem(new Item.Properties().stacksTo(16)));

    public static final Supplier<EntityType<BackpackItemEntity>> BACKPACK_ITEM_ENTITY = ENTITY_TYPES.register(
            "backpack", () -> EntityType.Builder.of(BackpackItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20).build("")
    );

    public static final List<Item> COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES = new ArrayList<>();
    public static final List<Item> COMPATIBLE_NETHER_BACKPACK_ENTRIES = new ArrayList<>();
 */
}