package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static AbilitySliderPacket decode(final PacketBuffer buffer)
    {
        final boolean sliderValue = buffer.readBoolean();
        BlockPos pos = null;

        if(buffer.writerIndex() == 10)
        {
            pos = buffer.readBlockPos();
        }

        return new AbilitySliderPacket(sliderValue, pos);
    }

    public static void encode(final AbilitySliderPacket message, final PacketBuffer buffer)
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
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.blockPos == null && CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                {
                    ServerActions.switchAbilitySlider(serverPlayerEntity, message.sliderValue);
                }
                else if(message.blockPos != null)
                {
                    ServerActions.switchAbilitySliderTileEntity(serverPlayerEntity, message.blockPos);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}