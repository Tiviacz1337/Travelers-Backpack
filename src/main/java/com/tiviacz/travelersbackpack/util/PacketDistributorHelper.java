package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.network.IPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PacketDistributorHelper {
    public static void sendToServer(IPacket packet) {
        FriendlyByteBuf payload = PacketByteBufs.create();
        packet.encode(packet, payload);
        ClientPlayNetworking.send(packet.getPacketId(), payload);
    }

    public static void sendToPlayer(ServerPlayer serverPlayer, IPacket packet) {
        FriendlyByteBuf payload = PacketByteBufs.create();
        packet.encode(packet, payload);
        ServerPlayNetworking.send(serverPlayer, packet.getPacketId(), payload);
    }

    public static void sendToPlayersTrackingEntityAndSelf(Player player, IPacket packet) {
        FriendlyByteBuf payload = PacketByteBufs.create();
        packet.encode(packet, payload);
        ServerPlayNetworking.send((ServerPlayer)player, packet.getPacketId(), payload);
        for(ServerPlayer sp : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(sp, packet.getPacketId(), payload);
        }
    }
}
