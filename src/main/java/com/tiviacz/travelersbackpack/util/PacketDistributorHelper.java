package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class PacketDistributorHelper {
    public static void sendToServer(Object object) {
        TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), object);
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        if(player.connection == null) return; //?
        TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, Object packet) {
        if(player.connection == null) return; //?
        TravelersBackpack.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
    }

    public static void sendToAllPlayers(Object packet) {
        TravelersBackpack.NETWORK.send(PacketDistributor.ALL.noArg(), packet);
    }
}
