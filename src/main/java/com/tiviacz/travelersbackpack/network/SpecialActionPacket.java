package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SpecialActionPacket
{
    private final double scrollDelta;
    private final int typeOfAction;

    public SpecialActionPacket(double scrollDelta, int typeOfAction)
    {
        this.scrollDelta = scrollDelta;
        this.typeOfAction = typeOfAction;
    }

    public static SpecialActionPacket decode(final PacketBuffer buffer)
    {
        final double scrollDelta = buffer.readDouble();
        final int typeOfAction = buffer.readInt();

        return new SpecialActionPacket(scrollDelta, typeOfAction);
    }

    public static void encode(final SpecialActionPacket message, final PacketBuffer buffer)
    {
        buffer.writeDouble(message.scrollDelta);
        buffer.writeInt(message.typeOfAction);
    }

    public static void handle(final SpecialActionPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                {
                    if(message.typeOfAction == Reference.SWAP_TOOL)
                    {
                        ServerActions.swapTool(serverPlayerEntity, message.scrollDelta);
                    }

                    else if(message.typeOfAction == Reference.SWITCH_HOSE_MODE)
                    {
                        ServerActions.switchHoseMode(serverPlayerEntity, message.scrollDelta);
                    }

                    else if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK)
                    {
                        ServerActions.toggleHoseTank(serverPlayerEntity);
                    }

                    else if(message.typeOfAction == Reference.EMPTY_TANK)
                    {
                        ServerActions.emptyTank(message.scrollDelta, serverPlayerEntity, serverPlayerEntity.level);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
