package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SleepingBagPacket
{
    private final BlockPos pos;

    public SleepingBagPacket(BlockPos pos)
    {
        this.pos = pos;
    }

    public static SleepingBagPacket decode(final PacketBuffer buffer)
    {
        final BlockPos pos = buffer.readBlockPos();

        return new SleepingBagPacket(pos);
    }

    public static void encode(final SleepingBagPacket message, final PacketBuffer buffer)
    {
        buffer.writeBlockPos(message.pos);
    }

    public static void handle(final SleepingBagPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                ServerActions.toggleSleepingBag(serverPlayerEntity, message.pos);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}