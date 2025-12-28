package com.tiviacz.travelersbackpack.compat.common;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class RecipeViewersNetwork {
    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ServerboundGhostSlotPacket.GHOST_SLOT_ID, ServerboundGhostSlotPacket::handle);
    }
}
