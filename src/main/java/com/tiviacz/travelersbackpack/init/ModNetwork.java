package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModNetwork {
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateConfigPacket.TYPE, ClientboundUpdateConfigPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket::handle);
        //ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket::handle);
        //ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncComponentsPacket.TYPE, ClientboundSyncComponentsPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound::handle);

        //Synchronise supporter badge visibility
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            boolean badgeVisibility = TravelersBackpackConfig.getConfig().client.showSupporterBadge;
            PacketDistributor.sendToServer(new SupporterBadgePacket.Serverbound(badgeVisibility));
        });
    }

    public static void initServer() {
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateConfigPacket.TYPE, ClientboundUpdateConfigPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket.STREAM_CODEC);
        //PayloadTypeRegistry.playS2C().register(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket.STREAM_CODEC);
        //PayloadTypeRegistry.playS2C().register(ClientboundSyncComponentsPacket.TYPE, ClientboundSyncComponentsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(ServerboundAbilitySliderPacket.TYPE, ServerboundAbilitySliderPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundEquipBackpackPacket.TYPE, ServerboundEquipBackpackPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundFillTankPacket.TYPE, ServerboundFillTankPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundOpenBackpackPacket.TYPE, ServerboundOpenBackpackPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundOpenSettingsPacket.TYPE, ServerboundOpenSettingsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundRemoveUpgradePacket.TYPE, ServerboundRemoveUpgradePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundShowToolSlotsPacket.TYPE, ServerboundShowToolSlotsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundSleepingBagPacket.TYPE, ServerboundSleepingBagPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundSlotPacket.TYPE, ServerboundSlotPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundSorterPacket.TYPE, ServerboundSorterPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundSpecialActionPacket.TYPE, ServerboundSpecialActionPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundTabPacket.TYPE, ServerboundTabPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundAbilitySliderPacket.TYPE, ServerboundAbilitySliderPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundEquipBackpackPacket.TYPE, ServerboundEquipBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundFillTankPacket.TYPE, ServerboundFillTankPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundOpenBackpackPacket.TYPE, ServerboundOpenBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundOpenSettingsPacket.TYPE, ServerboundOpenSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRemoveUpgradePacket.TYPE, ServerboundRemoveUpgradePacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundShowToolSlotsPacket.TYPE, ServerboundShowToolSlotsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSleepingBagPacket.TYPE, ServerboundSleepingBagPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSlotPacket.TYPE, ServerboundSlotPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSorterPacket.TYPE, ServerboundSorterPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSpecialActionPacket.TYPE, ServerboundSpecialActionPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundTabPacket.TYPE, ServerboundTabPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket::handle);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            //Load default config from file
            TravelersBackpack.LOGGER.info("Loading config from file...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load();

            //Sync config from server to client if present
            ServerPlayNetworking.send(handler.player, new ClientboundUpdateConfigPacket(TravelersBackpackConfig.writeToNbt()));

            //Packets to sync backpack component to client on login (Cardinal Components autosync somehow doesn't sync properly)

            //Sync to target client //#TODO?
            //sender.sendPacket(new ClientboundSyncAttachmentPacket(handler.getPlayer().getId(), ComponentUtils.getWearingBackpack(handler.getPlayer())));

            //Sync backpacks of all players in radius of 64 blocks
            //for(ServerPlayer serverPlayer : PlayerLookup.around(handler.getPlayer().serverLevel(), handler.getPlayer().blockPosition(), 64)) {
            //sender.sendPacket(new ClientboundSyncAttachmentPacket(serverPlayer.getId(), ComponentUtils.getWearingBackpack(serverPlayer)));
            //}
        });
    }
}