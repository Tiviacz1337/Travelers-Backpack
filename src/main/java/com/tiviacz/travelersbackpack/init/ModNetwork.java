package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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

        channel.messageBuilder(ClientboundSyncCapabilityPacket.class, 0)
                .decoder(ClientboundSyncCapabilityPacket::decode)
                .encoder(ClientboundSyncCapabilityPacket::encode)
                .consumer(ClientboundSyncCapabilityPacket::handle)
                .add();

        channel.messageBuilder(ServerboundEquipBackpackPacket.class, 1)
                .decoder(ServerboundEquipBackpackPacket::decode)
                .encoder(ServerboundEquipBackpackPacket::encode)
                .consumer(ServerboundEquipBackpackPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSleepingBagPacket.class, 2)
                .decoder(ServerboundSleepingBagPacket::decode)
                .encoder(ServerboundSleepingBagPacket::encode)
                .consumer(ServerboundSleepingBagPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSpecialActionPacket.class, 3)
                .decoder(ServerboundSpecialActionPacket::decode)
                .encoder(ServerboundSpecialActionPacket::encode)
                .consumer(ServerboundSpecialActionPacket::handle)
                .add();


        channel.messageBuilder(ClientboundUpdateRecipePacket.class, 4)
                .decoder(ClientboundUpdateRecipePacket::decode)
                .encoder(ClientboundUpdateRecipePacket::encode)
                .consumer(ClientboundUpdateRecipePacket::handle)
                .add();

        channel.messageBuilder(ServerboundAbilitySliderPacket.class, 5)
                .decoder(ServerboundAbilitySliderPacket::decode)
                .encoder(ServerboundAbilitySliderPacket::encode)
                .consumer(ServerboundAbilitySliderPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSorterPacket.class, 6)
                .decoder(ServerboundSorterPacket::decode)
                .encoder(ServerboundSorterPacket::encode)
                .consumer(ServerboundSorterPacket::handle)
                .add();

        channel.messageBuilder(ServerboundSlotPacket.class, 7)
                .decoder(ServerboundSlotPacket::decode)
                .encoder(ServerboundSlotPacket::encode)
                .consumer(ServerboundSlotPacket::handle)
                .add();

        channel.messageBuilder(ServerboundMemoryPacket.class, 8)
                .decoder(ServerboundMemoryPacket::decode)
                .encoder(ServerboundMemoryPacket::encode)
                .consumer(ServerboundMemoryPacket::handle)
                .add();

        return channel;
    }
}