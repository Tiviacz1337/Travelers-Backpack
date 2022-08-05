package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSorterPacket
{
    private final byte screenID;
    private final byte button;
    private final boolean shiftPressed;

    public ServerboundSorterPacket(byte screenID, byte button, boolean shiftPressed)
    {
        this.screenID = screenID;
        this.button = button;
        this.shiftPressed = shiftPressed;
    }

    public static ServerboundSorterPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final byte button = buffer.readByte();
        final boolean shiftPressed = buffer.readBoolean();

        return new ServerboundSorterPacket(screenID, button, shiftPressed);
    }

    public static void encode(final ServerboundSorterPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.button);
        buffer.writeBoolean(message.shiftPressed);
    }

    public static void handle(final ServerboundSorterPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                ServerActions.sortBackpack(serverPlayer, message.screenID, message.button, message.shiftPressed);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}