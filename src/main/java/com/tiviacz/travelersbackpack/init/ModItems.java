package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackItemAccess;
import com.tiviacz.travelersbackpack.inventory.transfer.ItemFluidTankWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.BackpackTankItem;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TravelersBackpack.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TravelersBackpack.MODID);

    //Standard
    public static final DeferredItem<TravelersBackpackItem> STANDARD_TRAVELERS_BACKPACK = ITEMS.registerItem("standard", (props) -> new TravelersBackpackItem(props, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get()));

    //Blocks
    public static final DeferredItem<TravelersBackpackItem> NETHERITE_TRAVELERS_BACKPACK = ITEMS.registerItem("netherite", (props) -> new TravelersBackpackItem(props, ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> DIAMOND_TRAVELERS_BACKPACK = ITEMS.registerItem("diamond", (props) -> new TravelersBackpackItem(props, ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> GOLD_TRAVELERS_BACKPACK = ITEMS.registerItem("gold", (props) -> new TravelersBackpackItem(props, ModBlocks.GOLD_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> EMERALD_TRAVELERS_BACKPACK = ITEMS.registerItem("emerald", (props) -> new TravelersBackpackItem(props, ModBlocks.EMERALD_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> IRON_TRAVELERS_BACKPACK = ITEMS.registerItem("iron", (props) -> new TravelersBackpackItem(props, ModBlocks.IRON_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> LAPIS_TRAVELERS_BACKPACK = ITEMS.registerItem("lapis", (props) -> new TravelersBackpackItem(props, ModBlocks.LAPIS_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> REDSTONE_TRAVELERS_BACKPACK = ITEMS.registerItem("redstone", (props) -> new TravelersBackpackItem(props, ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> COAL_TRAVELERS_BACKPACK = ITEMS.registerItem("coal", (props) -> new TravelersBackpackItem(props, ModBlocks.COAL_TRAVELERS_BACKPACK.get()));

    public static final DeferredItem<TravelersBackpackItem> QUARTZ_TRAVELERS_BACKPACK = ITEMS.registerItem("quartz", (props) -> new TravelersBackpackItem(props, ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> BOOKSHELF_TRAVELERS_BACKPACK = ITEMS.registerItem("bookshelf", (props) -> new TravelersBackpackItem(props, ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> END_TRAVELERS_BACKPACK = ITEMS.registerItem("end", (props) -> new TravelersBackpackItem(props, ModBlocks.END_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> NETHER_TRAVELERS_BACKPACK = ITEMS.registerItem("nether", (props) -> new TravelersBackpackItem(props, ModBlocks.NETHER_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SANDSTONE_TRAVELERS_BACKPACK = ITEMS.registerItem("sandstone", (props) -> new TravelersBackpackItem(props, ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SNOW_TRAVELERS_BACKPACK = ITEMS.registerItem("snow", (props) -> new TravelersBackpackItem(props, ModBlocks.SNOW_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SPONGE_TRAVELERS_BACKPACK = ITEMS.registerItem("sponge", (props) -> new TravelersBackpackItem(props, ModBlocks.SPONGE_TRAVELERS_BACKPACK.get()));

    //Food
    public static final DeferredItem<TravelersBackpackItem> CAKE_TRAVELERS_BACKPACK = ITEMS.registerItem("cake", (props) -> new TravelersBackpackItem(props, ModBlocks.CAKE_TRAVELERS_BACKPACK.get()));

    //Plants
    public static final DeferredItem<TravelersBackpackItem> CACTUS_TRAVELERS_BACKPACK = ITEMS.registerItem("cactus", (props) -> new TravelersBackpackItem(props, ModBlocks.CACTUS_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> HAY_TRAVELERS_BACKPACK = ITEMS.registerItem("hay", (props) -> new TravelersBackpackItem(props, ModBlocks.HAY_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> MELON_TRAVELERS_BACKPACK = ITEMS.registerItem("melon", (props) -> new TravelersBackpackItem(props, ModBlocks.MELON_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> PUMPKIN_TRAVELERS_BACKPACK = ITEMS.registerItem("pumpkin", (props) -> new TravelersBackpackItem(props, ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get()));

    //Mobs
    public static final DeferredItem<TravelersBackpackItem> CREEPER_TRAVELERS_BACKPACK = ITEMS.registerItem("creeper", (props) -> new TravelersBackpackItem(props, ModBlocks.CREEPER_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> DRAGON_TRAVELERS_BACKPACK = ITEMS.registerItem("dragon", (props) -> new TravelersBackpackItem(props, ModBlocks.DRAGON_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> ENDERMAN_TRAVELERS_BACKPACK = ITEMS.registerItem("enderman", (props) -> new TravelersBackpackItem(props, ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> BLAZE_TRAVELERS_BACKPACK = ITEMS.registerItem("blaze", (props) -> new TravelersBackpackItem(props, ModBlocks.BLAZE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> GHAST_TRAVELERS_BACKPACK = ITEMS.registerItem("ghast", (props) -> new TravelersBackpackItem(props, ModBlocks.GHAST_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> MAGMA_CUBE_TRAVELERS_BACKPACK = ITEMS.registerItem("magma_cube", (props) -> new TravelersBackpackItem(props, ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SKELETON_TRAVELERS_BACKPACK = ITEMS.registerItem("skeleton", (props) -> new TravelersBackpackItem(props, ModBlocks.SKELETON_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SPIDER_TRAVELERS_BACKPACK = ITEMS.registerItem("spider", (props) -> new TravelersBackpackItem(props, ModBlocks.SPIDER_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> WITHER_TRAVELERS_BACKPACK = ITEMS.registerItem("wither", (props) -> new TravelersBackpackItem(props, ModBlocks.WITHER_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> WARDEN_TRAVELERS_BACKPACK = ITEMS.registerItem("warden", (props) -> new TravelersBackpackItem(props, ModBlocks.WARDEN_TRAVELERS_BACKPACK.get()));

    //Friendly Mobs
    public static final DeferredItem<TravelersBackpackItem> BAT_TRAVELERS_BACKPACK = ITEMS.registerItem("bat", (props) -> new TravelersBackpackItem(props, ModBlocks.BAT_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> BEE_TRAVELERS_BACKPACK = ITEMS.registerItem("bee", (props) -> new TravelersBackpackItem(props, ModBlocks.BEE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> WOLF_TRAVELERS_BACKPACK = ITEMS.registerItem("wolf", (props) -> new TravelersBackpackItem(props, ModBlocks.WOLF_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> FOX_TRAVELERS_BACKPACK = ITEMS.registerItem("fox", (props) -> new TravelersBackpackItem(props, ModBlocks.FOX_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> OCELOT_TRAVELERS_BACKPACK = ITEMS.registerItem("ocelot", (props) -> new TravelersBackpackItem(props, ModBlocks.OCELOT_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> HORSE_TRAVELERS_BACKPACK = ITEMS.registerItem("horse", (props) -> new TravelersBackpackItem(props, ModBlocks.HORSE_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> COW_TRAVELERS_BACKPACK = ITEMS.registerItem("cow", (props) -> new TravelersBackpackItem(props, ModBlocks.COW_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> PIG_TRAVELERS_BACKPACK = ITEMS.registerItem("pig", (props) -> new TravelersBackpackItem(props, ModBlocks.PIG_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SHEEP_TRAVELERS_BACKPACK = ITEMS.registerItem("sheep", (props) -> new TravelersBackpackItem(props, ModBlocks.SHEEP_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> CHICKEN_TRAVELERS_BACKPACK = ITEMS.registerItem("chicken", (props) -> new TravelersBackpackItem(props, ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> SQUID_TRAVELERS_BACKPACK = ITEMS.registerItem("squid", (props) -> new TravelersBackpackItem(props, ModBlocks.SQUID_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> VILLAGER_TRAVELERS_BACKPACK = ITEMS.registerItem("villager", (props) -> new TravelersBackpackItem(props, ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get()));
    public static final DeferredItem<TravelersBackpackItem> IRON_GOLEM_TRAVELERS_BACKPACK = ITEMS.registerItem("iron_golem", (props) -> new TravelersBackpackItem(props, ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get()));

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
    public static final DeferredItem<RefillUpgradeItem> REFILL_UPGRADE = ITEMS.registerItem("refill_upgrade", (props) -> new RefillUpgradeItem(props.stacksTo(16)));

    public static final Supplier<EntityType<BackpackItemEntity>> BACKPACK_ITEM_ENTITY = ENTITY_TYPES.register(
            "backpack", () -> EntityType.Builder.of(BackpackItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"))));

    public static final List<Item> COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES = new ArrayList<>();
    public static final List<Item> COMPATIBLE_NETHER_BACKPACK_ENTRIES = new ArrayList<>();

    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Item.ITEM, (stack, access) -> new BackpackItemAccess(ItemAccess.forStack(stack), BackpackWrapper.fromStack(stack), ModDataComponents.BACKPACK_CONTAINER.get()),
                ModItems.STANDARD_TRAVELERS_BACKPACK.get(),
                ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
                ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
                ModItems.GOLD_TRAVELERS_BACKPACK.get(),
                ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
                ModItems.IRON_TRAVELERS_BACKPACK.get(),
                ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
                ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),
                ModItems.COAL_TRAVELERS_BACKPACK.get(),

                ModItems.QUARTZ_TRAVELERS_BACKPACK.get(),
                ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
                ModItems.END_TRAVELERS_BACKPACK.get(),
                ModItems.NETHER_TRAVELERS_BACKPACK.get(),
                ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
                ModItems.SNOW_TRAVELERS_BACKPACK.get(),
                ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

                ModItems.CAKE_TRAVELERS_BACKPACK.get(),

                ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
                ModItems.HAY_TRAVELERS_BACKPACK.get(),
                ModItems.MELON_TRAVELERS_BACKPACK.get(),
                ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

                ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
                ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
                ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
                ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
                ModItems.GHAST_TRAVELERS_BACKPACK.get(),
                ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
                ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
                ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
                ModItems.WITHER_TRAVELERS_BACKPACK.get(),
                ModItems.WARDEN_TRAVELERS_BACKPACK.get(),

                ModItems.BAT_TRAVELERS_BACKPACK.get(),
                ModItems.BEE_TRAVELERS_BACKPACK.get(),
                ModItems.WOLF_TRAVELERS_BACKPACK.get(),
                ModItems.FOX_TRAVELERS_BACKPACK.get(),
                ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
                ModItems.HORSE_TRAVELERS_BACKPACK.get(),
                ModItems.COW_TRAVELERS_BACKPACK.get(),
                ModItems.PIG_TRAVELERS_BACKPACK.get(),
                ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
                ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
                ModItems.SQUID_TRAVELERS_BACKPACK.get(),
                ModItems.VILLAGER_TRAVELERS_BACKPACK.get(),
                ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get());

        event.registerItem(Capabilities.Fluid.ITEM, (stack, context) -> {
                    BackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
                    if(wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                        return new ItemFluidTankWrapper(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
                    }
                    return null;
                }, ModItems.STANDARD_TRAVELERS_BACKPACK.get(),
                ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
                ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
                ModItems.GOLD_TRAVELERS_BACKPACK.get(),
                ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
                ModItems.IRON_TRAVELERS_BACKPACK.get(),
                ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
                ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),
                ModItems.COAL_TRAVELERS_BACKPACK.get(),

                ModItems.QUARTZ_TRAVELERS_BACKPACK.get(),
                ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
                ModItems.END_TRAVELERS_BACKPACK.get(),
                ModItems.NETHER_TRAVELERS_BACKPACK.get(),
                ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
                ModItems.SNOW_TRAVELERS_BACKPACK.get(),
                ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

                ModItems.CAKE_TRAVELERS_BACKPACK.get(),

                ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
                ModItems.HAY_TRAVELERS_BACKPACK.get(),
                ModItems.MELON_TRAVELERS_BACKPACK.get(),
                ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

                ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
                ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
                ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
                ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
                ModItems.GHAST_TRAVELERS_BACKPACK.get(),
                ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
                ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
                ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
                ModItems.WITHER_TRAVELERS_BACKPACK.get(),
                ModItems.WARDEN_TRAVELERS_BACKPACK.get(),

                ModItems.BAT_TRAVELERS_BACKPACK.get(),
                ModItems.BEE_TRAVELERS_BACKPACK.get(),
                ModItems.WOLF_TRAVELERS_BACKPACK.get(),
                ModItems.FOX_TRAVELERS_BACKPACK.get(),
                ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
                ModItems.HORSE_TRAVELERS_BACKPACK.get(),
                ModItems.COW_TRAVELERS_BACKPACK.get(),
                ModItems.PIG_TRAVELERS_BACKPACK.get(),
                ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
                ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
                ModItems.SQUID_TRAVELERS_BACKPACK.get(),
                ModItems.VILLAGER_TRAVELERS_BACKPACK.get(),
                ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get());
    }
}