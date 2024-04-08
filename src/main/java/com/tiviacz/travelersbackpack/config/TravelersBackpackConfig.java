package com.tiviacz.travelersbackpack.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class TravelersBackpackConfig
{
    public static TravelersBackpackConfigData getConfig()
    {
        return AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).getConfig();
    }

    public static void register()
    {
        AutoConfig.register(TravelersBackpackConfigData.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) -> AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load());
    }

    public static void loadItemsFromConfig(String[] configArray, List<Item> targetList)
    {
        for(String registryName : configArray)
        {
            Identifier id = Identifier.tryParse(registryName);

            if(Registries.ITEM.getOrEmpty(id).isPresent())
            {
                targetList.add(Registries.ITEM.get(id));
            }
        }
    }

    public static void loadEntityTypesFromConfig(String[] configArray, List<EntityType> targetList)
    {
        for(String registryName : configArray)
        {
            Identifier id = Identifier.tryParse(registryName);

            if(Registries.ENTITY_TYPE.getOrEmpty(id).isPresent())
            {
                targetList.add(Registries.ENTITY_TYPE.get(id));
            }
        }
    }

    public static NbtCompound writeToNbt()
    {
        TravelersBackpackConfigData data = getConfig();
        NbtCompound nbt = new NbtCompound();

        //Backpack Settings

        //Leather
        nbt.putInt("backpackSettings.leather.inventorySlotCount", data.backpackSettings.leather.inventorySlotCount);
        nbt.putInt("backpackSettings.leather.toolSlotCount", data.backpackSettings.leather.toolSlotCount);
        nbt.putLong("backpackSettings.leather.tankCapacity", data.backpackSettings.leather.tankCapacity);
        //Iron
        nbt.putInt("backpackSettings.iron.inventorySlotCount", data.backpackSettings.iron.inventorySlotCount);
        nbt.putInt("backpackSettings.iron.toolSlotCount", data.backpackSettings.iron.toolSlotCount);
        nbt.putLong("backpackSettings.iron.tankCapacity", data.backpackSettings.iron.tankCapacity);
        //Gold
        nbt.putInt("backpackSettings.gold.inventorySlotCount", data.backpackSettings.gold.inventorySlotCount);
        nbt.putInt("backpackSettings.gold.toolSlotCount", data.backpackSettings.gold.toolSlotCount);
        nbt.putLong("backpackSettings.gold.tankCapacity", data.backpackSettings.gold.tankCapacity);
        //Diamond
        nbt.putInt("backpackSettings.diamond.inventorySlotCount", data.backpackSettings.diamond.inventorySlotCount);
        nbt.putInt("backpackSettings.diamond.toolSlotCount", data.backpackSettings.diamond.toolSlotCount);
        nbt.putLong("backpackSettings.diamond.tankCapacity", data.backpackSettings.diamond.tankCapacity);
        //Netherite
        nbt.putInt("backpackSettings.netherite.inventorySlotCount", data.backpackSettings.netherite.inventorySlotCount);
        nbt.putInt("backpackSettings.netherite.toolSlotCount", data.backpackSettings.netherite.toolSlotCount);
        nbt.putLong("backpackSettings.netherite.tankCapacity", data.backpackSettings.netherite.tankCapacity);

        nbt.putBoolean("backpackSettings.enableTierUpgrades", data.backpackSettings.enableTierUpgrades);
        nbt.putBoolean("backpackSettings.enableCraftingUpgrade", data.backpackSettings.enableCraftingUpgrade);
        nbt.putBoolean("backpackSettings.craftingUpgradeByDefault", data.backpackSettings.craftingUpgradeByDefault);
        nbt.putBoolean("backpackSettings.craftingSavesItems", data.backpackSettings.craftingSavesItems);
        nbt.putBoolean("backpackSettings.enableBackpackBlockQuickEquip", data.backpackSettings.enableBackpackBlockQuickEquip);
        nbt.putBoolean("backpackSettings.enableBackpackRightClickUnequip", data.backpackSettings.enableBackpackRightClickUnequip);
        nbt.putBoolean("backpackSettings.invulnerableBackpack", data.backpackSettings.invulnerableBackpack);
        nbt.putString("backpackSettings.toolSlotsAcceptableItems", String.join(",", data.backpackSettings.toolSlotsAcceptableItems));
        nbt.putString("backpackSettings.blacklistedItems", String.join(",", data.backpackSettings.blacklistedItems));
        nbt.putBoolean("backpackSettings.toolSlotsAcceptEverything", data.backpackSettings.toolSlotsAcceptEverything);
        nbt.putBoolean("backpackSettings.allowShulkerBoxes", data.backpackSettings.allowShulkerBoxes);
        nbt.putBoolean("backpackSettings.voidProtection", data.backpackSettings.voidProtection);
        nbt.putBoolean("backpackSettings.backpackDeathPlace", data.backpackSettings.backpackDeathPlace);
        nbt.putBoolean("backpackSettings.backpackForceDeathPlace", data.backpackSettings.backpackForceDeathPlace);
        nbt.putBoolean("backpackSettings.enableSleepingBagSpawnPoint", data.backpackSettings.enableSleepingBagSpawnPoint);
        nbt.putBoolean("backpackSettings.trinketsIntegration", data.backpackSettings.trinketsIntegration);

        //World
        nbt.putBoolean("world.enableLoot", data.world.enableLoot);
        nbt.putBoolean("world.spawnEntitiesWithBackpack", data.world.spawnEntitiesWithBackpack);
        nbt.putString("world.possibleOverworldEntityTypes", String.join(",", data.world.possibleOverworldEntityTypes));
        nbt.putString("world.possibleNetherEntityTypes", String.join(",", data.world.possibleNetherEntityTypes));
        nbt.putInt("world.spawnChance", data.world.spawnChance);
        nbt.putString("world.overworldBackpacks", String.join(",", data.world.overworldBackpacks));
        nbt.putString("world.netherBackpacks", String.join(",", data.world.netherBackpacks));
        nbt.putBoolean("world.enableVillagerTrade", data.world.enableVillagerTrade);

        //Backpack Abilities
        nbt.putBoolean("backpackAbilities.enableBackpackAbilities", data.backpackAbilities.enableBackpackAbilities);
        nbt.putBoolean("backpackAbilities.forceAbilityEnabled", data.backpackAbilities.forceAbilityEnabled);
        nbt.putString("backpackAbilities.allowedAbilities", String.join(",", data.backpackAbilities.allowedAbilities));

        //Slowness Debuff
        nbt.putBoolean("slownessDebuff.tooManyBackpacksSlowness", data.slownessDebuff.tooManyBackpacksSlowness);
        nbt.putInt("slownessDebuff.maxNumberOfBackpacks", data.slownessDebuff.maxNumberOfBackpacks);
        nbt.putInt("slownessDebuff.slownessPerExcessedBackpack", data.slownessDebuff.slownessPerExcessedBackpack);

        return nbt;
    }

    public static TravelersBackpackConfigData readFromNbt(NbtCompound nbt)
    {
        TravelersBackpackConfigData client = getConfig();
        TravelersBackpackConfigData data = new TravelersBackpackConfigData();

        //Client

        //Overlay
        data.client.overlay.enableOverlay = client.client.overlay.enableOverlay;
        data.client.overlay.offsetX = client.client.overlay.offsetX;
        data.client.overlay.offsetY = client.client.overlay.offsetY;

        data.client.enableLegacyGui = client.client.enableLegacyGui;
        data.client.enableToolCycling = client.client.enableToolCycling;
        data.client.disableScrollWheel = client.client.disableScrollWheel;
        data.client.obtainTips = client.client.obtainTips;
        data.client.renderTools = client.client.renderTools;
        data.client.renderBackpackWithElytra = client.client.renderBackpackWithElytra;
        data.client.disableBackpackRender = client.client.disableBackpackRender;

        if(nbt == null)
        {
            return data;
        }

        //Leather
        data.backpackSettings.leather.inventorySlotCount = nbt.getInt("backpackSettings.leather.inventorySlotCount");
        data.backpackSettings.leather.toolSlotCount = nbt.getInt("backpackSettings.leather.toolSlotCount");
        data.backpackSettings.leather.tankCapacity = nbt.getLong("backpackSettings.leather.tankCapacity");
        //Iron
        data.backpackSettings.iron.inventorySlotCount = nbt.getInt("backpackSettings.iron.inventorySlotCount");
        data.backpackSettings.iron.toolSlotCount = nbt.getInt("backpackSettings.iron.toolSlotCount");
        data.backpackSettings.iron.tankCapacity = nbt.getLong("backpackSettings.iron.tankCapacity");
        //Gold
        data.backpackSettings.gold.inventorySlotCount = nbt.getInt("backpackSettings.gold.inventorySlotCount");
        data.backpackSettings.gold.toolSlotCount = nbt.getInt("backpackSettings.gold.toolSlotCount");
        data.backpackSettings.gold.tankCapacity = nbt.getLong("backpackSettings.gold.tankCapacity");
        //Diamond
        data.backpackSettings.diamond.inventorySlotCount = nbt.getInt("backpackSettings.diamond.inventorySlotCount");
        data.backpackSettings.diamond.toolSlotCount = nbt.getInt("backpackSettings.diamond.toolSlotCount");
        data.backpackSettings.diamond.tankCapacity = nbt.getLong("backpackSettings.diamond.tankCapacity");
        //Netherite
        data.backpackSettings.netherite.inventorySlotCount = nbt.getInt("backpackSettings.netherite.inventorySlotCount");
        data.backpackSettings.netherite.toolSlotCount = nbt.getInt("backpackSettings.netherite.toolSlotCount");
        data.backpackSettings.netherite.tankCapacity = nbt.getLong("backpackSettings.netherite.tankCapacity");

        data.backpackSettings.enableTierUpgrades = nbt.getBoolean("backpackSettings.enableTierUpgrades");
        data.backpackSettings.enableCraftingUpgrade = nbt.getBoolean("backpackSettings.enableCraftingUpgrade");
        data.backpackSettings.craftingUpgradeByDefault = nbt.getBoolean("backpackSettings.craftingUpgradeByDefault");
        data.backpackSettings.craftingSavesItems = nbt.getBoolean("backpackSettings.craftingSavesItems");
        data.backpackSettings.enableBackpackBlockQuickEquip = nbt.getBoolean("backpackSettings.enableBackpackBlockQuickEquip");
        data.backpackSettings.enableBackpackRightClickUnequip = nbt.getBoolean("backpackSettings.enableBackpackRightClickUnequip");
        data.backpackSettings.invulnerableBackpack = nbt.getBoolean("backpackSettings.invulnerableBackpack");
        data.backpackSettings.toolSlotsAcceptableItems = nbt.getString("backpackSettings.toolSlotsAcceptableItems").split(",");
        data.backpackSettings.blacklistedItems = nbt.getString("backpackSettings.blacklistedItems").split(",");
        data.backpackSettings.toolSlotsAcceptEverything = nbt.getBoolean("backpackSettings.toolSlotsAcceptEverything");
        data.backpackSettings.allowShulkerBoxes = nbt.getBoolean("backpackSettings.allowShulkerBoxes");
        data.backpackSettings.voidProtection = nbt.getBoolean("backpackSettings.voidProtection");
        data.backpackSettings.backpackDeathPlace = nbt.getBoolean("backpackSettings.backpackDeathPlace");
        data.backpackSettings.backpackForceDeathPlace = nbt.getBoolean("backpackSettings.backpackForceDeathPlace");
        data.backpackSettings.enableSleepingBagSpawnPoint = nbt.getBoolean("backpackSettings.enableSleepingBagSpawnPoint");
        data.backpackSettings.trinketsIntegration = nbt.getBoolean("backpackSettings.trinketsIntegration");

        //World
        data.world.enableLoot = nbt.getBoolean("world.enableLoot");
        data.world.spawnEntitiesWithBackpack = nbt.getBoolean("world.spawnEntitiesWithBackpack");
        data.world.possibleOverworldEntityTypes = nbt.getString("world.possibleOverworldEntityTypes").split(",");
        data.world.possibleNetherEntityTypes = nbt.getString("world.possibleNetherEntityTypes").split(",");
        data.world.spawnChance = nbt.getInt("world.spawnChance");
        data.world.overworldBackpacks = nbt.getString("world.overworldBackpacks").split(",");
        data.world.netherBackpacks = nbt.getString("world.netherBackpacks").split(",");
        data.world.enableVillagerTrade = nbt.getBoolean("world.enableVillagerTrade");

        //Backpack Abilities
        data.backpackAbilities.enableBackpackAbilities = nbt.getBoolean("backpackAbilities.enableBackpackAbilities");
        data.backpackAbilities.forceAbilityEnabled = nbt.getBoolean("backpackAbilities.forceAbilityEnabled");
        data.backpackAbilities.allowedAbilities = nbt.getString("backpackAbilities.allowedAbilities").split(",");

        //Slowness Debuff
        data.slownessDebuff.tooManyBackpacksSlowness = nbt.getBoolean("slownessDebuff.tooManyBackpacksSlowness");
        data.slownessDebuff.maxNumberOfBackpacks = nbt.getInt("slownessDebuff.maxNumberOfBackpacks");
        data.slownessDebuff.slownessPerExcessedBackpack = nbt.getInt("slownessDebuff.slownessPerExcessedBackpack");
        return data;
    }
}