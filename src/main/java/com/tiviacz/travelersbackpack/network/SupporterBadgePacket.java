package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SupporterBadgePacket {
    public static class Serverbound {
        private final boolean isEnabledForPlayer;

        public Serverbound(boolean isEnabledForPlayer) {
            this.isEnabledForPlayer = isEnabledForPlayer;
        }

        public static Serverbound decode(final FriendlyByteBuf buffer) {
            final boolean isEnabledForPlayer = buffer.readBoolean();

            return new Serverbound(isEnabledForPlayer);
        }

        public static void encode(final Serverbound message, final FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.isEnabledForPlayer);
        }

        public static void handle(final Serverbound message, final Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Player player = ctx.get().getSender();
                if(message.isEnabledForPlayer && !Supporters.SUPPORTERS.contains(player.getGameProfile().getName())) {
                    if(Supporters.SUPPORTERS_REFERENCE.contains(player.getGameProfile().getName())) {
                        Supporters.SUPPORTERS.add(player.getGameProfile().getName());
                        PacketDistributorHelper.sendToAllPlayers(new Clientbound(true, player.getGameProfile().getName()));
                    }
                } else if(!message.isEnabledForPlayer) {
                    Supporters.SUPPORTERS.remove(player.getGameProfile().getName());
                    PacketDistributorHelper.sendToAllPlayers(new Clientbound(false, player.getGameProfile().getName()));
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static class Clientbound {
        private final boolean isEnabledForPlayer;
        private final String playerName;

        public Clientbound(boolean isEnabledForPlayer, String playerName) {
            this.isEnabledForPlayer = isEnabledForPlayer;
            this.playerName = playerName;
        }

        public static Clientbound decode(final FriendlyByteBuf buffer) {
            final boolean isEnabledForPlayer = buffer.readBoolean();
            final String playerName = buffer.readUtf();

            return new Clientbound(isEnabledForPlayer, playerName);
        }

        public static void encode(final Clientbound message, final FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.isEnabledForPlayer);
            buffer.writeUtf(message.playerName);
        }

        public static void handle(final Clientbound message, final Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if(message.isEnabledForPlayer && !Supporters.SUPPORTERS.contains(message.playerName)) {
                    if(Supporters.SUPPORTERS_REFERENCE.contains(message.playerName)) {
                        Supporters.SUPPORTERS.add(message.playerName);
                    }
                } else if(!message.isEnabledForPlayer) {
                    Supporters.SUPPORTERS.remove(message.playerName);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
