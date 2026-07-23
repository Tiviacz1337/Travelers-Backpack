package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class SupporterBadgePacket {
    public record Serverbound(boolean isEnabledForPlayer) implements CustomPacketPayload {
        public static final Type<Serverbound> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "supporter_badge_serverbound"));
        public static final StreamCodec<FriendlyByteBuf, Serverbound> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Serverbound::isEnabledForPlayer,
                Serverbound::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(Serverbound message, ServerPlayNetworking.Context ctx) {
            ctx.server().execute(() -> {
                Player player = ctx.player();
                if(message.isEnabledForPlayer && !Supporters.SUPPORTERS.contains(player.getGameProfile().name())) {
                    if(Supporters.SUPPORTERS_REFERENCE.contains(player.getGameProfile().name())) {
                        Supporters.SUPPORTERS.add(player.getGameProfile().name());
                        PacketDistributor.sendToAllPlayers(new Clientbound(true, player.getGameProfile().name()), ctx.server());
                    }
                } else if(!message.isEnabledForPlayer) {
                    Supporters.SUPPORTERS.remove(player.getGameProfile().name());
                    PacketDistributor.sendToAllPlayers(new Clientbound(false, player.getGameProfile().name()), ctx.server());
                }
            });
        }
    }

    public record Clientbound(boolean isEnabledForPlayer, String playerName) implements CustomPacketPayload {
        public static final Type<Clientbound> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "supporter_badge_clientbound"));
        public static final StreamCodec<FriendlyByteBuf, Clientbound> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Clientbound::isEnabledForPlayer,
                ByteBufCodecs.STRING_UTF8, Clientbound::playerName,
                Clientbound::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(Clientbound message, ClientPlayNetworking.Context ctx) {
            ctx.client().execute(() -> {
                if(message.isEnabledForPlayer && !Supporters.SUPPORTERS.contains(message.playerName)) {
                    if(Supporters.SUPPORTERS_REFERENCE.contains(message.playerName)) {
                        Supporters.SUPPORTERS.add(message.playerName);
                    }
                } else if(!message.isEnabledForPlayer) {
                    Supporters.SUPPORTERS.remove(message.playerName);
                }
            });
        }
    }
}