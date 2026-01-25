package com.tiviacz.travelersbackpack.config;

import com.google.common.collect.Multimap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class TravelersBackpackConfig {
    public static class Server {
        private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";

        public final BackpackSettings backpackSettings;
        public final BackpackUpgrades backpackUpgrades;
        public final World world;
        public final BackpackAbilities backpackAbilities;
        public final SlownessDebuff slownessDebuff;

        Server(final ForgeConfigSpec.Builder builder) {
            builder.comment("Server config settings")
                    .push("server");

            //Backpack Settings
            backpackSettings = new BackpackSettings(builder, "backpackSettings");

            //Backpack Upgrades
            backpackUpgrades = new BackpackUpgrades(builder, "backpackUpgrades");

            //World
            world = new World(builder, "world");

            //Abilities
            backpackAbilities = new BackpackAbilities(builder, "backpackAbilities");

            //Slowness Debuff
            slownessDebuff = new SlownessDebuff(builder, "slownessDebuff");

            builder.pop();
        }

        public static class BackpackUpgrades {
            public final ForgeConfigSpec.BooleanValue enableTanksUpgrade;
            public final ForgeConfigSpec.BooleanValue enableCraftingUpgrade;
            public final ForgeConfigSpec.BooleanValue enableFurnaceUpgrade;
            public final ForgeConfigSpec.BooleanValue enableSmokerUpgrade;
            public final ForgeConfigSpec.BooleanValue enableBlastFurnaceUpgrade;
            public final FilterUpgradeSettings pickupUpgradeSettings;
            public final ForgeConfigSpec.BooleanValue enableJukeboxUpgrade;
            public final MagnetUpgradeSettings magnetUpgradeSettings;
            public final FeedingUpgradeSettings feedingUpgradeSettings;
            public final RefillUpgradeSettings refillUpgradeSettings;
            public final FilterUpgradeSettings voidUpgradeSettings;

            public BackpackUpgrades(final ForgeConfigSpec.Builder builder, final String path) {
                builder.push(path);

                enableTanksUpgrade = builder
                        .define("enableTanksUpgrade", true);

                enableCraftingUpgrade = builder
                        .define("enableCraftingUpgrade", true);

                enableFurnaceUpgrade = builder
                        .define("enableFurnaceUpgrade", true);

                enableSmokerUpgrade = builder
                        .define("enableSmokerUpgrade", true);

                enableBlastFurnaceUpgrade = builder
                        .define("enableBlastFurnaceUpgrade", true);

                pickupUpgradeSettings = new FilterUpgradeSettings(builder, "pickupUpgradeSettings", "PickupUpgrade");

                enableJukeboxUpgrade = builder
                        .define("enableJukeboxUpgrade", true);

                magnetUpgradeSettings = new MagnetUpgradeSettings(builder, "magnetUpgradeSettings");

                feedingUpgradeSettings = new FeedingUpgradeSettings(builder, "feedingUpgradeSettings");

                refillUpgradeSettings = new RefillUpgradeSettings(builder, "refillUpgradeSettings");

                voidUpgradeSettings = new FilterUpgradeSettings(builder, "voidUpgradeSettings", "VoidUpgrade");

                builder.pop();
            }

            public static class FilterUpgradeSettings {
                public final ForgeConfigSpec.BooleanValue enableUpgrade;
                public final ForgeConfigSpec.IntValue filterSlotCount;
                public final ForgeConfigSpec.IntValue slotsInRow;

                public FilterUpgradeSettings(final ForgeConfigSpec.Builder builder, final String path, final String upgradeName) {
                    builder.push(path);

                    enableUpgrade = builder
                            .define("enable" + upgradeName, true);

                    filterSlotCount = builder
                            .defineInRange("filterSlotCount", 9, 1, 20);

                    slotsInRow = builder
                            .defineInRange("slotsInRow", 3, 1, 5);

                    builder.pop();
                }
            }

            public static class FeedingUpgradeSettings {
                public final ForgeConfigSpec.BooleanValue enableFeedingUpgrade;
                public final ForgeConfigSpec.IntValue filterSlotCount;
                public final ForgeConfigSpec.IntValue slotsInRow;
                public final ForgeConfigSpec.IntValue tickRate;

                public FeedingUpgradeSettings(final ForgeConfigSpec.Builder builder, final String path) {
                    builder.push(path);

                    enableFeedingUpgrade = builder
                            .define("enableFeedingUpgrade", true);

                    filterSlotCount = builder
                            .defineInRange("filterSlotCount", 9, 1, 20);

                    slotsInRow = builder
                            .defineInRange("slotsInRow", 3, 1, 5);

                    tickRate = builder
                            .defineInRange("tickRate", 100, 1, 1000);

                    builder.pop();
                }
            }

            public static class MagnetUpgradeSettings {
                public final ForgeConfigSpec.BooleanValue enableMagnetUpgrade;
                public final ForgeConfigSpec.IntValue filterSlotCount;
                public final ForgeConfigSpec.IntValue slotsInRow;
                public final ForgeConfigSpec.IntValue pullRange;
                public final ForgeConfigSpec.IntValue tickRate;

                public MagnetUpgradeSettings(final ForgeConfigSpec.Builder builder, final String path) {
                    builder.push(path);

                    enableMagnetUpgrade = builder
                            .define("enableMagnetUpgrade", true);

                    filterSlotCount = builder
                            .defineInRange("filterSlotCount", 9, 1, 20);

                    slotsInRow = builder
                            .defineInRange("slotsInRow", 3, 1, 5);

                    pullRange = builder
                            .defineInRange("pullRange", 5, 1, 20);

                    tickRate = builder
                            .defineInRange("tickRate", 10, 1, 1000);

                    builder.pop();
                }
            }

            public static class RefillUpgradeSettings {
                public final ForgeConfigSpec.BooleanValue enableRefillUpgrade;
                public final ForgeConfigSpec.IntValue filterSlotCount;
                public final ForgeConfigSpec.IntValue slotsInRow;
                public final ForgeConfigSpec.IntValue tickRate;

                public RefillUpgradeSettings(final ForgeConfigSpec.Builder builder, final String path) {
                    builder.push(path);

                    enableRefillUpgrade = builder
                            .define("enableRefillUpgrade", true);

                    filterSlotCount = builder
                            .defineInRange("filterSlotCount", 9, 1, 20);

                    slotsInRow = builder
                            .defineInRange("slotsInRow", 3, 1, 5);

                    tickRate = builder
                            .defineInRange("tickRate", 5, 1, 1000);

                    builder.pop();
                }
            }
        }

        public static class BackpackSettings {
            public final BackpackSettings.TierConfig leather;
            public final BackpackSettings.TierConfig iron;
            public final BackpackSettings.TierConfig gold;
            public final BackpackSettings.TierConfig diamond;
            public final BackpackSettings.TierConfig netherite;
            public final ForgeConfigSpec.BooleanValue rightClickEquip;
            public final ForgeConfigSpec.BooleanValue rightClickUnequip;
            public final ForgeConfigSpec.BooleanValue allowOnlyEquippedBackpack;
            public final ForgeConfigSpec.BooleanValue allowOpeningFromSlot;
            public final ForgeConfigSpec.BooleanValue preventMultiplePlayersAccess;
            public final ForgeConfigSpec.BooleanValue invulnerableBackpack;
            public final ForgeConfigSpec.BooleanValue allowToolSwapping;
            public final ForgeConfigSpec.BooleanValue toolSlotsAcceptEverything;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> toolSlotsAcceptableItems;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedItems;
            public final ForgeConfigSpec.BooleanValue allowShulkerBoxes;
            public final ForgeConfigSpec.BooleanValue voidProtection;
            public final ForgeConfigSpec.BooleanValue backpackDeathPlace;
            public final ForgeConfigSpec.BooleanValue backpackForceDeathPlace;
            public final ForgeConfigSpec.BooleanValue quickSleepingBag;
            public final ForgeConfigSpec.BooleanValue enableSleepingBagSpawnPoint;
            public final ForgeConfigSpec.BooleanValue backSlotIntegration;

            BackpackSettings(final ForgeConfigSpec.Builder builder, final String path) {
                builder.push(path);

                //Backpack Settings
                leather = new BackpackSettings.TierConfig(builder, "Leather", 27, 2, 2, 1000);
                iron = new BackpackSettings.TierConfig(builder, "Iron", 45, 3, 3, 1000);
                gold = new BackpackSettings.TierConfig(builder, "Gold", 63, 4, 4, 1000);
                diamond = new BackpackSettings.TierConfig(builder, "Diamond", 81, 5, 5, 1000);
                netherite = new BackpackSettings.TierConfig(builder, "Netherite", 99, 6, 6, 1000);

                rightClickEquip = builder
                        .comment("Enables equipping the backpack on right-click from the ground")
                        .define("rightClickEquip", true);

                rightClickUnequip = builder
                        .comment("Enables unequipping the backpack on right-click on the ground with empty hand")
                        .define("rightClickUnequip", false);

                allowOnlyEquippedBackpack = builder
                        .comment("Allows to use only equipped backpack")
                        .define("allowOnlyEquippedBackpack", false);

                allowOpeningFromSlot = builder
                        .comment("Allows opening the backpack by pressing a keybind while hovering over the slot with backpack in the player's inventory")
                        .define("allowOpeningFromSlot", false);

                preventMultiplePlayersAccess = builder
                        .comment("Prevents more than one player from accessing the backpack at the same time when it's placed on the ground")
                        .define("preventMultiplePlayersAccess", false);

                invulnerableBackpack = builder
                        .comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
                        .define("invulnerableBackpack", true);

                allowToolSwapping = builder
                        .comment("Allows swapping tools between tool slots and the player’s inventory via a quick-swap menu")
                        .define("allowToolSwapping", true);

                toolSlotsAcceptEverything = builder
                        .comment("Tool slots accept any item")
                        .define("toolSlotsAcceptEverything", false);

                toolSlotsAcceptableItems = builder
                        .comment("List of items that can be put in tool slots (Use registry names, for example: \"minecraft:apple\", \"minecraft:flint\")")
                        .defineList("toolSlotsAcceptableItems", Collections.emptyList(), mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                blacklistedItems = builder
                        .comment("List of items that can't be put in backpack inventory (Use registry names, for example: \"minecraft:apple\", \"minecraft:flint\")")
                        .defineList("blacklistedItems", Collections.emptyList(), mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                allowShulkerBoxes = builder
                        .comment("Allows putting shulker boxes and other items with inventory in backpack")
                        .define("allowShulkerBoxes", false);

                voidProtection = builder
                        .comment("Prevents backpack disappearing in void, spawns floating backpack above minimum Y when player dies in void")
                        .define("voidProtection", true);

                backpackDeathPlace = builder
                        .comment("Places backpack at place where player died")
                        .define("backpackDeathPlace", true);

                backpackForceDeathPlace = builder
                        .comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
                        .define("backpackForceDeathPlace", false);

                quickSleepingBag = builder
                        .comment("Allows sleeping in a sleeping bag without the need to unequip and place the backpack on the ground")
                        .define("quickSleepingBag", true);

                enableSleepingBagSpawnPoint = builder
                        .define("enableSleepingBagSpawnPoint", false);

                backSlotIntegration = builder
                        .comment("Backpacks can only be equipped in the Curios/Accessories 'Back' slot, provided those mods are installed. If set to false, backpacks can only be equipped by clicking the button in the Backpack GUI. " +
                                "This setting can be changed without unequipping the backpack. An already equipped backpack will not disappear and can be retrieved from the player's inventory.")
                        .define("backSlotIntegration", true);

                builder.pop();
            }

            public static class TierConfig {
                public final ForgeConfigSpec.IntValue inventorySlotCount;
                public final ForgeConfigSpec.IntValue upgradeSlotCount;
                public final ForgeConfigSpec.IntValue toolSlotCount;
                public final ForgeConfigSpec.IntValue tankCapacityPerRow;

                public TierConfig(ForgeConfigSpec.Builder builder, String tier, int inventorySlotCountDefault, int upgradeSlotCountDefault, int toolSlotCountDefault, int tankCapacityPerRowDefault) {
                    builder.comment(tier + " Tier Backpack Settings").push(tier.toLowerCase(Locale.ENGLISH) + "TierBackpack");

                    inventorySlotCount =
                            builder.comment("Number of inventory slots for the tier")
                                    .defineInRange("inventorySlotCount", inventorySlotCountDefault, 1, 154);

                    upgradeSlotCount =
                            builder.comment("Number of upgrade slots for the tier")
                                    .defineInRange("upgradeSlotCount", upgradeSlotCountDefault, 0, 10);

                    toolSlotCount =
                            builder.comment("Number of tool slots for the tier")
                                    .defineInRange("toolSlotCount", toolSlotCountDefault, 0, 8);

                    tankCapacityPerRow =
                            builder.comment("Tank capacity per row of backpack storage, 1000 equals 1 Bucket (Leather backpack 3 rows of 9 slots = 3 * 1000")
                                    .defineInRange("tankCapacity", tankCapacityPerRowDefault, 1, 100000);

                    builder.pop();
                }
            }

            public record Tier(int inventorySlotCount, int toolSlotCount, int tankCapacity) {
            }
        }

        public static class World {
            public final ForgeConfigSpec.BooleanValue spawnEntitiesWithBackpack;
            public final ForgeConfigSpec.DoubleValue chance;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> possibleOverworldEntityTypes;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> possibleNetherEntityTypes;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> overworldBackpacks;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> netherBackpacks;

            World(final ForgeConfigSpec.Builder builder, final String path) {
                builder.push(path);

                spawnEntitiesWithBackpack = builder
                        .comment("Enables chance to spawn Zombie, Skeleton, Wither Skeleton, Piglin or Enderman with random backpack equipped")
                        .define("spawnEntitiesWithBackpack", true);

                chance = builder
                        .comment("Defines spawn chance of entity with a backpack")
                        .defineInRange("chance", 0.005, 0, 1);

                possibleOverworldEntityTypes = builder
                        .comment("List of overworld entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
                        .defineList("possibleOverworldEntityTypes", this::getPossibleOverworldEntityTypes, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                possibleNetherEntityTypes = builder
                        .comment("List of nether entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
                        .defineList("possibleNetherEntityTypes", this::getPossibleNetherEntityTypes, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                overworldBackpacks = builder
                        .comment("List of backpacks that can spawn on overworld mobs")
                        .defineList("overworldBackpacks", this::getOverworldBackpacksList, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                netherBackpacks = builder
                        .comment("List of backpacks that can spawn on nether mobs")
                        .defineList("netherBackpacks", this::getNetherBackpacksList, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                builder.pop();
            }

            private List<String> getPossibleOverworldEntityTypes() {
                List<String> ret = new ArrayList<>();
                ret.add("minecraft:zombie");
                ret.add("minecraft:skeleton");
                ret.add("minecraft:enderman");
                return ret;
            }

            private List<String> getPossibleNetherEntityTypes() {
                List<String> ret = new ArrayList<>();
                ret.add("minecraft:wither_skeleton");
                ret.add("minecraft:piglin");
                return ret;
            }


            private List<String> getOverworldBackpacksList() {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:standard");
                ret.add("travelersbackpack:diamond");
                ret.add("travelersbackpack:gold");
                ret.add("travelersbackpack:emerald");
                ret.add("travelersbackpack:iron");
                ret.add("travelersbackpack:lapis");
                ret.add("travelersbackpack:redstone");
                ret.add("travelersbackpack:coal");
                ret.add("travelersbackpack:bookshelf");
                ret.add("travelersbackpack:sandstone");
                ret.add("travelersbackpack:snow");
                ret.add("travelersbackpack:sponge");
                ret.add("travelersbackpack:cake");
                ret.add("travelersbackpack:cactus");
                ret.add("travelersbackpack:hay");
                ret.add("travelersbackpack:melon");
                ret.add("travelersbackpack:pumpkin");
                ret.add("travelersbackpack:creeper");
                ret.add("travelersbackpack:enderman");
                ret.add("travelersbackpack:skeleton");
                ret.add("travelersbackpack:spider");
                ret.add("travelersbackpack:bee");
                ret.add("travelersbackpack:wolf");
                ret.add("travelersbackpack:fox");
                ret.add("travelersbackpack:ocelot");
                ret.add("travelersbackpack:horse");
                ret.add("travelersbackpack:cow");
                ret.add("travelersbackpack:pig");
                ret.add("travelersbackpack:sheep");
                ret.add("travelersbackpack:chicken");
                ret.add("travelersbackpack:squid");
                return ret;
            }

            private List<String> getNetherBackpacksList() {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:quartz");
                ret.add("travelersbackpack:nether");
                ret.add("travelersbackpack:blaze");
                ret.add("travelersbackpack:ghast");
                ret.add("travelersbackpack:magma_cube");
                ret.add("travelersbackpack:wither");
                return ret;
            }
        }

        public static class BackpackAbilities {
            private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";
            private static final String EFFECT_ABILITY_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+),\\s*([a-z0-9_.-]+:[a-z0-9_/.-]+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)";
            private static final String COOLDOWNS_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+),\\s*(\\d+),\\s*(\\d+)";

            public final ForgeConfigSpec.BooleanValue enableBackpackAbilities;
            public final ForgeConfigSpec.BooleanValue forceAbilityEnabled;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedAbilities;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> backpackEffects;
            public final ForgeConfigSpec.ConfigValue<List<? extends String>> cooldowns;

            BackpackAbilities(final ForgeConfigSpec.Builder builder, final String path) {
                builder.push(path);

                enableBackpackAbilities = builder
                        .define("enableBackpackAbilities", true);

                forceAbilityEnabled = builder
                        .comment("Newly crafted backpacks will have ability enabled by default")
                        .define("forceAbilityEnabled", true);

                allowedAbilities = builder
                        .comment("List of backpacks that are allowed to have an ability. DO NOT ADD anything to this list, because the game will crash, remove entries if backpack should not have ability")
                        .defineList("allowedAbilities", this::getAllowedAbilities, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                backpackEffects = builder
                        .comment("List of effect abilities associated with backpacks, you can modify this list as you wish. Different effects can be added to different backpacks. \n Formatting: \"<backpack_registry_name>, <status_effect_registry_name>, <min_duration_ticks>, <max_duration_ticks>, <amplifier>\"")
                        .defineList("backpackEffects", this::getBackpackEffects, mapping -> ((String)mapping).matches(EFFECT_ABILITY_MATCHER));

                cooldowns = builder
                        .comment("List of cooldowns that are being applied after ability usage, the backpacks on the list are all that currently have cooldowns, adding additional backpack will not give it cooldown. \n Formatting: \"<backpack_registry_name>, <min_possible_cooldown_seconds>, <max_possible_cooldown_seconds>\"")
                        .defineList("cooldowns", this::getCooldowns, mapping -> ((String)mapping).matches(COOLDOWNS_MATCHER));

                builder.pop();
            }

            private List<String> getCooldowns() {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:creeper, 1200, 1800");
                ret.add("travelersbackpack:cow, 480, 540");
                ret.add("travelersbackpack:chicken, 360, 600");
                ret.add("travelersbackpack:cake, 360, 480");
                ret.add("travelersbackpack:melon, 120, 480");
                return ret;
            }

            private List<String> getBackpackEffects() {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:bat, minecraft:night_vision, 260, 300, 0");
                ret.add("travelersbackpack:magma_cube, minecraft:fire_resistance, 260, 300, 0");
                ret.add("travelersbackpack:squid, minecraft:water_breathing, 260, 300, 0");
                ret.add("travelersbackpack:squid, minecraft:night_vision, 260, 300, 0");
                ret.add("travelersbackpack:dragon, minecraft:regeneration, 260, 300, 0");
                ret.add("travelersbackpack:dragon, minecraft:strength, 250, 290, 0");
                ret.add("travelersbackpack:quartz, minecraft:haste, 260, 300, 0");
                ret.add("travelersbackpack:fox, minecraft:jump_boost, 260, 300, 0");
                return ret;
            }

            private List<String> getAllowedAbilities() {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:netherite");
                ret.add("travelersbackpack:diamond");
                ret.add("travelersbackpack:gold");
                ret.add("travelersbackpack:emerald");
                ret.add("travelersbackpack:iron");
                ret.add("travelersbackpack:lapis");
                ret.add("travelersbackpack:redstone");
                ret.add("travelersbackpack:bookshelf");
                ret.add("travelersbackpack:sponge");
                ret.add("travelersbackpack:cake");
                ret.add("travelersbackpack:cactus");
                ret.add("travelersbackpack:melon");
                ret.add("travelersbackpack:pumpkin");
                ret.add("travelersbackpack:creeper");
                ret.add("travelersbackpack:dragon");
                ret.add("travelersbackpack:enderman");
                ret.add("travelersbackpack:blaze");
                ret.add("travelersbackpack:ghast");
                ret.add("travelersbackpack:magma_cube");
                ret.add("travelersbackpack:spider");
                ret.add("travelersbackpack:wither");
                ret.add("travelersbackpack:warden");
                ret.add("travelersbackpack:bat");
                ret.add("travelersbackpack:bee");
                ret.add("travelersbackpack:ocelot");
                ret.add("travelersbackpack:cow");
                ret.add("travelersbackpack:chicken");
                ret.add("travelersbackpack:squid");
                ret.add("travelersbackpack:hay");
                ret.add("travelersbackpack:fox");
                return ret;
            }
        }

        public static class SlownessDebuff {
            public final ForgeConfigSpec.BooleanValue tooManyBackpacksSlowness;
            public final ForgeConfigSpec.IntValue maxNumberOfBackpacks;
            public final ForgeConfigSpec.DoubleValue slownessPerExcessedBackpack;

            SlownessDebuff(final ForgeConfigSpec.Builder builder, final String path) {
                builder.push(path);

                tooManyBackpacksSlowness = builder
                        .comment("Player gets slowness effect, if carries too many backpacks in inventory")
                        .define("tooManyBackpacksSlowness", false);

                maxNumberOfBackpacks = builder
                        .comment("Maximum number of backpacks, which can be carried in inventory, without slowness effect")
                        .defineInRange("maxNumberOfBackpacks", 3, 1, 37);

                slownessPerExcessedBackpack = builder
                        .defineInRange("slownessPerExcessedBackpack", 1, 0.1, 5);

                builder.pop();
            }
        }

        public void loadItemsFromConfig(List<? extends String> configList, List<Item> targetList) {
            for(String registryName : configList) {
                ResourceLocation res = ResourceLocation.tryParse(registryName);

                if(BuiltInRegistries.ITEM.containsKey(res)) {
                    targetList.add(BuiltInRegistries.ITEM.get(res));
                }
            }
        }

        public void loadEntityTypesFromConfig(List<? extends String> configList, List<EntityType> targetList) {
            for(String registryName : configList) {
                ResourceLocation res = ResourceLocation.tryParse(registryName);

                if(BuiltInRegistries.ENTITY_TYPE.containsKey(res)) {
                    targetList.add(BuiltInRegistries.ENTITY_TYPE.get(res));
                }
            }
        }

        public void loadBackpackEffectsFromConfig(List<? extends String> configList, Multimap<Item, BackpackEffect> backpackEffects) {
            try {
                for(String entry : configList) {
                    String[] parts = entry.replace(" ", "").split(",");
                    if(parts.length == 5) {
                        ResourceLocation backpackRes = ResourceLocation.tryParse(parts[0]);
                        ResourceLocation effectRes = ResourceLocation.tryParse(parts[1]);

                        if(BuiltInRegistries.ITEM.containsKey(backpackRes) && BuiltInRegistries.MOB_EFFECT.containsKey(effectRes)) {
                            Item backpack = BuiltInRegistries.ITEM.get(backpackRes);
                            int minDuration = Integer.parseInt(parts[2]);
                            int maxDuration = Integer.parseInt(parts[3]);
                            int amplifier = Integer.parseInt(parts[4]);

                            if(minDuration < 0 || maxDuration < 0 || amplifier < 0) {
                                TravelersBackpack.LOGGER.error("Backpack Effects: duration and amplifier must be positive integers!");
                            }

                            if(minDuration > maxDuration) {
                                TravelersBackpack.LOGGER.error("Backpack Effects: minDuration must be less than or equal to maxDuration!");
                            }

                            backpackEffects.put(backpack, new BackpackEffect(BuiltInRegistries.MOB_EFFECT.get(effectRes), minDuration, maxDuration, amplifier));
                        }
                    }
                }
            } catch(Exception e) {
                TravelersBackpack.LOGGER.error("Could not load Backpack Effect from Config! Check your config if entries are correct!");
            }
        }

        public void loadCooldownsFromConfig(List<? extends String> config, Map<Item, Cooldown> cooldownConfigs) {
            try {
                for(String entry : config) {
                    String[] parts = entry.replace(" ", "").split(",");
                    if(parts.length == 3) {
                        ResourceLocation backpackRes = ResourceLocation.tryParse(parts[0]);
                        Item backpack = BuiltInRegistries.ITEM.get(backpackRes);
                        int minCooldown = Integer.parseInt(parts[1]);
                        int maxCooldown = Integer.parseInt(parts[2]);

                        if(minCooldown < 0 || maxCooldown < 0) {
                            TravelersBackpack.LOGGER.error("Cooldowns: cooldowns must be positive integers!");
                        }

                        if(minCooldown > maxCooldown) {
                            TravelersBackpack.LOGGER.error("Cooldowns: minCooldown must be less than or equal to maxCooldown!");
                        }

                        cooldownConfigs.put(backpack, new Cooldown(minCooldown, maxCooldown));
                    }
                }
            } catch(Exception e) {
                TravelersBackpack.LOGGER.error("Could not load Cooldowns from Config! Check your config if entries are correct!");
            }
        }

        private boolean initialized = false;

        public void initializeLists() {
            if(!serverSpec.isLoaded()) {
                return;
            }

            if(!initialized) {
                //Container
                loadItemsFromConfig(TravelersBackpackConfig.SERVER.backpackSettings.toolSlotsAcceptableItems.get(), ToolSlotItemHandler.TOOL_SLOTS_ACCEPTABLE_ITEMS);
                loadItemsFromConfig(TravelersBackpackConfig.SERVER.backpackSettings.blacklistedItems.get(), BackpackSlotItemHandler.BLACKLISTED_ITEMS);

                //Spawns
                loadItemsFromConfig(TravelersBackpackConfig.SERVER.world.overworldBackpacks.get(), ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES);
                loadItemsFromConfig(TravelersBackpackConfig.SERVER.world.netherBackpacks.get(), ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES);

                //Abilities
                loadItemsFromConfig(TravelersBackpackConfig.SERVER.backpackAbilities.allowedAbilities.get(), com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES);

                //Entities
                loadEntityTypesFromConfig(TravelersBackpackConfig.SERVER.world.possibleOverworldEntityTypes.get(), Reference.ALLOWED_TYPE_ENTRIES);
                loadEntityTypesFromConfig(TravelersBackpackConfig.SERVER.world.possibleNetherEntityTypes.get(), Reference.ALLOWED_TYPE_ENTRIES);

                //Backpack Effects
                loadBackpackEffectsFromConfig(TravelersBackpackConfig.SERVER.backpackAbilities.backpackEffects.get(), com.tiviacz.travelersbackpack.common.BackpackAbilities.BACKPACK_EFFECTS);

                //Update allowed abilities if added effect
                com.tiviacz.travelersbackpack.common.BackpackAbilities.getBackpackEffects().entries().stream().forEach(entry -> {
                    if(!com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES.contains(entry.getKey())) {
                        com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES.add(entry.getKey());
                    }
                    if(!com.tiviacz.travelersbackpack.common.BackpackAbilities.ITEM_ABILITIES_LIST.contains(entry.getKey())) {
                        com.tiviacz.travelersbackpack.common.BackpackAbilities.ITEM_ABILITIES_LIST.add(entry.getKey());
                    }
                });

                //Cooldowns
                loadCooldownsFromConfig(TravelersBackpackConfig.SERVER.backpackAbilities.cooldowns.get(), com.tiviacz.travelersbackpack.common.BackpackAbilities.COOLDOWNS);
            }

            initialized = true;
        }
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableLoot;
        public final ForgeConfigSpec.BooleanValue enableVillagerTrade;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Common config settings")
                    .push("common");

            enableLoot = builder
                    .comment("Enables backpacks spawning in loot chests")
                    .define("enableLoot", true);

            enableVillagerTrade = builder
                    .comment("Enables trade for Villager Backpack in Librarian villager trades")
                    .define("enableVillagerTrade", true);

            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue showBackpackIconInInventory;
        public final ForgeConfigSpec.BooleanValue sendBackpackCoordinatesMessage;
        public final ForgeConfigSpec.BooleanValue obtainTips;
        public final ForgeConfigSpec.BooleanValue renderTools;
        public final ForgeConfigSpec.BooleanValue showSupporterBadge;
        public final ToolsOverlay toolsOverlay;
        public final Overlay overlay;

        Client(final ForgeConfigSpec.Builder builder) {
            builder.comment("Client-only settings")
                    .push("client");

            showBackpackIconInInventory = builder
                    .comment("Whether the backpack icon should be visible in player's inventory")
                    .define("showBackpackIconInInventory", true);

            sendBackpackCoordinatesMessage = builder
                    .comment("Sends a message to the player on death with backpack coordinates")
                    .define("sendBackpackCoordinatesMessage", true);

            obtainTips = builder
                    .comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
                    .define("obtainTips", true);

            renderTools = builder
                    .comment("Render tools in tool slots on the backpack, while worn")
                    .define("renderTools", true);

            showSupporterBadge = builder
                    .comment("Only for supporters, option to show/hide the Supporter Star Badge. If you want to receive the Supporter Star Badge, visit my Ko-fi page :)! - https://ko-fi.com/tiviacz1337")
                    .define("showSupporterBadge", true);

            toolsOverlay = new ToolsOverlay(builder, "The position of the Tools Overlay on the screen", "toolsOverlay");

            overlay = new Overlay(
                    builder,
                    "The position of the Overlay on the screen",
                    "overlay",
                    true, 20, 30
            );

            builder.pop();
        }

        public static class ToolsOverlay {
            public final ForgeConfigSpec.IntValue offsetX;
            public final ForgeConfigSpec.IntValue offsetY;

            ToolsOverlay(final ForgeConfigSpec.Builder builder, final String comment, final String path) {
                builder.comment(comment)
                        .push(path);
                offsetX = builder
                        .comment("X offset")
                        .defineInRange("offsetX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

                offsetY = builder
                        .comment("Y offset")
                        .defineInRange("offsetY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }
        }

        public static class Overlay {
            public final ForgeConfigSpec.BooleanValue enableOverlay;
            public final ForgeConfigSpec.IntValue offsetX;
            public final ForgeConfigSpec.IntValue offsetY;

            Overlay(final ForgeConfigSpec.Builder builder, final String comment, final String path, final boolean defaultOverlay, final int defaultX, final int defaultY) {
                builder.comment(comment)
                        .push(path);

                enableOverlay = builder
                        .comment("Enables tanks and tool slots overlay, while backpack is worn")
                        .define("enableOverlay", defaultOverlay);

                offsetX = builder
                        .comment("Offsets to left side")
                        .defineInRange("offsetX", defaultX, Integer.MIN_VALUE, Integer.MAX_VALUE);

                offsetY = builder
                        .comment("Offsets to up")
                        .defineInRange("offsetY", defaultY, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }
        }
    }

    //Server
    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    //Common
    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    //Client
    public static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}