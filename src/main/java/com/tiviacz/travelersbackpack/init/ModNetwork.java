package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModNetwork {
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound::handle);

        //Synchronise supporter badge visibility
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            boolean badgeVisibility = TravelersBackpackConfig.CLIENT.showSupporterBadge.get();
            PacketDistributor.sendToServer(new SupporterBadgePacket.Serverbound(badgeVisibility));
        });
    }

    public static void initServer() {
        PayloadTypeRegistry.playS2C().register(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundFilterTagsPacket.TYPE, ServerboundFilterTagsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundSlotPacket.TYPE, ServerboundSlotPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundActionTagPacket.TYPE, ServerboundActionTagPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundFilterTagsPacket.TYPE, ServerboundFilterTagsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSlotPacket.TYPE, ServerboundSlotPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundActionTagPacket.TYPE, ServerboundActionTagPacket::handle);
    }
}