package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SupporterBadgePacket {
    public record Serverbound(boolean isEnabledForPlayer) implements CustomPacketPayload {
        public static final Type<Serverbound> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "supporter_badge_serverbound"));
        public static final StreamCodec<FriendlyByteBuf, Serverbound> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Serverbound::isEnabledForPlayer,
                Serverbound::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(final Serverbound message, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                Player player = ctx.player();
                if(message.isEnabledForPlayer && !Supporters.SUPPORTERS.contains(player.getGameProfile().getName())) {
                    if(Supporters.SUPPORTERS_REFERENCE.contains(player.getGameProfile().getName())) {
                        Supporters.SUPPORTERS.add(player.getGameProfile().getName());
                        PacketDistributor.sendToAllPlayers(new Clientbound(true, player.getGameProfile().getName()));
                    }
                } else if(!message.isEnabledForPlayer) {
                    Supporters.SUPPORTERS.remove(player.getGameProfile().getName());
                    PacketDistributor.sendToAllPlayers(new Clientbound(false, player.getGameProfile().getName()));
                }
            });
        }
    }

    public record Clientbound(boolean isEnabledForPlayer, String playerName) implements CustomPacketPayload {
        public static final Type<Clientbound> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "supporter_badge_clientbound"));
        public static final StreamCodec<FriendlyByteBuf, Clientbound> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Clientbound::isEnabledForPlayer,
                ByteBufCodecs.STRING_UTF8, Clientbound::playerName,
                Clientbound::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(final Clientbound message, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
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
