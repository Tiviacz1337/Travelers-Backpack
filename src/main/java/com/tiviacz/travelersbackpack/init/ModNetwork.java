package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModNetwork {
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncComponentsPacket.TYPE, ClientboundSyncComponentsPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundVersionCheckPacket.TYPE, ClientboundVersionCheckPacket::handle);

        //Synchronise supporter badge visibility
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            boolean badgeVisibility = TravelersBackpackConfig.CLIENT.showSupporterBadge.get();
            PacketDistributor.sendToServer(new SupporterBadgePacket.Serverbound(badgeVisibility));
        });

        //Check Traveler's Backpack version
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientboundVersionCheckPacket.versionCheckTimeout = ClientPlayNetworking.canSend(ServerboundActionTagPacket.TYPE) ? 100 : -1;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientboundVersionCheckPacket.versionCheckTimeout = -1);
        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientboundVersionCheckPacket.tickVersionCheck());
    }

    public static void initServer() {
        PayloadTypeRegistry.clientboundPlay().register(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundSyncComponentsPacket.TYPE, ClientboundSyncComponentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundVersionCheckPacket.TYPE, ClientboundVersionCheckPacket.STREAM_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundFilterTagsPacket.TYPE, ServerboundFilterTagsPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundSlotPacket.TYPE, ServerboundSlotPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundActionTagPacket.TYPE, ServerboundActionTagPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundFilterTagsPacket.TYPE, ServerboundFilterTagsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSlotPacket.TYPE, ServerboundSlotPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundActionTagPacket.TYPE, ServerboundActionTagPacket::handle);

        //Check TB version
        ServerPlayerEvents.JOIN.register(player -> {
            if(ServerPlayNetworking.canSend(player, ClientboundVersionCheckPacket.TYPE)) {
                PacketDistributor.sendToPlayer(player, new ClientboundVersionCheckPacket(TravelersBackpack.getModVersion()));
            } else if(ServerPlayNetworking.canSend(player, ClientboundSyncAttachmentPacket.TYPE)) {
                //Client has older version of TB
                LogHelper.warn("Version mismatch! Player " + player.getGameProfile().name() + " has an older version of Traveler's Backpack than the server (server has " + TravelersBackpack.getModVersion() + "). Errors may occur.");
            }
        });
    }
}