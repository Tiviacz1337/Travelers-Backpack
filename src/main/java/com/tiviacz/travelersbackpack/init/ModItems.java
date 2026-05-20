package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.FluidStorageItemWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.item.BackpackTankItem;
import com.tiviacz.travelersbackpack.item.HoseItem;
import com.tiviacz.travelersbackpack.item.SleepingBagItem;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.item.upgrades.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

import java.util.List;

public class ModItems {
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
    public static TravelersBackpackItem WARDEN_TRAVELERS_BACKPACK;

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
    public static Item BLANK_UPGRADE;
    public static Item IRON_TIER_UPGRADE;
    public static Item GOLD_TIER_UPGRADE;
    public static Item DIAMOND_TIER_UPGRADE;
    public static Item NETHERITE_TIER_UPGRADE;
    public static Item TANKS_UPGRADE;
    public static Item CRAFTING_UPGRADE;
    public static Item FURNACE_UPGRADE;
    public static Item SMOKER_UPGRADE;
    public static Item BLAST_FURNACE_UPGRADE;
    public static Item PICKUP_UPGRADE;
    public static Item MAGNET_UPGRADE;
    public static Item JUKEBOX_UPGRADE;
    public static Item VOID_UPGRADE;
    public static Item FEEDING_UPGRADE;
    public static Item REFILL_UPGRADE;
    public static Item LANTERN_UPGRADE;

    //Backpack Item Entity
    public static EntityType<BackpackItemEntity> BACKPACK_ITEM_ENTITY;

    public static void init() {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "standard"), new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite"), new TravelersBackpackItem(ModBlocks.NETHERITE_TRAVELERS_BACKPACK));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond"), new TravelersBackpackItem(ModBlocks.DIAMOND_TRAVELERS_BACKPACK));
        GOLD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gold"), new TravelersBackpackItem(ModBlocks.GOLD_TRAVELERS_BACKPACK));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "emerald"), new TravelersBackpackItem(ModBlocks.EMERALD_TRAVELERS_BACKPACK));
        IRON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron"), new TravelersBackpackItem(ModBlocks.IRON_TRAVELERS_BACKPACK));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "lapis"), new TravelersBackpackItem(ModBlocks.LAPIS_TRAVELERS_BACKPACK));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "redstone"), new TravelersBackpackItem(ModBlocks.REDSTONE_TRAVELERS_BACKPACK));
        COAL_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "coal"), new TravelersBackpackItem(ModBlocks.COAL_TRAVELERS_BACKPACK));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "quartz"), new TravelersBackpackItem(ModBlocks.QUARTZ_TRAVELERS_BACKPACK));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackItem(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK));
        END_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "end"), new TravelersBackpackItem(ModBlocks.END_TRAVELERS_BACKPACK));
        NETHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "nether"), new TravelersBackpackItem(ModBlocks.NETHER_TRAVELERS_BACKPACK));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackItem(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK));
        SNOW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "snow"), new TravelersBackpackItem(ModBlocks.SNOW_TRAVELERS_BACKPACK));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sponge"), new TravelersBackpackItem(ModBlocks.SPONGE_TRAVELERS_BACKPACK));

        CAKE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cake"), new TravelersBackpackItem(ModBlocks.CAKE_TRAVELERS_BACKPACK));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cactus"), new TravelersBackpackItem(ModBlocks.CACTUS_TRAVELERS_BACKPACK));
        HAY_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hay"), new TravelersBackpackItem(ModBlocks.HAY_TRAVELERS_BACKPACK));
        MELON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "melon"), new TravelersBackpackItem(ModBlocks.MELON_TRAVELERS_BACKPACK));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackItem(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "creeper"), new TravelersBackpackItem(ModBlocks.CREEPER_TRAVELERS_BACKPACK));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "dragon"), new TravelersBackpackItem(ModBlocks.DRAGON_TRAVELERS_BACKPACK));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "enderman"), new TravelersBackpackItem(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blaze"), new TravelersBackpackItem(ModBlocks.BLAZE_TRAVELERS_BACKPACK));
        GHAST_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "ghast"), new TravelersBackpackItem(ModBlocks.GHAST_TRAVELERS_BACKPACK));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackItem(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackItem(ModBlocks.SKELETON_TRAVELERS_BACKPACK));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "spider"), new TravelersBackpackItem(ModBlocks.SPIDER_TRAVELERS_BACKPACK));
        WITHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "wither"), new TravelersBackpackItem(ModBlocks.WITHER_TRAVELERS_BACKPACK));
        WARDEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "warden"), new TravelersBackpackItem(ModBlocks.WARDEN_TRAVELERS_BACKPACK));

        BAT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "bat"), new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK));
        BEE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "bee"), new TravelersBackpackItem(ModBlocks.BEE_TRAVELERS_BACKPACK));
        WOLF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "wolf"), new TravelersBackpackItem(ModBlocks.WOLF_TRAVELERS_BACKPACK));
        FOX_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "fox"), new TravelersBackpackItem(ModBlocks.FOX_TRAVELERS_BACKPACK));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackItem(ModBlocks.OCELOT_TRAVELERS_BACKPACK));
        HORSE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "horse"), new TravelersBackpackItem(ModBlocks.HORSE_TRAVELERS_BACKPACK));
        COW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cow"), new TravelersBackpackItem(ModBlocks.COW_TRAVELERS_BACKPACK));
        PIG_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pig"), new TravelersBackpackItem(ModBlocks.PIG_TRAVELERS_BACKPACK));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sheep"), new TravelersBackpackItem(ModBlocks.SHEEP_TRAVELERS_BACKPACK));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "chicken"), new TravelersBackpackItem(ModBlocks.CHICKEN_TRAVELERS_BACKPACK));
        SQUID_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "squid"), new TravelersBackpackItem(ModBlocks.SQUID_TRAVELERS_BACKPACK));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "villager"), new TravelersBackpackItem(ModBlocks.VILLAGER_TRAVELERS_BACKPACK));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackItem(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK));

        WHITE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagItem(ModBlocks.WHITE_SLEEPING_BAG, new Item.Properties()));
        ORANGE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagItem(ModBlocks.ORANGE_SLEEPING_BAG, new Item.Properties()));
        MAGENTA_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagItem(ModBlocks.MAGENTA_SLEEPING_BAG, new Item.Properties()));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagItem(ModBlocks.LIGHT_BLUE_SLEEPING_BAG, new Item.Properties()));
        YELLOW_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagItem(ModBlocks.YELLOW_SLEEPING_BAG, new Item.Properties()));
        LIME_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagItem(ModBlocks.LIME_SLEEPING_BAG, new Item.Properties()));
        PINK_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagItem(ModBlocks.PINK_SLEEPING_BAG, new Item.Properties()));
        GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagItem(ModBlocks.GRAY_SLEEPING_BAG, new Item.Properties()));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagItem(ModBlocks.LIGHT_GRAY_SLEEPING_BAG, new Item.Properties()));
        CYAN_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagItem(ModBlocks.CYAN_SLEEPING_BAG, new Item.Properties()));
        PURPLE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagItem(ModBlocks.PURPLE_SLEEPING_BAG, new Item.Properties()));
        BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagItem(ModBlocks.BLUE_SLEEPING_BAG, new Item.Properties()));
        BROWN_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagItem(ModBlocks.BROWN_SLEEPING_BAG, new Item.Properties()));
        GREEN_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagItem(ModBlocks.GREEN_SLEEPING_BAG, new Item.Properties()));
        RED_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagItem(ModBlocks.RED_SLEEPING_BAG, new Item.Properties()));
        BLACK_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagItem(ModBlocks.BLACK_SLEEPING_BAG, new Item.Properties()));
        BACKPACK_TANK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_tank"), new BackpackTankItem(new Item.Properties().stacksTo(16)));
        HOSE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hose"), new HoseItem(new Item.Properties().stacksTo(1)));
        HOSE_NOZZLE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hose_nozzle"), new Item(new Item.Properties()));
        BLANK_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blank_upgrade"), new TierUpgrade(new Item.Properties(), TierUpgrade.Upgrade.BLANK_UPGRADE));
        IRON_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.IRON_TIER_UPGRADE));
        GOLD_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "gold_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.GOLD_TIER_UPGRADE));
        DIAMOND_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.DIAMOND_TIER_UPGRADE));
        NETHERITE_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16), TierUpgrade.Upgrade.NETHERITE_TIER_UPGRADE));
        TANKS_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "tanks_upgrade"), new TanksUpgradeItem(new Item.Properties().stacksTo(16)));
        CRAFTING_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "crafting_upgrade"), new CraftingUpgradeItem(new Item.Properties().stacksTo(16)));
        FURNACE_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "furnace_upgrade"), new FurnaceUpgradeItem(new Item.Properties().stacksTo(16)));
        SMOKER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "smoker_upgrade"), new SmokerUpgradeItem(new Item.Properties().stacksTo(16)));
        BLAST_FURNACE_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blast_furnace_upgrade"), new BlastFurnaceUpgradeItem(new Item.Properties().stacksTo(16)));
        PICKUP_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "pickup_upgrade"), new PickupUpgradeItem(new Item.Properties().stacksTo(16)));
        MAGNET_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "magnet_upgrade"), new MagnetUpgradeItem(new Item.Properties().stacksTo(16)));
        JUKEBOX_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "jukebox_upgrade"), new JukeboxUpgradeItem(new Item.Properties().stacksTo(16)));
        VOID_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "void_upgrade"), new VoidUpgradeItem(new Item.Properties().stacksTo(16)));
        FEEDING_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "feeding_upgrade"), new FeedingUpgradeItem(new Item.Properties().stacksTo(16)));
        REFILL_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "refill_upgrade"), new RefillUpgradeItem(new Item.Properties().stacksTo(16)));
        LANTERN_UPGRADE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "lantern_upgrade"), new LanternUpgradeItem(new Item.Properties().stacksTo(16)));

        BACKPACK_ITEM_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), EntityType.Builder.of(BackpackItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20).build(""));
    }

    public static void registerItemFluidStorage() {
        FluidStorage.ITEM.registerForItems((stack, context) -> {
            BackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
            if(wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                return new CombinedSlottedStorage<FluidVariant, SlottedStorage<FluidVariant>>(List.of(new FluidStorageItemWrapper(context, true), new FluidStorageItemWrapper(context, false)));
            }
            return null;
        }, ModItems.STANDARD_TRAVELERS_BACKPACK,
                ModItems.NETHERITE_TRAVELERS_BACKPACK,
                ModItems.DIAMOND_TRAVELERS_BACKPACK,
                ModItems.GOLD_TRAVELERS_BACKPACK,
                ModItems.EMERALD_TRAVELERS_BACKPACK,
                ModItems.IRON_TRAVELERS_BACKPACK,
                ModItems.LAPIS_TRAVELERS_BACKPACK,
                ModItems.REDSTONE_TRAVELERS_BACKPACK,
                ModItems.COAL_TRAVELERS_BACKPACK,

                ModItems.QUARTZ_TRAVELERS_BACKPACK,
                ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
                ModItems.END_TRAVELERS_BACKPACK,
                ModItems.NETHER_TRAVELERS_BACKPACK,
                ModItems.SANDSTONE_TRAVELERS_BACKPACK,
                ModItems.SNOW_TRAVELERS_BACKPACK,
                ModItems.SPONGE_TRAVELERS_BACKPACK,

                ModItems.CAKE_TRAVELERS_BACKPACK,

                ModItems.CACTUS_TRAVELERS_BACKPACK,
                ModItems.HAY_TRAVELERS_BACKPACK,
                ModItems.MELON_TRAVELERS_BACKPACK,
                ModItems.PUMPKIN_TRAVELERS_BACKPACK,

                ModItems.CREEPER_TRAVELERS_BACKPACK,
                ModItems.DRAGON_TRAVELERS_BACKPACK,
                ModItems.ENDERMAN_TRAVELERS_BACKPACK,
                ModItems.BLAZE_TRAVELERS_BACKPACK,
                ModItems.GHAST_TRAVELERS_BACKPACK,
                ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
                ModItems.SKELETON_TRAVELERS_BACKPACK,
                ModItems.SPIDER_TRAVELERS_BACKPACK,
                ModItems.WITHER_TRAVELERS_BACKPACK,
                ModItems.WARDEN_TRAVELERS_BACKPACK,

                ModItems.BAT_TRAVELERS_BACKPACK,
                ModItems.BEE_TRAVELERS_BACKPACK,
                ModItems.WOLF_TRAVELERS_BACKPACK,
                ModItems.FOX_TRAVELERS_BACKPACK,
                ModItems.OCELOT_TRAVELERS_BACKPACK,
                ModItems.HORSE_TRAVELERS_BACKPACK,
                ModItems.COW_TRAVELERS_BACKPACK,
                ModItems.PIG_TRAVELERS_BACKPACK,
                ModItems.SHEEP_TRAVELERS_BACKPACK,
                ModItems.CHICKEN_TRAVELERS_BACKPACK,
                ModItems.SQUID_TRAVELERS_BACKPACK,
                ModItems.VILLAGER_TRAVELERS_BACKPACK,
                ModItems.IRON_GOLEM_TRAVELERS_BACKPACK);
    }
}