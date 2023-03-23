package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.init.ModNetwork;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public class TravelersBackpackConfig
{
    //Backpack Settings
    public static boolean disableCrafting;
    public static boolean enableBackpackBlockQuickEquip;
    public static boolean invulnerableBackpack;
    public static long tanksCapacity;
    public static boolean voidProtection;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableSleepingBagSpawnPoint;
    public static boolean trinketsIntegration;

    //World
    public static boolean enableLoot;
    public static boolean spawnEntitiesWithBackpack;
    public static int spawnChance;

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
        disableCrafting = data.backpackSettings.disableCrafting;
        enableBackpackBlockQuickEquip = data.backpackSettings.enableBackpackBlockQuickEquip;
        invulnerableBackpack = data.backpackSettings.invulnerableBackpack;
        tanksCapacity = data.backpackSettings.tanksCapacity;
        voidProtection = data.backpackSettings.voidProtection;
        backpackDeathPlace = data.backpackSettings.backpackDeathPlace;
        backpackForceDeathPlace = data.backpackSettings.backpackForceDeathPlace;
        enableSleepingBagSpawnPoint = data.backpackSettings.enableSleepingBagSpawnPoint;
        trinketsIntegration = data.backpackSettings.trinketsIntegration;

        //World
        enableLoot = data.world.enableLoot;
        spawnEntitiesWithBackpack = data.world.spawnEntitiesWithBackpack;
        spawnChance = data.world.spawnChance;

        //Abilities
        enableBackpackAbilities = data.abilities.enableBackpackAbilities;
        forceAbilityEnabled = data.abilities.forceAbilityEnabled;

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
        compound.putBoolean("disableCrafting", disableCrafting);
        compound.putBoolean("enableBackpackBlockQuickEquip", enableBackpackBlockQuickEquip);
        compound.putBoolean("invulnerableBackpack", invulnerableBackpack);
        compound.putLong("tanksCapacity", tanksCapacity);
        compound.putBoolean("voidProtection", voidProtection);
        compound.putBoolean("backpackDeathPlace", backpackDeathPlace);
        compound.putBoolean("backpackForceDeathPlace", backpackForceDeathPlace);
        compound.putBoolean("enableSleepingBagSpawnPoint", enableSleepingBagSpawnPoint);
        compound.putBoolean("trinketsIntegration", trinketsIntegration);

        //World
        compound.putBoolean("enableLoot", enableLoot);
        compound.putBoolean("spawnEntitiesWithBackpack", spawnEntitiesWithBackpack);
        compound.putInt("spawnChance", spawnChance);

        //Abilities
        compound.putBoolean("enableBackpackAbilities",enableBackpackAbilities);
        compound.putBoolean("forceAbilityEnabled", forceAbilityEnabled);

        //Slowness Debuff
        compound.putBoolean("tooManyBackpacksSlowness", tooManyBackpacksSlowness);
        compound.putInt("maxNumberOfBackpacks", maxNumberOfBackpacks);
        compound.putInt("slownessPerExcessedBackpack", slownessPerExcessedBackpack);

        return compound;
    }

    public static void fromNbt(NbtCompound compound)
    {
        //Backpack Settings
        disableCrafting = compound.getBoolean("disableCrafting");
        enableBackpackBlockQuickEquip = compound.getBoolean("enableBackpackBlockQuickEquip");
        invulnerableBackpack = compound.getBoolean("invulnerableBackpack");
        tanksCapacity = compound.getLong("tanksCapacity");
        voidProtection = compound.getBoolean("voidProtection");
        backpackDeathPlace = compound.getBoolean("backpackDeathPlace");
        backpackForceDeathPlace = compound.getBoolean("backpackForceDeathPlace");
        enableSleepingBagSpawnPoint = compound.getBoolean("enableSleepingBagSpawnPoint");
        trinketsIntegration = compound.getBoolean("trinketsIntegration");

        //World
        enableLoot = compound.getBoolean("enableLoot");
        spawnEntitiesWithBackpack = compound.getBoolean("spawnEntitiesWithBackpack");
        spawnChance = compound.getInt("spawnChance");

        //Abilities
        enableBackpackAbilities = compound.getBoolean("enableBackpackAbilities");
        forceAbilityEnabled = compound.getBoolean("forceAbilityEnabled");

        //Slowness Debuff
        tooManyBackpacksSlowness = compound.getBoolean("tooManyBackpacksSlowness");
        maxNumberOfBackpacks = compound.getInt("maxNumberOfBackpacks");
        slownessPerExcessedBackpack = compound.getInt("slownessPerExcessedBackpack");
    }
}