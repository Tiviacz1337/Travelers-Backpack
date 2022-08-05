package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSpecialActionPacket
{
    private final byte screenID;
    private final byte typeOfAction;
    private final double scrollDelta;

    public ServerboundSpecialActionPacket(byte screenID, byte typeOfAction, double scrollDelta)
    {
        this.screenID = screenID;
        this.typeOfAction = typeOfAction;
        this.scrollDelta = scrollDelta;
    }

    public static ServerboundSpecialActionPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final byte typeOfAction = buffer.readByte();
        final double scrollDelta = buffer.readDouble();

        return new ServerboundSpecialActionPacket(screenID, typeOfAction, scrollDelta);
    }

    public static void encode(final ServerboundSpecialActionPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.typeOfAction);
        buffer.writeDouble(message.scrollDelta);
    }

    public static void handle(final ServerboundSpecialActionPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
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
                    ServerActions.emptyTank(message.scrollDelta, serverPlayer, serverPlayer.getLevel(), message.screenID);
                }

                else if(message.typeOfAction == Reference.OPEN_SCREEN)
                {
                    if(CapabilityUtils.isWearingBackpack(serverPlayer))
                    {
                        TravelersBackpackContainer.openGUI(serverPlayer, CapabilityUtils.getWearingBackpack(serverPlayer), Reference.WEARABLE_SCREEN_ID);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}