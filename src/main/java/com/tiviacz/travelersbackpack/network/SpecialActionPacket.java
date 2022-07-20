package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

    public static SpecialActionPacket decode(final FriendlyByteBuf buffer)
    {
        final double scrollDelta = buffer.readDouble();
        final int typeOfAction = buffer.readInt();

        return new SpecialActionPacket(scrollDelta, typeOfAction);
    }

    public static void encode(final SpecialActionPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeDouble(message.scrollDelta);
        buffer.writeInt(message.typeOfAction);
    }

    public static void handle(final SpecialActionPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                if(CapabilityUtils.isWearingBackpack(serverPlayer))
                {
                    if(message.typeOfAction == Reference.SWAP_TOOL)
                    {
                        ServerActions.swapTool(serverPlayer, message.scrollDelta);
                    }

                    else if(message.typeOfAction == Reference.SWITCH_HOSE_MODE)
                    {
                        ServerActions.switchHoseMode(serverPlayer, message.scrollDelta);
                    }

                    else if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK)
                    {
                        ServerActions.toggleHoseTank(serverPlayer);
                    }

                    else if(message.typeOfAction == Reference.EMPTY_TANK)
                    {
                        ServerActions.emptyTank(message.scrollDelta, serverPlayer, serverPlayer.getLevel());
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}