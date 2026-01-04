package com.tiviacz.travelersbackpack.compat.common;

import net.minecraftforge.network.simple.SimpleChannel;

public class RecipeViewersNetwork {
    public static void registerPackets(SimpleChannel simpleChannel, int id) {
        simpleChannel.messageBuilder(ServerboundGhostSlotPacket.class, id)
                .decoder(ServerboundGhostSlotPacket::decode)
                .encoder(ServerboundGhostSlotPacket::encode)
                .consumerMainThread(ServerboundGhostSlotPacket::handle)
                .add();
    }
}