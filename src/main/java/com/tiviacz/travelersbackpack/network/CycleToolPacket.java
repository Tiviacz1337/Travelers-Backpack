package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleToolPacket
{
    private final double scrollDelta;
    private final int typeOfAction;

    public CycleToolPacket(double scrollDelta, int typeOfAction)
    {
        this.scrollDelta = scrollDelta;
        this.typeOfAction = typeOfAction;
    }

    public static CycleToolPacket decode(final PacketBuffer buffer)
    {
        final double scrollDelta = buffer.readDouble();
        final int typeOfAction = buffer.readInt();

        return new CycleToolPacket(scrollDelta, typeOfAction);
    }

    public static void encode(final CycleToolPacket message, final PacketBuffer buffer)
    {
        buffer.writeDouble(message.scrollDelta);
        buffer.writeInt(message.typeOfAction);
    }

    public static void handle(final CycleToolPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                {
                    if(message.typeOfAction == Reference.CYCLE_TOOL_ACTION)
                    {
                        ServerActions.cycleTool(serverPlayerEntity, message.scrollDelta);
                    }

                    else if(message.typeOfAction == Reference.SWITCH_HOSE_ACTION)
                    {
                        ServerActions.switchHoseMode(serverPlayerEntity, message.scrollDelta);
                    }

                    else if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK)
                    {
                        ServerActions.toggleHoseTank(serverPlayerEntity);
                    }

                    else if(message.typeOfAction == Reference.EMPTY_TANK)
                    {
                        ServerActions.emptyTank(message.scrollDelta, serverPlayerEntity, serverPlayerEntity.world);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
