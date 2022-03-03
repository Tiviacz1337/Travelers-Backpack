package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenPacket
{
    private final byte type;
    private final byte from;

    public ScreenPacket(byte type, byte from)
    {
        this.type = type;
        this.from = from;
    }

    public static ScreenPacket decode(final FriendlyByteBuf buffer)
    {
        final byte type = buffer.readByte();
        final byte from = buffer.readByte();

        return new ScreenPacket(type, from);
    }

    public static void encode(final ScreenPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.type);
        buffer.writeByte(message.from);
    }

    public static void handle(final ScreenPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.type == Reference.BACKPACK_GUI && message.from == Reference.FROM_KEYBIND)
                {
                    if(CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                    {
                        TravelersBackpackContainer.openGUI(serverPlayerEntity, CapabilityUtils.getWearingBackpack(serverPlayerEntity), Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

