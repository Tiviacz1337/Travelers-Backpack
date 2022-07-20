package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SleepingBagPacket
{
    private final BlockPos pos;

    public SleepingBagPacket(BlockPos pos)
    {
        this.pos = pos;
    }

    public static SleepingBagPacket decode(final FriendlyByteBuf buffer)
    {
        final BlockPos pos = buffer.readBlockPos();

        return new SleepingBagPacket(pos);
    }

    public static void encode(final SleepingBagPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(message.pos);
    }

    public static void handle(final SleepingBagPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                ServerActions.toggleSleepingBag(serverPlayer, message.pos);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}