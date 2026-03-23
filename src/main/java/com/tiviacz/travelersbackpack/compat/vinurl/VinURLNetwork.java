package com.tiviacz.travelersbackpack.compat.vinurl;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class VinURLNetwork {
    public static void register(PayloadRegistrar registrar) {
        registrar.playToServer(ServerboundVinURLStartPacket.TYPE, ServerboundVinURLStartPacket.STREAM_CODEC, ServerboundVinURLStartPacket::handle);
        registrar.playToServer(ServerboundVinURLStopPacket.TYPE, ServerboundVinURLStopPacket.STREAM_CODEC, ServerboundVinURLStopPacket::handle);
    }
}