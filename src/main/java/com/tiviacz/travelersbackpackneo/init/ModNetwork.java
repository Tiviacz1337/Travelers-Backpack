package com.tiviacz.travelersbackpackneo.init;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfigData;
import com.tiviacz.travelersbackpackold.network.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModNetwork
{
    public static void initClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(UpdateConfigPacket.PACKET_ID, UpdateConfigPacket::apply);
        ClientPlayNetworking.registerGlobalReceiver(SyncBackpackPacket.PACKET_ID, SyncBackpackPacket::apply);
        ClientPlayNetworking.registerGlobalReceiver(SyncItemStackPacket.PACKET_ID, SyncItemStackPacket::apply);
        ClientPlayNetworking.registerGlobalReceiver(SendMessagePacket.PACKET_ID, SendMessagePacket::apply);
    }

    public static void initServer()
    {
        PayloadTypeRegistry.playS2C().register(UpdateConfigPacket.PACKET_ID, UpdateConfigPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBackpackPacket.PACKET_ID, SyncBackpackPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncItemStackPacket.PACKET_ID, SyncItemStackPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SendMessagePacket.PACKET_ID, SendMessagePacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(EquipBackpackPacket.PACKET_ID, EquipBackpackPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SleepingBagPacket.PACKET_ID, SleepingBagPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SpecialActionPacket.PACKET_ID, SpecialActionPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(AbilitySliderPacket.PACKET_ID, AbilitySliderPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SorterPacket.PACKET_ID, SorterPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SlotPacket.PACKET_ID, SlotPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(MemoryPacket.PACKET_ID, MemoryPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SettingsPacket.PACKET_ID, SettingsPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EquipBackpackPacket.PACKET_ID, EquipBackpackPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(SleepingBagPacket.PACKET_ID, SleepingBagPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(SpecialActionPacket.PACKET_ID, SpecialActionPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(AbilitySliderPacket.PACKET_ID, AbilitySliderPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(SorterPacket.PACKET_ID, SorterPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(SlotPacket.PACKET_ID, SlotPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(MemoryPacket.PACKET_ID, MemoryPacket::apply);
        ServerPlayNetworking.registerGlobalReceiver(SettingsPacket.PACKET_ID, SettingsPacket::apply);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
        {
            //Load default config from file
            TravelersBackpack.LOGGER.info("Loading config from file...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load();

            //Sync config from server to client if present
            ServerPlayNetworking.send(handler.player, new UpdateConfigPacket(TravelersBackpackConfig.writeToNbt()));

            //Packets to sync backpack component to client on login (Cardinal Components autosync somehow doesn't sync properly)

            //Sync to target client
            sender.sendPacket(new SyncBackpackPacket(handler.getPlayer().getId(), ComponentUtils.getWearingBackpack(handler.getPlayer())));

            //Sync backpacks of all players in radius of 64 blocks
            for(ServerPlayerEntity serverPlayer : PlayerLookup.around(handler.getPlayer().getServerWorld(), handler.getPlayer().getPos(), 64))
            {
                sender.sendPacket(new SyncBackpackPacket(serverPlayer.getId(), ComponentUtils.getWearingBackpack(serverPlayer)));
            }
        });
    }
}