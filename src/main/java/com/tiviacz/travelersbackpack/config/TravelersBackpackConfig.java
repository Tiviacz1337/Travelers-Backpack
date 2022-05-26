package com.tiviacz.travelersbackpack.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TravelersBackpackConfig
{
    public static boolean disableCrafting;
    public static boolean enableBackpackBlockQuickEquip;
    public static boolean enableLoot;
    public static boolean invulnerableBackpack;
    public static long tanksCapacity;

    //Common
    public static boolean trinketsIntegration;
    public static boolean enableBackpackAbilities;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableEmptyTankButton;
    public static boolean enableSleepingBagSpawnPoint;

    //Client
    public static boolean enableBackpackCoordsMessage;
    public static boolean enableToolCycling;
    public static boolean disableScrollWheel;
    public static boolean obtainTips;
    public static boolean renderTools;
    public static boolean renderBackpackWithElytra;

    //Overlay
    public static boolean enableOverlay;
    public static int offsetX;
    public static int offsetY;

    public static void setup() {
        TravelersBackpackConfigData data =
                AutoConfig.register(TravelersBackpackConfigData.class, JanksonConfigSerializer::new).getConfig();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> bake(data));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, b) -> bake(data));
    }

    public static void bake(TravelersBackpackConfigData data)
    {
        disableCrafting = data.disableCrafting;
        enableBackpackBlockQuickEquip = data.enableBackpackBlockQuickEquip;
        enableLoot = data.enableLoot;
        invulnerableBackpack = data.invulnerableBackpack;
        tanksCapacity = data.tanksCapacity;

        trinketsIntegration = data.trinketsIntegration;
        enableBackpackAbilities = data.enableBackpackAbilities;
        backpackDeathPlace = data.backpackDeathPlace;
        backpackForceDeathPlace = data.backpackForceDeathPlace;
        enableEmptyTankButton = data.enableEmptyTankButton;
        enableSleepingBagSpawnPoint = data.enableSleepingBagSpawnPoint;

        //Client
        enableBackpackCoordsMessage = data.enableBackpackCoordsMessage;
        enableToolCycling = data.enableToolCycling;
        disableScrollWheel = data.disableScrollWheel;
        obtainTips = data.obtainTips;
        renderTools = data.renderTools;
        renderBackpackWithElytra = data.renderBackpackWithElytra;

        enableOverlay = data.enableOverlay;
        offsetX = data.offsetX;
        offsetY = data.offsetY;
    }
}