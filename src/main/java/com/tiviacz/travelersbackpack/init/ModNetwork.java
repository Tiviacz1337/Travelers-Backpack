package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.network.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork
{
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(TravelersBackpack.MODID, "network");
    public static final String NETWORK_VERSION = new ResourceLocation(TravelersBackpack.MODID, "1").toString();

    public static SimpleChannel registerNetworkChannel() {
        final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .simpleChannel();

        TravelersBackpack.NETWORK = channel;

        channel.messageBuilder(CSyncCapabilityPacket.class, 0)
                .decoder(CSyncCapabilityPacket::decode)
                .encoder(CSyncCapabilityPacket::encode)
                .consumer(CSyncCapabilityPacket::handle)
                .add();

        channel.messageBuilder(SEquipBackpackPacket.class, 1)
                .decoder(SEquipBackpackPacket::decode)
                .encoder(SEquipBackpackPacket::encode)
                .consumer(SEquipBackpackPacket::handle)
                .add();

        channel.messageBuilder(SSleepingBagPacket.class, 2)
                .decoder(SSleepingBagPacket::decode)
                .encoder(SSleepingBagPacket::encode)
                .consumer(SSleepingBagPacket::handle)
                .add();

        channel.messageBuilder(SSpecialActionPacket.class, 3)
                .decoder(SSpecialActionPacket::decode)
                .encoder(SSpecialActionPacket::encode)
                .consumer(SSpecialActionPacket::handle)
                .add();

        channel.messageBuilder(CUpdateRecipePacket.class, 4)
                .decoder(CUpdateRecipePacket::decode)
                .encoder(CUpdateRecipePacket::encode)
                .consumer(CUpdateRecipePacket::handle)
                .add();

        channel.messageBuilder(SAbilitySliderPacket.class, 5)
                .decoder(SAbilitySliderPacket::decode)
                .encoder(SAbilitySliderPacket::encode)
                .consumer(SAbilitySliderPacket::handle)
                .add();

        channel.messageBuilder(SSorterPacket.class, 6)
                .decoder(SSorterPacket::decode)
                .encoder(SSorterPacket::encode)
                .consumer(SSorterPacket::handle)
                .add();

        channel.messageBuilder(SSlotPacket.class, 7)
                .decoder(SSlotPacket::decode)
                .encoder(SSlotPacket::encode)
                .consumer(SSlotPacket::handle)
                .add();

        channel.messageBuilder(SMemoryPacket.class, 8)
                .decoder(SMemoryPacket::decode)
                .encoder(SMemoryPacket::encode)
                .consumer(SMemoryPacket::handle)
                .add();

        return channel;
    }
}