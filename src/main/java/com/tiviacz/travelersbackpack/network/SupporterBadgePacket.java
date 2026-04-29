package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SupporterBadgePacket {
    public static class Serverbound implements IPacket<Serverbound> {
        private final boolean isEnabledForPlayer;

        public Serverbound(boolean isEnabledForPlayer) {
            this.isEnabledForPlayer = isEnabledForPlayer;
        }

        public static Serverbound decode(FriendlyByteBuf buffer) {
            boolean isEnabledForPlayer = buffer.readBoolean();

            return new Serverbound(isEnabledForPlayer);
        }

        public void encode(Serverbound message, FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.isEnabledForPlayer);
        }

        @Override
        public ResourceLocation getPacketId() {
            return ModNetwork.SUPPORTER_BADGE_SERVERBOUND_ID;
        }

        public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            Serverbound message = Serverbound.decode(buf);
            server.execute(() -> {
                if(message.isEnabledForPlayer && !Supporters.SUPPORTERS.contains(player.getGameProfile().getName())) {
                    if(Supporters.SUPPORTERS_REFERENCE.contains(player.getGameProfile().getName())) {
                        Supporters.SUPPORTERS.add(player.getGameProfile().getName());
                        PacketDistributorHelper.sendToAllPlayers(new Clientbound(true, player.getGameProfile().getName()), server);
                    }
                } else if(!message.isEnabledForPlayer) {
                    Supporters.SUPPORTERS.remove(player.getGameProfile().getName());
                    PacketDistributorHelper.sendToAllPlayers(new Clientbound(false, player.getGameProfile().getName()), server);
                }
            });
        }
    }

    public static class Clientbound implements IPacket<Clientbound> {
        private final boolean isEnabledForPlayer;
        private final String playerName;

        public Clientbound(boolean isEnabledForPlayer, String playerName) {
            this.isEnabledForPlayer = isEnabledForPlayer;
            this.playerName = playerName;
        }

        public static Clientbound decode(FriendlyByteBuf buffer) {
            boolean isEnabledForPlayer = buffer.readBoolean();
            String playerName = buffer.readUtf();

            return new Clientbound(isEnabledForPlayer, playerName);
        }

        public void encode(Clientbound message, FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.isEnabledForPlayer);
            buffer.writeUtf(message.playerName);
        }

        @Override
        public ResourceLocation getPacketId() {
            return ModNetwork.SUPPORTER_BADGE_CLIENTBOUND_ID;
        }

        public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
            Clientbound message = decode(buf);
            client.execute(() -> {
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
