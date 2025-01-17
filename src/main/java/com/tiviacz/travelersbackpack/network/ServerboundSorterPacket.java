package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundSorterPacket implements IPacket<ServerboundSorterPacket> {
    private final byte screenID;
    private final byte button;
    private final boolean shiftPressed;

    public ServerboundSorterPacket(byte screenID, byte button, boolean shiftPressed) {
        this.screenID = screenID;
        this.button = button;
        this.shiftPressed = shiftPressed;
    }

    public static ServerboundSorterPacket decode(final FriendlyByteBuf buffer) {
        final byte screenID = buffer.readByte();
        final byte button = buffer.readByte();
        final boolean shiftPressed = buffer.readBoolean();

        return new ServerboundSorterPacket(screenID, button, shiftPressed);
    }

    public void encode(final ServerboundSorterPacket message, final FriendlyByteBuf buffer) {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.button);
        buffer.writeBoolean(message.shiftPressed);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SORTER_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundSorterPacket message = decode(buf);
        server.execute(() -> {
            ServerActions.sortBackpack(player, message.screenID, message.button, message.shiftPressed);
        });
    }
}