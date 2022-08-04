package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSleepingBagPacket
{
    private final BlockPos pos;

    public ServerboundSleepingBagPacket(BlockPos pos)
    {
        this.pos = pos;
    }

    public static ServerboundSleepingBagPacket decode(final FriendlyByteBuf buffer)
    {
        final BlockPos pos = buffer.readBlockPos();

        return new ServerboundSleepingBagPacket(pos);
    }

    public static void encode(final ServerboundSleepingBagPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(message.pos);
    }

    public static void handle(final ServerboundSleepingBagPacket message, final Supplier<NetworkEvent.Context> ctx)
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