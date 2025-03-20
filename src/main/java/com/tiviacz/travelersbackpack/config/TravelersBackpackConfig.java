package com.tiviacz.travelersbackpack.config;

import com.google.common.collect.Multimap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class TravelersBackpackConfig {
    public static TravelersBackpackConfigData getConfig() {
        return AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).getConfig();
    }

    public static void saveConfig() {
        AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).save();
    }

    public static void register() {
        AutoConfig.register(TravelersBackpackConfigData.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) -> AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load());

        //Register Config load listener
        AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).registerLoadListener((holder, config) -> {

            //Abilities
            BackpackAbilities.ALLOWED_ABILITIES.clear();
            loadItemsFromConfig(TravelersBackpackConfig.getConfig().backpackAbilities.allowedAbilities, com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES);

            //Load Backpack Effects
            BackpackAbilities.getBackpackEffects().clear();
            loadBackpackEffectsFromConfig(config.backpackAbilities.backpackEffects, com.tiviacz.travelersbackpack.common.BackpackAbilities.BACKPACK_EFFECTS);

            //Update allowed abilities if added effect
            com.tiviacz.travelersbackpack.common.BackpackAbilities.getBackpackEffects().entries().stream().forEach(entry -> {
                if(!com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES.contains(entry.getKey())) {
                    com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES.add(entry.getKey());
                }
                if(!com.tiviacz.travelersbackpack.common.BackpackAbilities.ITEM_ABILITIES_LIST.contains(entry.getKey())) {
                    com.tiviacz.travelersbackpack.common.BackpackAbilities.ITEM_ABILITIES_LIST.add(entry.getKey());
                }
            });

            //Remove all abilities that are not allowed //#TODO probably tweak
            List<Item> allowed = new ArrayList<>(BackpackAbilities.ALLOWED_ABILITIES);
            BackpackAbilities.ITEM_ABILITIES_LIST.removeIf(item -> !allowed.contains(item));
            BackpackAbilities.BLOCK_ABILITIES_LIST.removeIf(item -> !allowed.contains(item));

            //Cooldowns
            BackpackAbilities.getCooldowns().clear();
            loadCooldownsFromConfig(config.backpackAbilities.cooldowns, com.tiviacz.travelersbackpack.common.BackpackAbilities.COOLDOWNS);

            return InteractionResult.SUCCESS;
        });
    }

    public static boolean isToolAllowed(ItemStack value) {
        return isOnItemList(value, getConfig().backpackSettings.toolSlotsAcceptableItems);
    }

    public static boolean isItemBlacklisted(ItemStack value) {
        return isOnItemList(value, getConfig().backpackSettings.blacklistedItems);
    }

    public static boolean isOverworldEntityTypePossible(Entity value) {
        return isOnEntityList(value, getConfig().world.possibleOverworldEntityTypes);
    }

    public static boolean isNetherEntityTypePossible(Entity value) {
        return isOnEntityList(value, getConfig().world.possibleOverworldEntityTypes);
    }

    public static boolean isOnEntityList(Entity value, String[] list) {
        return Arrays.stream(list).anyMatch(p -> p.equals(BuiltInRegistries.ENTITY_TYPE.getKey(value.getType()).toString()));
    }

    public static boolean isOnItemList(ItemStack value, String[] list) {
        return Arrays.stream(list).anyMatch(p -> p.equals(BuiltInRegistries.ITEM.getKey(value.getItem()).toString()));
    }

    public static Item getRandomCompatibleOverworldBackpackEntry(RandomSource random) {
        String[] backpacks = getConfig().world.overworldBackpacks;
        String selectedBackpack = backpacks[random.nextInt(backpacks.length)];

        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(selectedBackpack)).orElseThrow(() -> new NoSuchElementException("Wrong backpack registry name specified in the config!"));
    }

    public static Item getRandomCompatibleNetherBackpackEntry(RandomSource random) {
        String[] backpacks = getConfig().world.netherBackpacks;
        String selectedBackpack = backpacks[random.nextInt(backpacks.length)];

        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(selectedBackpack)).orElseThrow(() -> new NoSuchElementException("Wrong backpack registry name specified in the config!"));
    }

    public static CompoundTag writeToNbt() {
        TravelersBackpackConfigData data = getConfig();
        CompoundTag nbt = new CompoundTag();

        //Backpack Upgrades
        nbt.putBoolean("backpackUpgrades.enableTanksUpgrade", data.backpackUpgrades.enableTanksUpgrade);
        nbt.putBoolean("backpackUpgrades.enableCraftingUpgrade", data.backpackUpgrades.enableCraftingUpgrade);
        nbt.putBoolean("backpackUpgrades.enableJukeboxUpgrade", data.backpackUpgrades.enableJukeboxUpgrade);
        //Pickup
        nbt.putBoolean("backpackUpgrades.pickupUpgradeSettings.enableUpgrade", data.backpackUpgrades.pickupUpgradeSettings.enableUpgrade);
        nbt.putInt("backpackUpgrades.pickupUpgradeSettings.filterSlotCount", data.backpackUpgrades.pickupUpgradeSettings.filterSlotCount);
        //Magnet
        nbt.putBoolean("backpackUpgrades.magnetUpgradeSettings.enableUpgrade", data.backpackUpgrades.magnetUpgradeSettings.enableUpgrade);
        nbt.putInt("backpackUpgrades.magnetUpgradeSettings.filterSlotCount", data.backpackUpgrades.magnetUpgradeSettings.filterSlotCount);
        nbt.putInt("backpackUpgrades.magnetUpgradeSettings.pullRange", data.backpackUpgrades.magnetUpgradeSettings.pullRange);
        nbt.putInt("backpackUpgrades.magnetUpgradeSettings.tickRate", data.backpackUpgrades.magnetUpgradeSettings.tickRate);
        //Feeding
        nbt.putBoolean("backpackUpgrades.feedingUpgradeSettings.enableUpgrade", data.backpackUpgrades.feedingUpgradeSettings.enableUpgrade);
        nbt.putInt("backpackUpgrades.feedingUpgradeSettings.filterSlotCount", data.backpackUpgrades.feedingUpgradeSettings.filterSlotCount);
        nbt.putInt("backpackUpgrades.feedingUpgradeSettings.tickRate", data.backpackUpgrades.feedingUpgradeSettings.tickRate);
        //Void
        nbt.putBoolean("backpackUpgrades.voidUpgradeSettings.enableUpgrade", data.backpackUpgrades.voidUpgradeSettings.enableUpgrade);
        nbt.putInt("backpackUpgrades.voidUpgradeSettings.filterSlotCount", data.backpackUpgrades.voidUpgradeSettings.filterSlotCount);

        //Backpack Settings

        //Leather
        nbt.putInt("backpackSettings.leather.inventorySlotCount", data.backpackSettings.leather.inventorySlotCount);
        nbt.putInt("backpackSettings.leather.upgradeSlotCount", data.backpackSettings.leather.upgradeSlotCount);
        nbt.putInt("backpackSettings.leather.toolSlotCount", data.backpackSettings.leather.toolSlotCount);
        nbt.putLong("backpackSettings.leather.tankCapacityPerRow", data.backpackSettings.leather.tankCapacityPerRow);
        //Iron
        nbt.putInt("backpackSettings.iron.inventorySlotCount", data.backpackSettings.iron.inventorySlotCount);
        nbt.putInt("backpackSettings.iron.upgradeSlotCount", data.backpackSettings.iron.upgradeSlotCount);
        nbt.putInt("backpackSettings.iron.toolSlotCount", data.backpackSettings.iron.toolSlotCount);
        nbt.putLong("backpackSettings.iron.tankCapacityPerRow", data.backpackSettings.iron.tankCapacityPerRow);
        //Gold
        nbt.putInt("backpackSettings.gold.inventorySlotCount", data.backpackSettings.gold.inventorySlotCount);
        nbt.putInt("backpackSettings.gold.upgradeSlotCount", data.backpackSettings.gold.upgradeSlotCount);
        nbt.putInt("backpackSettings.gold.toolSlotCount", data.backpackSettings.gold.toolSlotCount);
        nbt.putLong("backpackSettings.gold.tankCapacityPerRow", data.backpackSettings.gold.tankCapacityPerRow);
        //Diamond
        nbt.putInt("backpackSettings.diamond.inventorySlotCount", data.backpackSettings.diamond.inventorySlotCount);
        nbt.putInt("backpackSettings.diamond.upgradeSlotCount", data.backpackSettings.diamond.upgradeSlotCount);
        nbt.putInt("backpackSettings.diamond.toolSlotCount", data.backpackSettings.diamond.toolSlotCount);
        nbt.putLong("backpackSettings.diamond.tankCapacityPerRow", data.backpackSettings.diamond.tankCapacityPerRow);
        //Netherite
        nbt.putInt("backpackSettings.netherite.inventorySlotCount", data.backpackSettings.netherite.inventorySlotCount);
        nbt.putInt("backpackSettings.netherite.upgradeSlotCount", data.backpackSettings.netherite.upgradeSlotCount);
        nbt.putInt("backpackSettings.netherite.toolSlotCount", data.backpackSettings.netherite.toolSlotCount);
        nbt.putLong("backpackSettings.netherite.tankCapacityPerRow", data.backpackSettings.netherite.tankCapacityPerRow);

        nbt.putBoolean("backpackSettings.rightClickEquip", data.backpackSettings.rightClickEquip);
        nbt.putBoolean("backpackSettings.rightClickUnequip", data.backpackSettings.rightClickUnequip);
        nbt.putBoolean("backpackSettings.allowOnlyEquippedBackpack", data.backpackSettings.allowOnlyEquippedBackpack);
        nbt.putBoolean("backpackSettings.allowOpeningFromSlot", data.backpackSettings.allowOpeningFromSlot);
        nbt.putBoolean("backpackSettings.invulnerableBackpack", data.backpackSettings.invulnerableBackpack);
        nbt.putString("backpackSettings.toolSlotsAcceptableItems", String.join(",", data.backpackSettings.toolSlotsAcceptableItems));
        nbt.putString("backpackSettings.blacklistedItems", String.join(",", data.backpackSettings.blacklistedItems));
        nbt.putBoolean("backpackSettings.toolSlotsAcceptEverything", data.backpackSettings.toolSlotsAcceptEverything);
        nbt.putBoolean("backpackSettings.allowShulkerBoxes", data.backpackSettings.allowShulkerBoxes); //#TODO disable backpacks
        nbt.putBoolean("backpackSettings.voidProtection", data.backpackSettings.voidProtection);
        nbt.putBoolean("backpackSettings.backpackDeathPlace", data.backpackSettings.backpackDeathPlace);
        nbt.putBoolean("backpackSettings.backpackForceDeathPlace", data.backpackSettings.backpackForceDeathPlace);
        nbt.putBoolean("backpackSettings.quickSleepingBag", data.backpackSettings.quickSleepingBag);
        nbt.putBoolean("backpackSettings.enableSleepingBagSpawnPoint", data.backpackSettings.enableSleepingBagSpawnPoint);
        nbt.putBoolean("backpackSettings.backSlotIntegration", data.backpackSettings.backSlotIntegration);

        //World
        nbt.putBoolean("world.enableLoot", data.world.enableLoot);
        nbt.putFloat("world.chance", data.world.chance);
        nbt.putBoolean("world.spawnEntitiesWithBackpack", data.world.spawnEntitiesWithBackpack);
        nbt.putString("world.possibleOverworldEntityTypes", String.join(",", data.world.possibleOverworldEntityTypes));
        nbt.putString("world.possibleNetherEntityTypes", String.join(",", data.world.possibleNetherEntityTypes));
        nbt.putString("world.overworldBackpacks", String.join(",", data.world.overworldBackpacks));
        nbt.putString("world.netherBackpacks", String.join(",", data.world.netherBackpacks));
        nbt.putBoolean("world.enableVillagerTrade", data.world.enableVillagerTrade);

        //Backpack Abilities
        nbt.putBoolean("backpackAbilities.enableBackpackAbilities", data.backpackAbilities.enableBackpackAbilities);
        nbt.putBoolean("backpackAbilities.forceAbilityEnabled", data.backpackAbilities.forceAbilityEnabled);
        nbt.putString("backpackAbilities.allowedAbilities", String.join(",", data.backpackAbilities.allowedAbilities));
        nbt.putString("backpackAbilities.backpackEffects", String.join(",", data.backpackAbilities.backpackEffects));
        nbt.putString("backpackAbilities.cooldowns", String.join(",", data.backpackAbilities.cooldowns));

        //Slowness Debuff
        nbt.putBoolean("slownessDebuff.tooManyBackpacksSlowness", data.slownessDebuff.tooManyBackpacksSlowness);
        nbt.putInt("slownessDebuff.maxNumberOfBackpacks", data.slownessDebuff.maxNumberOfBackpacks);
        nbt.putInt("slownessDebuff.slownessPerExcessedBackpack", data.slownessDebuff.slownessPerExcessedBackpack);

        return nbt;
    }

    public static TravelersBackpackConfigData readFromNbt(CompoundTag nbt) {
        TravelersBackpackConfigData client = getConfig();
        TravelersBackpackConfigData data = new TravelersBackpackConfigData();

        //Client

        //Overlay
        data.client.overlay.enableOverlay = client.client.overlay.enableOverlay;
        data.client.overlay.offsetX = client.client.overlay.offsetX;
        data.client.overlay.offsetY = client.client.overlay.offsetY;

        data.client.showBackpackIconInInventory = client.client.showBackpackIconInInventory;
        data.client.sendBackpackCoordinatesMessage = client.client.sendBackpackCoordinatesMessage;
        data.client.enableToolCycling = client.client.enableToolCycling;
        data.client.disableScrollWheel = client.client.disableScrollWheel;
        data.client.obtainTips = client.client.obtainTips;
        data.client.renderTools = client.client.renderTools;
        data.client.showSupporterBadge = client.client.showSupporterBadge;

        if(nbt == null) {
            return data;
        }

        data.backpackUpgrades.enableTanksUpgrade = nbt.getBoolean("backpackUpgrades.enableTanksUpgrade");
        data.backpackUpgrades.enableCraftingUpgrade = nbt.getBoolean("backpackUpgrades.enableCraftingUpgrade");
        data.backpackUpgrades.enableJukeboxUpgrade = nbt.getBoolean("backpackUpgrades.enableJukeboxUpgrade");

        data.backpackUpgrades.pickupUpgradeSettings.enableUpgrade = nbt.getBoolean("backpackUpgrades.pickupUpgradeSettings.enableUpgrade");
        data.backpackUpgrades.pickupUpgradeSettings.filterSlotCount = nbt.getInt("backpackUpgrades.pickupUpgradeSettings.filterSlotCount");

        data.backpackUpgrades.magnetUpgradeSettings.enableUpgrade = nbt.getBoolean("backpackUpgrades.magnetUpgradeSettings.enableUpgrade");
        data.backpackUpgrades.magnetUpgradeSettings.filterSlotCount = nbt.getInt("backpackUpgrades.magnetUpgradeSettings.filterSlotCount");
        data.backpackUpgrades.magnetUpgradeSettings.pullRange = nbt.getInt("backpackUpgrades.magnetUpgradeSettings.pullRange");
        data.backpackUpgrades.magnetUpgradeSettings.tickRate = nbt.getInt("backpackUpgrades.magnetUpgradeSettings.tickRate");

        data.backpackUpgrades.feedingUpgradeSettings.enableUpgrade = nbt.getBoolean("backpackUpgrades.feedingUpgradeSettings.enableUpgrade");
        data.backpackUpgrades.feedingUpgradeSettings.filterSlotCount = nbt.getInt("backpackUpgrades.feedingUpgradeSettings.filterSlotCount");
        data.backpackUpgrades.feedingUpgradeSettings.tickRate = nbt.getInt("backpackUpgrades.feedingUpgradeSettings.tickRate");

        data.backpackUpgrades.voidUpgradeSettings.enableUpgrade = nbt.getBoolean("backpackUpgrades.voidUpgradeSettings.enableUpgrade");
        data.backpackUpgrades.voidUpgradeSettings.filterSlotCount = nbt.getInt("backpackUpgrades.voidUpgradeSettings.filterSlotCount");

        //Leather
        data.backpackSettings.leather.inventorySlotCount = nbt.getInt("backpackSettings.leather.inventorySlotCount");
        data.backpackSettings.leather.upgradeSlotCount = nbt.getInt("backpackSettings.leather.upgradeSlotCount");
        data.backpackSettings.leather.toolSlotCount = nbt.getInt("backpackSettings.leather.toolSlotCount");
        data.backpackSettings.leather.tankCapacityPerRow = nbt.getLong("backpackSettings.leather.tankCapacityPerRow");
        //Iron
        data.backpackSettings.iron.inventorySlotCount = nbt.getInt("backpackSettings.iron.inventorySlotCount");
        data.backpackSettings.iron.upgradeSlotCount = nbt.getInt("backpackSettings.iron.upgradeSlotCount");
        data.backpackSettings.iron.toolSlotCount = nbt.getInt("backpackSettings.iron.toolSlotCount");
        data.backpackSettings.iron.tankCapacityPerRow = nbt.getLong("backpackSettings.iron.tankCapacityPerRow");
        //Gold
        data.backpackSettings.gold.inventorySlotCount = nbt.getInt("backpackSettings.gold.inventorySlotCount");
        data.backpackSettings.gold.upgradeSlotCount = nbt.getInt("backpackSettings.gold.upgradeSlotCount");
        data.backpackSettings.gold.toolSlotCount = nbt.getInt("backpackSettings.gold.toolSlotCount");
        data.backpackSettings.gold.tankCapacityPerRow = nbt.getLong("backpackSettings.gold.tankCapacityPerRow");
        //Diamond
        data.backpackSettings.diamond.inventorySlotCount = nbt.getInt("backpackSettings.diamond.inventorySlotCount");
        data.backpackSettings.diamond.upgradeSlotCount = nbt.getInt("backpackSettings.diamond.upgradeSlotCount");
        data.backpackSettings.diamond.toolSlotCount = nbt.getInt("backpackSettings.diamond.toolSlotCount");
        data.backpackSettings.diamond.tankCapacityPerRow = nbt.getLong("backpackSettings.diamond.tankCapacityPerRow");
        //Netherite
        data.backpackSettings.netherite.inventorySlotCount = nbt.getInt("backpackSettings.netherite.inventorySlotCount");
        data.backpackSettings.netherite.upgradeSlotCount = nbt.getInt("backpackSettings.netherite.upgradeSlotCount");
        data.backpackSettings.netherite.toolSlotCount = nbt.getInt("backpackSettings.netherite.toolSlotCount");
        data.backpackSettings.netherite.tankCapacityPerRow = nbt.getLong("backpackSettings.netherite.tankCapacityPerRow");

        data.backpackSettings.rightClickEquip = nbt.getBoolean("backpackSettings.rightClickEquip");
        data.backpackSettings.rightClickUnequip = nbt.getBoolean("backpackSettings.rightClickUnequip");
        data.backpackSettings.allowOnlyEquippedBackpack = nbt.getBoolean("backpackSettings.allowOnlyEquippedBackpack");
        data.backpackSettings.allowOpeningFromSlot = nbt.getBoolean("backpackSettings.allowOpeningFromSlot");
        data.backpackSettings.invulnerableBackpack = nbt.getBoolean("backpackSettings.invulnerableBackpack");
        data.backpackSettings.toolSlotsAcceptableItems = nbt.getString("backpackSettings.toolSlotsAcceptableItems").split(",");
        data.backpackSettings.blacklistedItems = nbt.getString("backpackSettings.blacklistedItems").split(",");
        data.backpackSettings.toolSlotsAcceptEverything = nbt.getBoolean("backpackSettings.toolSlotsAcceptEverything");
        data.backpackSettings.allowShulkerBoxes = nbt.getBoolean("backpackSettings.allowShulkerBoxes");
        data.backpackSettings.voidProtection = nbt.getBoolean("backpackSettings.voidProtection");
        data.backpackSettings.backpackDeathPlace = nbt.getBoolean("backpackSettings.backpackDeathPlace");
        data.backpackSettings.backpackForceDeathPlace = nbt.getBoolean("backpackSettings.backpackForceDeathPlace");
        data.backpackSettings.quickSleepingBag = nbt.getBoolean("backpackSettings.quickSleepingBag");
        data.backpackSettings.enableSleepingBagSpawnPoint = nbt.getBoolean("backpackSettings.enableSleepingBagSpawnPoint");
        data.backpackSettings.backSlotIntegration = nbt.getBoolean("backpackSettings.backSlotIntegration");

        //World
        data.world.enableLoot = nbt.getBoolean("world.enableLoot");
        data.world.chance = nbt.getFloat("world.chance");
        data.world.spawnEntitiesWithBackpack = nbt.getBoolean("world.spawnEntitiesWithBackpack");
        data.world.possibleOverworldEntityTypes = nbt.getString("world.possibleOverworldEntityTypes").split(",");
        data.world.possibleNetherEntityTypes = nbt.getString("world.possibleNetherEntityTypes").split(",");
        data.world.overworldBackpacks = nbt.getString("world.overworldBackpacks").split(",");
        data.world.netherBackpacks = nbt.getString("world.netherBackpacks").split(",");
        data.world.enableVillagerTrade = nbt.getBoolean("world.enableVillagerTrade");

        //Backpack Abilities
        data.backpackAbilities.enableBackpackAbilities = nbt.getBoolean("backpackAbilities.enableBackpackAbilities");
        data.backpackAbilities.forceAbilityEnabled = nbt.getBoolean("backpackAbilities.forceAbilityEnabled");
        data.backpackAbilities.allowedAbilities = nbt.getString("backpackAbilities.allowedAbilities").split(",");
        data.backpackAbilities.backpackEffects = nbt.getString("backpackAbilities.backpackEffects").split(",");
        data.backpackAbilities.cooldowns = nbt.getString("backpackAbilities.cooldowns").split(",");

        //Slowness Debuff
        data.slownessDebuff.tooManyBackpacksSlowness = nbt.getBoolean("slownessDebuff.tooManyBackpacksSlowness");
        data.slownessDebuff.maxNumberOfBackpacks = nbt.getInt("slownessDebuff.maxNumberOfBackpacks");
        data.slownessDebuff.slownessPerExcessedBackpack = nbt.getInt("slownessDebuff.slownessPerExcessedBackpack");
        return data;
    }

    public static void loadItemsFromConfig(String[] configList, List<Item> targetList) {
        for(String registryName : configList) {
            ResourceLocation res = ResourceLocation.tryParse(registryName);

            if(BuiltInRegistries.ITEM.get(res).isPresent()) {
                targetList.add(BuiltInRegistries.ITEM.getValue(res));
            }
        }
    }

    public static void loadBackpackEffectsFromConfig(String[] configList, Multimap<Item, BackpackEffect> backpackEffects) {
        try {
            for(String entry : configList) {
                String[] parts = entry.replace(" ", "").split(";");
                if(parts.length == 5) {
                    ResourceLocation backpackRes = ResourceLocation.tryParse(parts[0]);
                    ResourceLocation effectRes = ResourceLocation.tryParse(parts[1]);

                    if(BuiltInRegistries.ITEM.containsKey(backpackRes) && BuiltInRegistries.MOB_EFFECT.get(effectRes).isPresent() && BuiltInRegistries.ITEM.get(backpackRes).isPresent()) {
                        Item backpack = BuiltInRegistries.ITEM.getValue(backpackRes);
                        int minDuration = Integer.parseInt(parts[2]);
                        int maxDuration = Integer.parseInt(parts[3]);
                        int amplifier = Integer.parseInt(parts[4]);

                        if(minDuration < 0 || maxDuration < 0 || amplifier < 0) {
                            TravelersBackpack.LOGGER.error("Backpack Effects: duration and amplifier must be positive integers!");
                        }

                        if(minDuration > maxDuration) {
                            TravelersBackpack.LOGGER.error("Backpack Effects: minDuration must be less than or equal to maxDuration!");
                        }

                        backpackEffects.put(backpack, new BackpackEffect(BuiltInRegistries.MOB_EFFECT.get(effectRes).get(), minDuration, maxDuration, amplifier));
                    }
                }
            }
        } catch(Exception e) {
            TravelersBackpack.LOGGER.error("Could not load Backpack Effect from Config! Check your config if entries are correct!");
        }
    }

    public static void loadCooldownsFromConfig(String[] config, Map<Item, Cooldown> cooldownConfigs) {
        try {
            for(String entry : config) {
                String[] parts = entry.replace(" ", "").split(";");
                if(parts.length == 3) {
                    ResourceLocation backpackRes = ResourceLocation.tryParse(parts[0]);
                    if(BuiltInRegistries.ITEM.get(backpackRes).isEmpty()) {
                        continue;
                    }
                    Item backpack = BuiltInRegistries.ITEM.getValue(backpackRes);
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
}