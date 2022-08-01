package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AbilitySliderPacket
{
    private final boolean sliderValue;
    @Nullable
    private final BlockPos blockPos;

    public AbilitySliderPacket(boolean sliderValue, @Nullable BlockPos blockPos)
    {
        this.sliderValue = sliderValue;
        this.blockPos = blockPos;
    }

    public static AbilitySliderPacket decode(final FriendlyByteBuf buffer)
    {
        final boolean sliderValue = buffer.readBoolean();
        BlockPos pos = null;

        if(buffer.writerIndex() == 10)
        {
            pos = buffer.readBlockPos();
        }

        return new AbilitySliderPacket(sliderValue, pos);
    }

    public static void encode(final AbilitySliderPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(message.sliderValue);
        if(message.blockPos != null)
        {
            buffer.writeBlockPos(message.blockPos);
        }
    }

    public static void handle(final AbilitySliderPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                if(message.blockPos == null && CapabilityUtils.isWearingBackpack(serverPlayer))
                {
                    ServerActions.switchAbilitySlider(serverPlayer, message.sliderValue);
                }
                else if(message.blockPos != null)
                {
                    ServerActions.switchAbilitySliderBlockEntity(serverPlayer, message.blockPos);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}