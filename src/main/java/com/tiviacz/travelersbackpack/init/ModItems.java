package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.items.BackpackTankItem;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TravelersBackpack.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TravelersBackpack.MODID);

    //Standard
    public static final DeferredItem<TravelersBackpackItem> STANDARD_TRAVELERS_BACKPACK = ITEMS.registerItem("standard", (props) -> new TravelersBackpackItem(props, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get(), "standard"));

    //Blocks
    public static final DeferredItem<TravelersBackpackItem> NETHERITE_TRAVELERS_BACKPACK = ITEMS.registerItem("netherite", (props) -> new TravelersBackpackItem(props, ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get(), "netherite"));
    public static final DeferredItem<TravelersBackpackItem> DIAMOND_TRAVELERS_BACKPACK = ITEMS.registerItem("diamond", (props) -> new TravelersBackpackItem(props, ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get(), "diamond"));
    public static final DeferredItem<TravelersBackpackItem> GOLD_TRAVELERS_BACKPACK = ITEMS.registerItem("gold", (props) -> new TravelersBackpackItem(props, ModBlocks.GOLD_TRAVELERS_BACKPACK.get(), "gold"));
    public static final DeferredItem<TravelersBackpackItem> EMERALD_TRAVELERS_BACKPACK = ITEMS.registerItem("emerald", (props) -> new TravelersBackpackItem(props, ModBlocks.EMERALD_TRAVELERS_BACKPACK.get(), "emerald"));
    public static final DeferredItem<TravelersBackpackItem> IRON_TRAVELERS_BACKPACK = ITEMS.registerItem("iron", (props) -> new TravelersBackpackItem(props, ModBlocks.IRON_TRAVELERS_BACKPACK.get(), "iron"));
    public static final DeferredItem<TravelersBackpackItem> LAPIS_TRAVELERS_BACKPACK = ITEMS.registerItem("lapis", (props) -> new TravelersBackpackItem(props, ModBlocks.LAPIS_TRAVELERS_BACKPACK.get(), "lapis"));
    public static final DeferredItem<TravelersBackpackItem> REDSTONE_TRAVELERS_BACKPACK = ITEMS.registerItem("redstone", (props) -> new TravelersBackpackItem(props, ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get(), "redstone"));
    public static final DeferredItem<TravelersBackpackItem> COAL_TRAVELERS_BACKPACK = ITEMS.registerItem("coal", (props) -> new TravelersBackpackItem(props, ModBlocks.COAL_TRAVELERS_BACKPACK.get(), "coal"));

    public static final DeferredItem<TravelersBackpackItem> QUARTZ_TRAVELERS_BACKPACK = ITEMS.registerItem("quartz", (props) -> new TravelersBackpackItem(props, ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get(), "quartz"));
    public static final DeferredItem<TravelersBackpackItem> BOOKSHELF_TRAVELERS_BACKPACK = ITEMS.registerItem("bookshelf", (props) -> new TravelersBackpackItem(props, ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get(), "bookshelf"));
    public static final DeferredItem<TravelersBackpackItem> END_TRAVELERS_BACKPACK = ITEMS.registerItem("end", (props) -> new TravelersBackpackItem(props, ModBlocks.END_TRAVELERS_BACKPACK.get(), "end"));
    public static final DeferredItem<TravelersBackpackItem> NETHER_TRAVELERS_BACKPACK = ITEMS.registerItem("nether", (props) -> new TravelersBackpackItem(props, ModBlocks.NETHER_TRAVELERS_BACKPACK.get(), "nether"));
    public static final DeferredItem<TravelersBackpackItem> SANDSTONE_TRAVELERS_BACKPACK = ITEMS.registerItem("sandstone", (props) -> new TravelersBackpackItem(props, ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get(), "sandstone"));
    public static final DeferredItem<TravelersBackpackItem> SNOW_TRAVELERS_BACKPACK = ITEMS.registerItem("snow", (props) -> new TravelersBackpackItem(props, ModBlocks.SNOW_TRAVELERS_BACKPACK.get(), "snow"));
    public static final DeferredItem<TravelersBackpackItem> SPONGE_TRAVELERS_BACKPACK = ITEMS.registerItem("sponge", (props) -> new TravelersBackpackItem(props, ModBlocks.SPONGE_TRAVELERS_BACKPACK.get(), "sponge"));

    //Food
    public static final DeferredItem<TravelersBackpackItem> CAKE_TRAVELERS_BACKPACK = ITEMS.registerItem("cake", (props) -> new TravelersBackpackItem(props, ModBlocks.CAKE_TRAVELERS_BACKPACK.get(), "cake"));

    //Plants
    public static final DeferredItem<TravelersBackpackItem> CACTUS_TRAVELERS_BACKPACK = ITEMS.registerItem("cactus", (props) -> new TravelersBackpackItem(props, ModBlocks.CACTUS_TRAVELERS_BACKPACK.get(), "cactus"));
    public static final DeferredItem<TravelersBackpackItem> HAY_TRAVELERS_BACKPACK = ITEMS.registerItem("hay", (props) -> new TravelersBackpackItem(props, ModBlocks.HAY_TRAVELERS_BACKPACK.get(), "hay"));
    public static final DeferredItem<TravelersBackpackItem> MELON_TRAVELERS_BACKPACK = ITEMS.registerItem("melon", (props) -> new TravelersBackpackItem(props, ModBlocks.MELON_TRAVELERS_BACKPACK.get(), "melon"));
    public static final DeferredItem<TravelersBackpackItem> PUMPKIN_TRAVELERS_BACKPACK = ITEMS.registerItem("pumpkin", (props) -> new TravelersBackpackItem(props, ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get(), "pumpkin"));

    //Mobs
    public static final DeferredItem<TravelersBackpackItem> CREEPER_TRAVELERS_BACKPACK = ITEMS.registerItem("creeper", (props) -> new TravelersBackpackItem(props, ModBlocks.CREEPER_TRAVELERS_BACKPACK.get(), "creeper"));
    public static final DeferredItem<TravelersBackpackItem> DRAGON_TRAVELERS_BACKPACK = ITEMS.registerItem("dragon", (props) -> new TravelersBackpackItem(props, ModBlocks.DRAGON_TRAVELERS_BACKPACK.get(), "dragon"));
    public static final DeferredItem<TravelersBackpackItem> ENDERMAN_TRAVELERS_BACKPACK = ITEMS.registerItem("enderman", (props) -> new TravelersBackpackItem(props, ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get(), "enderman"));
    public static final DeferredItem<TravelersBackpackItem> BLAZE_TRAVELERS_BACKPACK = ITEMS.registerItem("blaze", (props) -> new TravelersBackpackItem(props, ModBlocks.BLAZE_TRAVELERS_BACKPACK.get(), "blaze"));
    public static final DeferredItem<TravelersBackpackItem> GHAST_TRAVELERS_BACKPACK = ITEMS.registerItem("ghast", (props) -> new TravelersBackpackItem(props, ModBlocks.GHAST_TRAVELERS_BACKPACK.get(), "ghast"));
    public static final DeferredItem<TravelersBackpackItem> MAGMA_CUBE_TRAVELERS_BACKPACK = ITEMS.registerItem("magma_cube", (props) -> new TravelersBackpackItem(props, ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get(), "magma_cube"));
    public static final DeferredItem<TravelersBackpackItem> SKELETON_TRAVELERS_BACKPACK = ITEMS.registerItem("skeleton", (props) -> new TravelersBackpackItem(props, ModBlocks.SKELETON_TRAVELERS_BACKPACK.get(), "skeleton"));
    public static final DeferredItem<TravelersBackpackItem> SPIDER_TRAVELERS_BACKPACK = ITEMS.registerItem("spider", (props) -> new TravelersBackpackItem(props, ModBlocks.SPIDER_TRAVELERS_BACKPACK.get(), "spider"));
    public static final DeferredItem<TravelersBackpackItem> WITHER_TRAVELERS_BACKPACK = ITEMS.registerItem("wither", (props) -> new TravelersBackpackItem(props, ModBlocks.WITHER_TRAVELERS_BACKPACK.get(), "wither"));
    public static final DeferredItem<TravelersBackpackItem> WARDEN_TRAVELERS_BACKPACK = ITEMS.registerItem("warden", (props) -> new TravelersBackpackItem(props, ModBlocks.WARDEN_TRAVELERS_BACKPACK.get(), "warden"));

    //Friendly Mobs
    public static final DeferredItem<TravelersBackpackItem> BAT_TRAVELERS_BACKPACK = ITEMS.registerItem("bat", (props) -> new TravelersBackpackItem(props, ModBlocks.BAT_TRAVELERS_BACKPACK.get(), "bat"));
    public static final DeferredItem<TravelersBackpackItem> BEE_TRAVELERS_BACKPACK = ITEMS.registerItem("bee", (props) -> new TravelersBackpackItem(props, ModBlocks.BEE_TRAVELERS_BACKPACK.get(), "bee"));
    public static final DeferredItem<TravelersBackpackItem> WOLF_TRAVELERS_BACKPACK = ITEMS.registerItem("wolf", (props) -> new TravelersBackpackItem(props, ModBlocks.WOLF_TRAVELERS_BACKPACK.get(), "wolf"));
    public static final DeferredItem<TravelersBackpackItem> FOX_TRAVELERS_BACKPACK = ITEMS.registerItem("fox", (props) -> new TravelersBackpackItem(props, ModBlocks.FOX_TRAVELERS_BACKPACK.get(), "fox"));
    public static final DeferredItem<TravelersBackpackItem> OCELOT_TRAVELERS_BACKPACK = ITEMS.registerItem("ocelot", (props) -> new TravelersBackpackItem(props, ModBlocks.OCELOT_TRAVELERS_BACKPACK.get(), "ocelot"));
    public static final DeferredItem<TravelersBackpackItem> HORSE_TRAVELERS_BACKPACK = ITEMS.registerItem("horse", (props) -> new TravelersBackpackItem(props, ModBlocks.HORSE_TRAVELERS_BACKPACK.get(), "horse"));
    public static final DeferredItem<TravelersBackpackItem> COW_TRAVELERS_BACKPACK = ITEMS.registerItem("cow", (props) -> new TravelersBackpackItem(props, ModBlocks.COW_TRAVELERS_BACKPACK.get(), "cow"));
    public static final DeferredItem<TravelersBackpackItem> PIG_TRAVELERS_BACKPACK = ITEMS.registerItem("pig", (props) -> new TravelersBackpackItem(props, ModBlocks.PIG_TRAVELERS_BACKPACK.get(), "pig"));
    public static final DeferredItem<TravelersBackpackItem> SHEEP_TRAVELERS_BACKPACK = ITEMS.registerItem("sheep", (props) -> new TravelersBackpackItem(props, ModBlocks.SHEEP_TRAVELERS_BACKPACK.get(), "sheep"));
    public static final DeferredItem<TravelersBackpackItem> CHICKEN_TRAVELERS_BACKPACK = ITEMS.registerItem("chicken", (props) -> new TravelersBackpackItem(props, ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get(), "chicken"));
    public static final DeferredItem<TravelersBackpackItem> SQUID_TRAVELERS_BACKPACK = ITEMS.registerItem("squid", (props) -> new TravelersBackpackItem(props, ModBlocks.SQUID_TRAVELERS_BACKPACK.get(), "squid"));
    public static final DeferredItem<TravelersBackpackItem> VILLAGER_TRAVELERS_BACKPACK = ITEMS.registerItem("villager", (props) -> new TravelersBackpackItem(props, ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get(), "villager"));
    public static final DeferredItem<TravelersBackpackItem> IRON_GOLEM_TRAVELERS_BACKPACK = ITEMS.registerItem("iron_golem", (props) -> new TravelersBackpackItem(props, ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get(), "iron_golem"));

    //Other Items
    public static final DeferredItem<SleepingBagItem> WHITE_SLEEPING_BAG = ITEMS.registerItem("white_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.WHITE_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> ORANGE_SLEEPING_BAG = ITEMS.registerItem("orange_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.ORANGE_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> MAGENTA_SLEEPING_BAG = ITEMS.registerItem("magenta_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.MAGENTA_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> LIGHT_BLUE_SLEEPING_BAG = ITEMS.registerItem("light_blue_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> YELLOW_SLEEPING_BAG = ITEMS.registerItem("yellow_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.YELLOW_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> LIME_SLEEPING_BAG = ITEMS.registerItem("lime_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.LIME_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> PINK_SLEEPING_BAG = ITEMS.registerItem("pink_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.PINK_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> GRAY_SLEEPING_BAG = ITEMS.registerItem("gray_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.GRAY_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> LIGHT_GRAY_SLEEPING_BAG = ITEMS.registerItem("light_gray_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> CYAN_SLEEPING_BAG = ITEMS.registerItem("cyan_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.CYAN_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> PURPLE_SLEEPING_BAG = ITEMS.registerItem("purple_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.PURPLE_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> BLUE_SLEEPING_BAG = ITEMS.registerItem("blue_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.BLUE_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> BROWN_SLEEPING_BAG = ITEMS.registerItem("brown_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.BROWN_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> GREEN_SLEEPING_BAG = ITEMS.registerItem("green_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.GREEN_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> RED_SLEEPING_BAG = ITEMS.registerItem("red_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.RED_SLEEPING_BAG.get(), props));
    public static final DeferredItem<SleepingBagItem> BLACK_SLEEPING_BAG = ITEMS.registerItem("black_sleeping_bag", (props) -> new SleepingBagItem(ModBlocks.BLACK_SLEEPING_BAG.get(), props));
    public static final DeferredItem<Item> BACKPACK_TANK = ITEMS.registerItem("backpack_tank", (props) -> new BackpackTankItem(props.stacksTo(16)));
    public static final DeferredItem<HoseItem> HOSE = ITEMS.registerItem("hose", (props) -> new HoseItem(props.stacksTo(1)));
    public static final DeferredItem<Item> HOSE_NOZZLE = ITEMS.registerItem("hose_nozzle", Item::new);
    public static final DeferredItem<TierUpgrade> BLANK_UPGRADE = ITEMS.registerItem("blank_upgrade", (props) -> new TierUpgrade(props, TierUpgrade.Upgrade.BLANK_UPGRADE));
    public static final DeferredItem<TierUpgrade> IRON_TIER_UPGRADE = ITEMS.registerItem("iron_tier_upgrade", (props) -> new TierUpgrade(props.stacksTo(16), TierUpgrade.Upgrade.IRON_TIER_UPGRADE));
    public static final DeferredItem<TierUpgrade> GOLD_TIER_UPGRADE = ITEMS.registerItem("gold_tier_upgrade", (props) -> new TierUpgrade(props.stacksTo(16), TierUpgrade.Upgrade.GOLD_TIER_UPGRADE));
    public static final DeferredItem<TierUpgrade> DIAMOND_TIER_UPGRADE = ITEMS.registerItem("diamond_tier_upgrade", (props) -> new TierUpgrade(props.stacksTo(16), TierUpgrade.Upgrade.DIAMOND_TIER_UPGRADE));
    public static final DeferredItem<TierUpgrade> NETHERITE_TIER_UPGRADE = ITEMS.registerItem("netherite_tier_upgrade", (props) -> new TierUpgrade(props.stacksTo(16), TierUpgrade.Upgrade.NETHERITE_TIER_UPGRADE));
    public static final DeferredItem<TanksUpgradeItem> TANKS_UPGRADE = ITEMS.registerItem("tanks_upgrade", (props) -> new TanksUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<CraftingUpgradeItem> CRAFTING_UPGRADE = ITEMS.registerItem("crafting_upgrade", (props) -> new CraftingUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<FurnaceUpgradeItem> FURNACE_UPGRADE = ITEMS.registerItem("furnace_upgrade", (props) -> new FurnaceUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<SmokerUpgradeItem> SMOKER_UPGRADE = ITEMS.registerItem("smoker_upgrade", (props) -> new SmokerUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<BlastFurnaceUpgradeItem> BLAST_FURNACE_UPGRADE = ITEMS.registerItem("blast_furnace_upgrade", (props) -> new BlastFurnaceUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<PickupUpgradeItem> PICKUP_UPGRADE = ITEMS.registerItem("pickup_upgrade", (props) -> new PickupUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<MagnetUpgradeItem> MAGNET_UPGRADE = ITEMS.registerItem("magnet_upgrade", (props) -> new MagnetUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<JukeboxUpgradeItem> JUKEBOX_UPGRADE = ITEMS.registerItem("jukebox_upgrade", (props) -> new JukeboxUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<VoidUpgradeItem> VOID_UPGRADE = ITEMS.registerItem("void_upgrade", (props) -> new VoidUpgradeItem(props.stacksTo(16)));
    public static final DeferredItem<FeedingUpgradeItem> FEEDING_UPGRADE = ITEMS.registerItem("feeding_upgrade", (props) -> new FeedingUpgradeItem(props.stacksTo(16)));

    public static final Supplier<EntityType<BackpackItemEntity>> BACKPACK_ITEM_ENTITY = ENTITY_TYPES.register(
            "backpack", () -> EntityType.Builder.of(BackpackItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"))));

    public static final List<Item> COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES = new ArrayList<>();
    public static final List<Item> COMPATIBLE_NETHER_BACKPACK_ENTRIES = new ArrayList<>();

}