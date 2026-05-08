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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
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
        STANDARD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "standard"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("standard")), ModBlocks.STANDARD_TRAVELERS_BACKPACK));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("netherite")), ModBlocks.NETHERITE_TRAVELERS_BACKPACK));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("diamond")), ModBlocks.DIAMOND_TRAVELERS_BACKPACK));
        GOLD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "gold"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("gold")), ModBlocks.GOLD_TRAVELERS_BACKPACK));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "emerald"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("emerald")), ModBlocks.EMERALD_TRAVELERS_BACKPACK));
        IRON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "iron"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("iron")), ModBlocks.IRON_TRAVELERS_BACKPACK));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "lapis"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("lapis")), ModBlocks.LAPIS_TRAVELERS_BACKPACK));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "redstone"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("redstone")), ModBlocks.REDSTONE_TRAVELERS_BACKPACK));
        COAL_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "coal"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("coal")), ModBlocks.COAL_TRAVELERS_BACKPACK));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "quartz"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("quartz")), ModBlocks.QUARTZ_TRAVELERS_BACKPACK));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("bookshelf")), ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK));
        END_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "end"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("end")), ModBlocks.END_TRAVELERS_BACKPACK));
        NETHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "nether"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("nether")), ModBlocks.NETHER_TRAVELERS_BACKPACK));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("sandstone")), ModBlocks.SANDSTONE_TRAVELERS_BACKPACK));
        SNOW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "snow"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("snow")), ModBlocks.SNOW_TRAVELERS_BACKPACK));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sponge"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("sponge")), ModBlocks.SPONGE_TRAVELERS_BACKPACK));

        CAKE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cake"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("cake")), ModBlocks.CAKE_TRAVELERS_BACKPACK));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cactus"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("cactus")), ModBlocks.CACTUS_TRAVELERS_BACKPACK));
        HAY_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "hay"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("hay")), ModBlocks.HAY_TRAVELERS_BACKPACK));
        MELON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "melon"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("melon")), ModBlocks.MELON_TRAVELERS_BACKPACK));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("pumpkin")), ModBlocks.PUMPKIN_TRAVELERS_BACKPACK));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "creeper"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("creeper")), ModBlocks.CREEPER_TRAVELERS_BACKPACK));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "dragon"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("dragon")), ModBlocks.DRAGON_TRAVELERS_BACKPACK));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "enderman"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("enderman")), ModBlocks.ENDERMAN_TRAVELERS_BACKPACK));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "blaze"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("blaze")), ModBlocks.BLAZE_TRAVELERS_BACKPACK));
        GHAST_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "ghast"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("ghast")), ModBlocks.GHAST_TRAVELERS_BACKPACK));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("magma_cube")), ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("skeleton")), ModBlocks.SKELETON_TRAVELERS_BACKPACK));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "spider"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("spider")), ModBlocks.SPIDER_TRAVELERS_BACKPACK));
        WITHER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "wither"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("wither")), ModBlocks.WITHER_TRAVELERS_BACKPACK));
        WARDEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "warden"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("warden")), ModBlocks.WARDEN_TRAVELERS_BACKPACK));

        BAT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "bat"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("bat")), ModBlocks.BAT_TRAVELERS_BACKPACK));
        BEE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "bee"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("bee")), ModBlocks.BEE_TRAVELERS_BACKPACK));
        WOLF_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "wolf"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("wolf")), ModBlocks.WOLF_TRAVELERS_BACKPACK));
        FOX_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "fox"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("fox")), ModBlocks.FOX_TRAVELERS_BACKPACK));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("ocelot")), ModBlocks.OCELOT_TRAVELERS_BACKPACK));
        HORSE_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "horse"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("horse")), ModBlocks.HORSE_TRAVELERS_BACKPACK));
        COW_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cow"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("cow")), ModBlocks.COW_TRAVELERS_BACKPACK));
        PIG_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pig"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("pig")), ModBlocks.PIG_TRAVELERS_BACKPACK));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sheep"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("sheep")), ModBlocks.SHEEP_TRAVELERS_BACKPACK));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "chicken"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("chicken")), ModBlocks.CHICKEN_TRAVELERS_BACKPACK));
        SQUID_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "squid"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("squid")), ModBlocks.SQUID_TRAVELERS_BACKPACK));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "villager"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("villager")), ModBlocks.VILLAGER_TRAVELERS_BACKPACK));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackItem(new Item.Properties().setId(resourceKey("iron_golem")), ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK));

        WHITE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagItem(ModBlocks.WHITE_SLEEPING_BAG, new Item.Properties().setId(resourceKey("white_sleeping_bag"))));
        ORANGE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagItem(ModBlocks.ORANGE_SLEEPING_BAG, new Item.Properties().setId(resourceKey("orange_sleeping_bag"))));
        MAGENTA_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagItem(ModBlocks.MAGENTA_SLEEPING_BAG, new Item.Properties().setId(resourceKey("magenta_sleeping_bag"))));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagItem(ModBlocks.LIGHT_BLUE_SLEEPING_BAG, new Item.Properties().setId(resourceKey("light_blue_sleeping_bag"))));
        YELLOW_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagItem(ModBlocks.YELLOW_SLEEPING_BAG, new Item.Properties().setId(resourceKey("yellow_sleeping_bag"))));
        LIME_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagItem(ModBlocks.LIME_SLEEPING_BAG, new Item.Properties().setId(resourceKey("lime_sleeping_bag"))));
        PINK_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagItem(ModBlocks.PINK_SLEEPING_BAG, new Item.Properties().setId(resourceKey("pink_sleeping_bag"))));
        GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagItem(ModBlocks.GRAY_SLEEPING_BAG, new Item.Properties().setId(resourceKey("gray_sleeping_bag"))));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagItem(ModBlocks.LIGHT_GRAY_SLEEPING_BAG, new Item.Properties().setId(resourceKey("light_gray_sleeping_bag"))));
        CYAN_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagItem(ModBlocks.CYAN_SLEEPING_BAG, new Item.Properties().setId(resourceKey("cyan_sleeping_bag"))));
        PURPLE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagItem(ModBlocks.PURPLE_SLEEPING_BAG, new Item.Properties().setId(resourceKey("purple_sleeping_bag"))));
        BLUE_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagItem(ModBlocks.BLUE_SLEEPING_BAG, new Item.Properties().setId(resourceKey("blue_sleeping_bag"))));
        BROWN_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagItem(ModBlocks.BROWN_SLEEPING_BAG, new Item.Properties().setId(resourceKey("brown_sleeping_bag"))));
        GREEN_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagItem(ModBlocks.GREEN_SLEEPING_BAG, new Item.Properties().setId(resourceKey("green_sleeping_bag"))));
        RED_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagItem(ModBlocks.RED_SLEEPING_BAG, new Item.Properties().setId(resourceKey("red_sleeping_bag"))));
        BLACK_SLEEPING_BAG = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagItem(ModBlocks.BLACK_SLEEPING_BAG, new Item.Properties().setId(resourceKey("black_sleeping_bag"))));
        BACKPACK_TANK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_tank"), new BackpackTankItem(new Item.Properties().stacksTo(16).setId(resourceKey("backpack_tank"))));
        HOSE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "hose"), new HoseItem(new Item.Properties().stacksTo(1).setId(resourceKey("hose"))));
        HOSE_NOZZLE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "hose_nozzle"), new Item(new Item.Properties().setId(resourceKey("hose_nozzle"))));
        BLANK_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "blank_upgrade"), new TierUpgrade(new Item.Properties().setId(resourceKey("blank_upgrade")), TierUpgrade.Upgrade.BLANK_UPGRADE));
        IRON_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "iron_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16).setId(resourceKey("iron_tier_upgrade")), TierUpgrade.Upgrade.IRON_TIER_UPGRADE));
        GOLD_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "gold_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16).setId(resourceKey("gold_tier_upgrade")), TierUpgrade.Upgrade.GOLD_TIER_UPGRADE));
        DIAMOND_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "diamond_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16).setId(resourceKey("diamond_tier_upgrade")), TierUpgrade.Upgrade.DIAMOND_TIER_UPGRADE));
        NETHERITE_TIER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "netherite_tier_upgrade"), new TierUpgrade(new Item.Properties().stacksTo(16).setId(resourceKey("netherite_tier_upgrade")), TierUpgrade.Upgrade.NETHERITE_TIER_UPGRADE));
        TANKS_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "tanks_upgrade"), new TanksUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("tanks_upgrade"))));
        CRAFTING_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "crafting_upgrade"), new CraftingUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("crafting_upgrade"))));
        FURNACE_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "furnace_upgrade"), new FurnaceUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("furnace_upgrade"))));
        SMOKER_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "smoker_upgrade"), new SmokerUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("smoker_upgrade"))));
        BLAST_FURNACE_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "blast_furnace_upgrade"), new BlastFurnaceUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("blast_furnace_upgrade"))));
        PICKUP_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "pickup_upgrade"), new PickupUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("pickup_upgrade"))));
        MAGNET_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "magnet_upgrade"), new MagnetUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("magnet_upgrade"))));
        JUKEBOX_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "jukebox_upgrade"), new JukeboxUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("jukebox_upgrade"))));
        VOID_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "void_upgrade"), new VoidUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("void_upgrade"))));
        FEEDING_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "feeding_upgrade"), new FeedingUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("feeding_upgrade"))));
        REFILL_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "refill_upgrade"), new RefillUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("refill_upgrade"))));
        LANTERN_UPGRADE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "lantern_upgrade"), new LanternUpgradeItem(new Item.Properties().stacksTo(16).setId(resourceKey("lantern_upgrade"))));
        BACKPACK_ITEM_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), EntityType.Builder.of(BackpackItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20).build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"))));
    }

    public static Identifier resourceLocation(String name) {
        return Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, name);
    }

    public static ResourceKey<Item> resourceKey(String name) {
        return ResourceKey.create(Registries.ITEM, resourceLocation(name));
    }

    public static final List<Item> COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES = new ArrayList<>();
    public static final List<Item> COMPATIBLE_NETHER_BACKPACK_ENTRIES = new ArrayList<>();

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