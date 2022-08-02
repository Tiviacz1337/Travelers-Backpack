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
    public static boolean disableCrafting;
    public static boolean enableBackpackBlockQuickEquip;
    public static boolean enableLoot;
    public static boolean invulnerableBackpack;
    public static boolean enableBackpackAbilities;
    public static long tanksCapacity;

    //Common
    public static boolean trinketsIntegration;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableSleepingBagSpawnPoint;

    //Client
    public static boolean enableBackpackCoordsMessage;
    public static boolean enableToolCycling;
    //public static boolean disableScrollWheel;
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

        bake(null, data); // To load the initial config into the data.  Not sure why the config is not loaded until server start after this
        ServerLifecycleEvents.SERVER_STARTED.register(server -> bake(server, data));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, b) -> bake(server, data));
    }

    public static void bake(MinecraftServer server, TravelersBackpackConfigData data)
    {
        disableCrafting = data.disableCrafting;
        enableBackpackBlockQuickEquip = data.enableBackpackBlockQuickEquip;
        enableLoot = data.enableLoot;
        invulnerableBackpack = data.invulnerableBackpack;
        enableBackpackAbilities = data.enableBackpackAbilities;
        tanksCapacity = data.tanksCapacity;

        trinketsIntegration = data.trinketsIntegration;
        backpackDeathPlace = data.backpackDeathPlace;
        backpackForceDeathPlace = data.backpackForceDeathPlace;
        enableSleepingBagSpawnPoint = data.enableSleepingBagSpawnPoint;

        if(server == null)
        {
            //Client
            enableBackpackCoordsMessage = data.enableBackpackCoordsMessage;
            enableToolCycling = data.enableToolCycling;
            //disableScrollWheel = data.disableScrollWheel;
            obtainTips = data.obtainTips;
            renderTools = data.renderTools;
            renderBackpackWithElytra = data.renderBackpackWithElytra;

            enableOverlay = data.enableOverlay;
            offsetX = data.offsetX;
            offsetY = data.offsetY;
        }
        else
        {
            PacketByteBuf buf= PacketByteBufs.create();
            buf.writeNbt(toNbt());
            server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, ModNetwork.UPDATE_CONFIG_ID, buf));
        }
    }

    public static NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("disableCrafting",disableCrafting);
        nbt.putBoolean("enableBackpackBlockQuickEquip",enableBackpackBlockQuickEquip);
        nbt.putBoolean("enableLoot",enableLoot);
        nbt.putBoolean("invulnerableBackpack",invulnerableBackpack);
        nbt.putBoolean("enableBackpackAbilities",enableBackpackAbilities);
        nbt.putLong("tanksCapacity",tanksCapacity);

        //Common
        nbt.putBoolean("trinketsIntegration",trinketsIntegration);
        nbt.putBoolean("backpackDeathPlace",backpackDeathPlace);
        nbt.putBoolean("backpackForceDeathPlace",backpackForceDeathPlace);
        nbt.putBoolean("enableSleepingBagSpawnPoint",enableSleepingBagSpawnPoint);

        return nbt;
    }

    public static void fromNbt(NbtCompound nbt)
    {
        disableCrafting=nbt.getBoolean("disableCrafting");
        enableBackpackBlockQuickEquip=nbt.getBoolean("enableBackpackBlockQuickEquip");
        enableLoot=nbt.getBoolean("enableLoot");
        invulnerableBackpack=nbt.getBoolean("invulnerableBackpack");
        enableBackpackAbilities=nbt.getBoolean("enableBackpackAbilities");
        tanksCapacity=nbt.getLong("tanksCapacity");

        //Common
        trinketsIntegration=nbt.getBoolean("trinketsIntegration");
        backpackDeathPlace=nbt.getBoolean("backpackDeathPlace");
        backpackForceDeathPlace =nbt.getBoolean("backpackForceDeathPlace");
        enableSleepingBagSpawnPoint =nbt.getBoolean("enableSleepingBagSpawnPoint");
    }
}