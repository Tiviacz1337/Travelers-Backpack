package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(TravelersBackpack.MODID, "network");
    public static final String NETWORK_VERSION = new ResourceLocation(TravelersBackpack.MODID, "1").toString();

    public static SimpleChannel registerNetworkChannel() {
        final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .simpleChannel();


        TravelersBackpack.NETWORK = channel;

        channel.messageBuilder(ClientboundSyncCapabilityPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncCapabilityPacket::decode)
                .encoder(ClientboundSyncCapabilityPacket::encode)
                .consumerNetworkThread(ClientboundSyncCapabilityPacket::handle)
                .add();

        channel.messageBuilder(ClientboundUpdateRecipePacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundUpdateRecipePacket::decode)
                .encoder(ClientboundUpdateRecipePacket::encode)
                .consumerMainThread(ClientboundUpdateRecipePacket::handle)
                .add();

        channel.messageBuilder(ClientboundSendMessagePacket.class, 2)
                .decoder(ClientboundSendMessagePacket::decode)
                .encoder(ClientboundSendMessagePacket::encode)
                .consumerMainThread(ClientboundSendMessagePacket::handle)
                .add();

        channel.messageBuilder(ClientboundSyncItemStackPacket.class, 3, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncItemStackPacket::decode)
                .encoder(ClientboundSyncItemStackPacket::encode)
                .consumerNetworkThread(ClientboundSyncItemStackPacket::handle)
                .add();

        channel.messageBuilder(ClientboundSyncComponentsPacket.class, 4, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncComponentsPacket::decode)
                .encoder(ClientboundSyncComponentsPacket::encode)
                .consumerNetworkThread(ClientboundSyncComponentsPacket::handle)
                .add();

        channel.messageBuilder(ServerboundFilterSettingsPacket.class, 5)
                .decoder(ServerboundFilterSettingsPacket::decode)
                .encoder(ServerboundFilterSettingsPacket::encode)
                .consumerMainThread(ServerboundFilterSettingsPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSlotPacket.class, 6, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundSlotPacket::decode)
                .encoder(ServerboundSlotPacket::encode)
                .consumerMainThread(ServerboundSlotPacket::handle)
                .add();

        channel.messageBuilder(SupporterBadgePacket.Serverbound.class, 7, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SupporterBadgePacket.Serverbound::decode)
                .encoder(SupporterBadgePacket.Serverbound::encode)
                .consumerMainThread(SupporterBadgePacket.Serverbound::handle)
                .add();

        channel.messageBuilder(SupporterBadgePacket.Clientbound.class, 8, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SupporterBadgePacket.Clientbound::decode)
                .encoder(SupporterBadgePacket.Clientbound::encode)
                .consumerMainThread(SupporterBadgePacket.Clientbound::handle)
                .add();

        channel.messageBuilder(ServerboundRetrieveBackpackPacket.class, 9)
                .decoder(ServerboundRetrieveBackpackPacket::decode)
                .encoder(ServerboundRetrieveBackpackPacket::encode)
                .consumerMainThread(ServerboundRetrieveBackpackPacket::handle)
                .add();

        channel.messageBuilder(ServerboundActionTagPacket.class, 10)
                .decoder(ServerboundActionTagPacket::decode)
                .encoder(ServerboundActionTagPacket::encode)
                .consumerMainThread(ServerboundActionTagPacket::handle)
                .add();

        channel.messageBuilder(ServerboundFilterTagsPacket.class, 11)
                .decoder(ServerboundFilterTagsPacket::decode)
                .encoder(ServerboundFilterTagsPacket::encode)
                .consumerMainThread(ServerboundFilterTagsPacket::handle)
                .add();

        return channel;
    }
}