package com.tiviacz.travelersbackpack.compat.common;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class RecipeViewersNetwork {
    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(ServerboundGhostSlotPacket.TYPE, ServerboundGhostSlotPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundGhostSlotPacket.TYPE, ServerboundGhostSlotPacket::handle);
    }
}
