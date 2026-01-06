package com.tiviacz.travelersbackpack.compat.common;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class RecipeViewersNetwork {
    public static void registerPackets(PayloadRegistrar registrar) {
        registrar.playToServer(ServerboundGhostSlotPacket.TYPE, ServerboundGhostSlotPacket.STREAM_CODEC, ServerboundGhostSlotPacket::handle);
    }
}