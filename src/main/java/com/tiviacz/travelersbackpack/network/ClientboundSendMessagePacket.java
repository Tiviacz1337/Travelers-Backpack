package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSendMessagePacket implements IPacket<ClientboundSendMessagePacket> {
    private final boolean drop;
    private final BlockPos pos;

    public ClientboundSendMessagePacket(boolean drop, BlockPos pos) {
        this.drop = drop;
        this.pos = pos;
    }

    public static ClientboundSendMessagePacket decode(final FriendlyByteBuf buffer) {
        final boolean drop = buffer.readBoolean();
        final BlockPos pos = buffer.readBlockPos();
        return new ClientboundSendMessagePacket(drop, pos);
    }

    public void encode(final ClientboundSendMessagePacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.drop);
        buffer.writeBlockPos(message.pos);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SEND_MESSAGE_ID;
    }

    public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientboundSendMessagePacket message = ClientboundSendMessagePacket.decode(buf);
        client.execute(() -> {
            if(TravelersBackpackConfig.getConfig().client.sendBackpackCoordinatesMessage) {
                if(client.player != null) {
                    client.player.sendSystemMessage(Component.translatable(message.drop ? "information.travelersbackpack.backpack_drop" : "information.travelersbackpack.backpack_coords", message.pos.getX(), message.pos.getY(), message.pos.getZ()));
                }
            }
        });
    }
}