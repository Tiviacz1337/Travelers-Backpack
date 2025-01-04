package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSendMessagePacket {
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

    public static void encode(final ClientboundSendMessagePacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.drop);
        buffer.writeBlockPos(message.pos);
    }

    public static void handle(final ClientboundSendMessagePacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(TravelersBackpackConfig.CLIENT.sendBackpackCoordinatesMessage.get()) {
                if(Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.translatable(message.drop ? "information.travelersbackpack.backpack_drop" : "information.travelersbackpack.backpack_coords", message.pos.getX(), message.pos.getY(), message.pos.getZ()));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}