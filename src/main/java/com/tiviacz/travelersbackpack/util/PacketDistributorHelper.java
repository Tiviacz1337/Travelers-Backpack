package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class PacketDistributorHelper {
    public static void sendToServer(Object object) {
        TravelersBackpack.NETWORK.send(object, PacketDistributor.SERVER.noArg());
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        TravelersBackpack.NETWORK.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, Object packet) {
        TravelersBackpack.NETWORK.send(packet, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(player));
    }

    public static void sendToAllPlayers(Object packet) {
        TravelersBackpack.NETWORK.send(packet, PacketDistributor.ALL.noArg());
    }
}
