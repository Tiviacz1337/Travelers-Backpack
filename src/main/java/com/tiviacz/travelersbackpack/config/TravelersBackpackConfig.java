package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.init.ModNetwork;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackConfig
{
    //Backpack Settings
    public static boolean enableTierUpgrades;
    public static boolean disableCrafting;
    public static boolean enableBackpackBlockQuickEquip;
    public static boolean invulnerableBackpack;
    public static String[] toolSlotsAcceptableItems;
    public static String[] blacklistedItems;
    public static boolean allowShulkerBoxes;
    public static long[] tanksCapacity;
    public static boolean voidProtection;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableSleepingBagSpawnPoint;
    public static boolean trinketsIntegration;

    //World
    public static boolean enableLoot;
    public static boolean spawnEntitiesWithBackpack;
    public static String[] possibleOverworldEntityTypes;
    public static String[] possibleNetherEntityTypes;
    public static int spawnChance;
    public static String[] overworldBackpacks;
    public static String[] netherBackpacks;
    public static boolean enableVillagerTrade;

    //Abilities
    public static boolean enableBackpackAbilities;
    public static boolean forceAbilityEnabled;

    //Slowness Debuff
    public static boolean tooManyBackpacksSlowness;
    public static int maxNumberOfBackpacks;
    public static int slownessPerExcessedBackpack;

    //Client Settings
    public static boolean enableToolCycling;
    public static boolean obtainTips;
    public static boolean renderTools;
    public static boolean renderBackpackWithElytra;
    public static boolean disableBackpackRender;

    //Overlay
    public static boolean enableOverlay;
    public static int offsetX;
    public static int offsetY;

    public static void setup() {
        TravelersBackpackConfigData data =
                AutoConfig.register(TravelersBackpackConfigData.class, JanksonConfigSerializer::new).getConfig();

        bake(null, data); // To load the initial config into the data.  Not sure why the config is not loaded until server start after this
        ServerLifecycleEvents.SERVER_STARTED.register(server -> bake(server, data));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, b) -> bake(server, data));
    }

    public static void bake(MinecraftServer server, TravelersBackpackConfigData data)
    {
        //Backpack Settings
        enableTierUpgrades = data.backpackSettings.enableTierUpgrades;
        disableCrafting = data.backpackSettings.disableCrafting;
        enableBackpackBlockQuickEquip = data.backpackSettings.enableBackpackBlockQuickEquip;
        invulnerableBackpack = data.backpackSettings.invulnerableBackpack;
        toolSlotsAcceptableItems = data.backpackSettings.toolSlotsAcceptableItems;
        blacklistedItems = data.backpackSettings.blacklistedItems;
        allowShulkerBoxes = data.backpackSettings.allowShulkerBoxes;
        tanksCapacity = data.backpackSettings.tanksCapacity;
        voidProtection = data.backpackSettings.voidProtection;
        backpackDeathPlace = data.backpackSettings.backpackDeathPlace;
        backpackForceDeathPlace = data.backpackSettings.backpackForceDeathPlace;
        enableSleepingBagSpawnPoint = data.backpackSettings.enableSleepingBagSpawnPoint;
        trinketsIntegration = data.backpackSettings.trinketsIntegration;

        //World
        enableLoot = data.world.enableLoot;
        spawnEntitiesWithBackpack = data.world.spawnEntitiesWithBackpack;
        possibleOverworldEntityTypes = data.world.possibleOverworldEntityTypes;
        possibleNetherEntityTypes = data.world.possibleNetherEntityTypes;
        spawnChance = data.world.spawnChance;
        overworldBackpacks = data.world.overworldBackpacks;
        netherBackpacks = data.world.netherBackpacks;
        enableVillagerTrade = data.world.enableVillagerTrade;

        //Abilities
        enableBackpackAbilities = data.backpackAbilities.enableBackpackAbilities;
        forceAbilityEnabled = data.backpackAbilities.forceAbilityEnabled;

        //Slowness Debuff
        tooManyBackpacksSlowness = data.slownessDebuff.tooManyBackpacksSlowness;
        maxNumberOfBackpacks = data.slownessDebuff.maxNumberOfBackpacks;
        slownessPerExcessedBackpack = data.slownessDebuff.slownessPerExcessedBackpack;

        if(server == null)
        {
            //Client
            enableToolCycling = data.client.enableToolCycling;
            obtainTips = data.client.obtainTips;
            renderTools = data.client.renderTools;
            renderBackpackWithElytra = data.client.renderBackpackWithElytra;
            disableBackpackRender = data.client.disableBackpackRender;

            enableOverlay = data.client.overlay.enableOverlay;
            offsetX = data.client.overlay.offsetX;
            offsetY = data.client.overlay.offsetY;
        }
        else
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeNbt(toNbt());
            server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, ModNetwork.UPDATE_CONFIG_ID, buf));
        }
    }

    public static NbtCompound toNbt()
    {
        NbtCompound compound = new NbtCompound();

        //Backpack Settings
        compound.putBoolean("enableTierUpgrades", enableTierUpgrades);
        compound.putBoolean("disableCrafting", disableCrafting);
        compound.putBoolean("enableBackpackBlockQuickEquip", enableBackpackBlockQuickEquip);
        compound.putBoolean("invulnerableBackpack", invulnerableBackpack);
        putStringArray(compound, toolSlotsAcceptableItems, "toolSlotsAcceptableItems");
        putStringArray(compound, blacklistedItems, "blacklistedItems");
        compound.putBoolean("allowShulkerBoxes", allowShulkerBoxes);
        compound.putLongArray("tanksCapacity", tanksCapacity);
        compound.putBoolean("voidProtection", voidProtection);
        compound.putBoolean("backpackDeathPlace", backpackDeathPlace);
        compound.putBoolean("backpackForceDeathPlace", backpackForceDeathPlace);
        compound.putBoolean("enableSleepingBagSpawnPoint", enableSleepingBagSpawnPoint);
        compound.putBoolean("trinketsIntegration", trinketsIntegration);

        //World
        compound.putBoolean("enableLoot", enableLoot);
        compound.putBoolean("spawnEntitiesWithBackpack", spawnEntitiesWithBackpack);
        putStringArray(compound, possibleOverworldEntityTypes, "possibleOverworldEntityTypes");
        putStringArray(compound, possibleNetherEntityTypes, "possibleNetherEntityTypes");
        compound.putInt("spawnChance", spawnChance);
        putStringArray(compound, overworldBackpacks, "overworldBackpacks");
        putStringArray(compound, netherBackpacks, "netherBackpacks");
        compound.putBoolean("enableVillagerTrade", enableVillagerTrade);

        //Abilities
        compound.putBoolean("enableBackpackAbilities",enableBackpackAbilities);
        compound.putBoolean("forceAbilityEnabled", forceAbilityEnabled);

        //Slowness Debuff
        compound.putBoolean("tooManyBackpacksSlowness", tooManyBackpacksSlowness);
        compound.putInt("maxNumberOfBackpacks", maxNumberOfBackpacks);
        compound.putInt("slownessPerExcessedBackpack", slownessPerExcessedBackpack);

        return compound;
    }

    public static void putStringArray(NbtCompound targetCompound, String[] array, String listName)
    {
        NbtList nbtList = new NbtList();

        for(String s : array)
        {
            nbtList.add(NbtString.of(s));
        }

        targetCompound.put(listName, nbtList);
    }

    public static String[] getStringArray(NbtCompound targetCompound, String listName)
    {
        List<String> stringList = new ArrayList<>();

        for(NbtElement nbt : (NbtList)targetCompound.get(listName))
        {
            NbtString nbtString = (NbtString)nbt;
            stringList.add(nbtString.toString());
        }
        return stringList.toArray(new String[0]);
    }

    public static void fromNbt(NbtCompound compound)
    {
        //Backpack Settings
        enableTierUpgrades = compound.getBoolean("enableTierUpgrades");
        disableCrafting = compound.getBoolean("disableCrafting");
        enableBackpackBlockQuickEquip = compound.getBoolean("enableBackpackBlockQuickEquip");
        invulnerableBackpack = compound.getBoolean("invulnerableBackpack");
        toolSlotsAcceptableItems = getStringArray(compound, "toolSlotsAcceptableItems");
        blacklistedItems = getStringArray(compound, "blacklistedItems");
        allowShulkerBoxes = compound.getBoolean("allowShulkerBoxes");
        tanksCapacity = compound.getLongArray("tanksCapacity");
        voidProtection = compound.getBoolean("voidProtection");
        backpackDeathPlace = compound.getBoolean("backpackDeathPlace");
        backpackForceDeathPlace = compound.getBoolean("backpackForceDeathPlace");
        enableSleepingBagSpawnPoint = compound.getBoolean("enableSleepingBagSpawnPoint");
        trinketsIntegration = compound.getBoolean("trinketsIntegration");

        //World
        enableLoot = compound.getBoolean("enableLoot");
        spawnEntitiesWithBackpack = compound.getBoolean("spawnEntitiesWithBackpack");
        possibleOverworldEntityTypes = getStringArray(compound, "possibleOverworldEntityTypes");
        possibleNetherEntityTypes = getStringArray(compound, "possibleNetherEntityTypes");
        spawnChance = compound.getInt("spawnChance");
        overworldBackpacks = getStringArray(compound, "overworldBackpacks");
        netherBackpacks = getStringArray(compound, "netherBackpacks");
        enableVillagerTrade = compound.getBoolean("enableVillagerTrade");

        //Abilities
        enableBackpackAbilities = compound.getBoolean("enableBackpackAbilities");
        forceAbilityEnabled = compound.getBoolean("forceAbilityEnabled");

        //Slowness Debuff
        tooManyBackpacksSlowness = compound.getBoolean("tooManyBackpacksSlowness");
        maxNumberOfBackpacks = compound.getInt("maxNumberOfBackpacks");
        slownessPerExcessedBackpack = compound.getInt("slownessPerExcessedBackpack");
    }

    public static void loadItemsFromConfig(String[] configArray, List<Item> targetList)
    {
        for(String registryName : configArray)
        {
            Identifier id = new Identifier(registryName);

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
            Identifier id = new Identifier(registryName);

            if(Registries.ENTITY_TYPE.getOrEmpty(id).isPresent())
            {
                targetList.add(Registries.ENTITY_TYPE.get(id));
            }
        }
    }
}