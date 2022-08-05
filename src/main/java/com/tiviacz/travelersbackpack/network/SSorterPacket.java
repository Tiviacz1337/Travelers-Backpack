package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SSorterPacket
{
    private final byte screenID;
    private final byte button;
    private final boolean shiftPressed;

    public SSorterPacket(byte screenID, byte button, boolean shiftPressed)
    {
        this.screenID = screenID;
        this.button = button;
        this.shiftPressed = shiftPressed;
    }

    public static SSorterPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final byte button = buffer.readByte();
        final boolean shiftPressed = buffer.readBoolean();

        return new SSorterPacket(screenID, button, shiftPressed);
    }

    public static void encode(final SSorterPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.button);
        buffer.writeBoolean(message.shiftPressed);
    }

    public static void handle(final SSorterPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                ServerActions.sortBackpack(serverPlayerEntity, message.screenID, message.button, message.shiftPressed);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}