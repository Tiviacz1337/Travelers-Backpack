package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenPacket
{
    private final byte type;

    public ScreenPacket(byte type)
    {
        this.type = type;
    }

    public static ScreenPacket decode(final FriendlyByteBuf buffer)
    {
        final byte type = buffer.readByte();

        return new ScreenPacket(type);
    }

    public static void encode(final ScreenPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.type);
    }

    public static void handle(final ScreenPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.type == Reference.BACKPACK_SCREEN)
                {
                    if(CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                    {
                        TravelersBackpackContainer.openGUI(serverPlayerEntity, CapabilityUtils.getWearingBackpack(serverPlayerEntity), Reference.WEARABLE_SCREEN_ID);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}