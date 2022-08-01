package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenPacket
{
    private final byte type;

    public ScreenPacket(byte type)
    {
        this.type = type;
    }

    public static ScreenPacket decode(final PacketBuffer buffer)
    {
        final byte type = buffer.readByte();

        return new ScreenPacket(type);
    }

    public static void encode(final ScreenPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.type);
    }

    public static void handle(final ScreenPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.type == Reference.BACKPACK_SCREEN)
                {
                    if(CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                    {
                        TravelersBackpackInventory.openGUI(serverPlayerEntity, CapabilityUtils.getWearingBackpack(serverPlayerEntity), Reference.WEARABLE_SCREEN_ID);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}