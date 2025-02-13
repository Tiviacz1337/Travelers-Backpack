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

        channel.messageBuilder(ServerboundEquipBackpackPacket.class, 1)
                .decoder(ServerboundEquipBackpackPacket::decode)
                .encoder(ServerboundEquipBackpackPacket::encode)
                .consumerMainThread(ServerboundEquipBackpackPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSleepingBagPacket.class, 2)
                .decoder(ServerboundSleepingBagPacket::decode)
                .encoder(ServerboundSleepingBagPacket::encode)
                .consumerMainThread(ServerboundSleepingBagPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSpecialActionPacket.class, 3)
                .decoder(ServerboundSpecialActionPacket::decode)
                .encoder(ServerboundSpecialActionPacket::encode)
                .consumerMainThread(ServerboundSpecialActionPacket::handle)
                .add();

        channel.messageBuilder(ClientboundUpdateRecipePacket.class, 4, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundUpdateRecipePacket::decode)
                .encoder(ClientboundUpdateRecipePacket::encode)
                .consumerMainThread(ClientboundUpdateRecipePacket::handle)
                .add();

        channel.messageBuilder(ServerboundAbilitySliderPacket.class, 5)
                .decoder(ServerboundAbilitySliderPacket::decode)
                .encoder(ServerboundAbilitySliderPacket::encode)
                .consumerMainThread(ServerboundAbilitySliderPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSorterPacket.class, 6)
                .decoder(ServerboundSorterPacket::decode)
                .encoder(ServerboundSorterPacket::encode)
                .consumerMainThread(ServerboundSorterPacket::handle)
                .add();

        channel.messageBuilder(ServerboundShowToolSlotsPacket.class, 7)
                .decoder(ServerboundShowToolSlotsPacket::decode)
                .encoder(ServerboundShowToolSlotsPacket::encode)
                .consumerMainThread(ServerboundShowToolSlotsPacket::handle)
                .add();

        channel.messageBuilder(ServerboundOpenSettingsPacket.class, 8, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundOpenSettingsPacket::decode)
                .encoder(ServerboundOpenSettingsPacket::encode)
                .consumerMainThread(ServerboundOpenSettingsPacket::handle)
                .add();

        channel.messageBuilder(ServerboundRemoveUpgradePacket.class, 9)
                .decoder(ServerboundRemoveUpgradePacket::decode)
                .encoder(ServerboundRemoveUpgradePacket::encode)
                .consumerMainThread(ServerboundRemoveUpgradePacket::handle)
                .add();

        channel.messageBuilder(ClientboundSendMessagePacket.class, 10)
                .decoder(ClientboundSendMessagePacket::decode)
                .encoder(ClientboundSendMessagePacket::encode)
                .consumerMainThread(ClientboundSendMessagePacket::handle)
                .add();

        channel.messageBuilder(ClientboundSyncItemStackPacket.class, 11, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncItemStackPacket::decode)
                .encoder(ClientboundSyncItemStackPacket::encode)
                .consumerNetworkThread(ClientboundSyncItemStackPacket::handle)
                .add();

        channel.messageBuilder(ClientboundSyncComponentsPacket.class, 12, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncComponentsPacket::decode)
                .encoder(ClientboundSyncComponentsPacket::encode)
                .consumerNetworkThread(ClientboundSyncComponentsPacket::handle)
                .add();

        channel.messageBuilder(ServerboundFillTankPacket.class, 13)
                .decoder(ServerboundFillTankPacket::decode)
                .encoder(ServerboundFillTankPacket::encode)
                .consumerMainThread(ServerboundFillTankPacket::handle)
                .add();

        channel.messageBuilder(ServerboundFilterSettingsPacket.class, 14)
                .decoder(ServerboundFilterSettingsPacket::decode)
                .encoder(ServerboundFilterSettingsPacket::encode)
                .consumerMainThread(ServerboundFilterSettingsPacket::handle)
                .add();

        channel.messageBuilder(ServerboundOpenBackpackPacket.class, 15)
                .decoder(ServerboundOpenBackpackPacket::decode)
                .encoder(ServerboundOpenBackpackPacket::encode)
                .consumerMainThread(ServerboundOpenBackpackPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSlotPacket.class, 16, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundSlotPacket::decode)
                .encoder(ServerboundSlotPacket::encode)
                .consumerMainThread(ServerboundSlotPacket::handle)
                .add();

        channel.messageBuilder(ServerboundTabPacket.class, 17)
                .decoder(ServerboundTabPacket::decode)
                .encoder(ServerboundTabPacket::encode)
                .consumerMainThread(ServerboundTabPacket::handle)
                .add();

        channel.messageBuilder(SupporterBadgePacket.Serverbound.class, 18, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SupporterBadgePacket.Serverbound::decode)
                .encoder(SupporterBadgePacket.Serverbound::encode)
                .consumerMainThread(SupporterBadgePacket.Serverbound::handle)
                .add();

        channel.messageBuilder(SupporterBadgePacket.Clientbound.class, 19, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SupporterBadgePacket.Clientbound::decode)
                .encoder(SupporterBadgePacket.Clientbound::encode)
                .consumerMainThread(SupporterBadgePacket.Clientbound::handle)
                .add();

        channel.messageBuilder(ServerboundRetrieveBackpackPacket.class, 20)
                .decoder(ServerboundRetrieveBackpackPacket::decode)
                .encoder(ServerboundRetrieveBackpackPacket::encode)
                .consumerMainThread(ServerboundRetrieveBackpackPacket::handle)
                .add();

        return channel;
    }
}