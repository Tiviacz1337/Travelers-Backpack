package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.network.*;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    public static void register(final PayloadRegistrar registrar) {
        //Client
        registrar.playToClient(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket.STREAM_CODEC, ClientboundSyncAttachmentPacket::handle);
        registrar.playToClient(ClientboundSyncComponentsPacket.TYPE, ClientboundSyncComponentsPacket.STREAM_CODEC, ClientboundSyncComponentsPacket::handle);
        registrar.playToClient(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket.STREAM_CODEC, ClientboundUpdateRecipePacket::handle);
        registrar.playToClient(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket.STREAM_CODEC, ClientboundSendMessagePacket::handle);
        registrar.playToClient(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket.STREAM_CODEC, ClientboundSyncItemStackPacket::handle);
        registrar.playToClient(SupporterBadgePacket.Clientbound.TYPE, SupporterBadgePacket.Clientbound.STREAM_CODEC, SupporterBadgePacket.Clientbound::handle);

        //Server
        registrar.playToServer(ServerboundSlotPacket.TYPE, ServerboundSlotPacket.STREAM_CODEC, ServerboundSlotPacket::handle);
        registrar.playToServer(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket.STREAM_CODEC, ServerboundFilterSettingsPacket::handle);
        registrar.playToServer(ServerboundFilterTagsPacket.TYPE, ServerboundFilterTagsPacket.STREAM_CODEC, ServerboundFilterTagsPacket::handle);
        registrar.playToServer(SupporterBadgePacket.Serverbound.TYPE, SupporterBadgePacket.Serverbound.STREAM_CODEC, SupporterBadgePacket.Serverbound::handle);
        registrar.playToServer(ServerboundRetrieveBackpackPacket.TYPE, ServerboundRetrieveBackpackPacket.STREAM_CODEC, ServerboundRetrieveBackpackPacket::handle);
        registrar.playToServer(ServerboundActionTagPacket.TYPE, ServerboundActionTagPacket.STREAM_CODEC, ServerboundActionTagPacket::handle);
    }
}