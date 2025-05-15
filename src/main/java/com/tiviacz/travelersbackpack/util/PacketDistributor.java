package com.tiviacz.travelersbackpack.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PacketDistributor {
    public static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void sendToPlayer(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        if(serverPlayer.connection == null) return; //?
        ServerPlayNetworking.send(serverPlayer, payload);
    }

    public static void sendToPlayersTrackingEntityAndSelf(Player player, CustomPacketPayload payload) {
        ServerPlayNetworking.send((ServerPlayer)player, payload);
        for(ServerPlayer sp : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(sp, payload);
        }
    }

    public static void sendToAllPlayers(CustomPacketPayload packet, MinecraftServer server) {
        for(ServerPlayer player : PlayerLookup.all(server)) {
            if(player.connection == null) return; //?
            ServerPlayNetworking.send(player, packet);
        }
    }
}
