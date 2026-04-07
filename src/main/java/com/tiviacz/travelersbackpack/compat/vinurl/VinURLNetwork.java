package com.tiviacz.travelersbackpack.compat.vinurl;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VinURLNetwork {
    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(ServerboundVinURLStartPacket.TYPE, ServerboundVinURLStartPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundVinURLStopPacket.TYPE, ServerboundVinURLStopPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundVinURLStartPacket.TYPE, ServerboundVinURLStartPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundVinURLStopPacket.TYPE, ServerboundVinURLStopPacket::handle);
    }
}