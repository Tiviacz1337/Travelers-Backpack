package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SSpecialActionPacket
{
    private final byte screenID;
    private final byte typeOfAction;
    private final double scrollDelta;

    public SSpecialActionPacket(byte screenID, byte typeOfAction, double scrollDelta)
    {
        this.screenID = screenID;
        this.typeOfAction = typeOfAction;
        this.scrollDelta = scrollDelta;
    }

    public static SSpecialActionPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final byte typeOfAction = buffer.readByte();
        final double scrollDelta = buffer.readDouble();

        return new SSpecialActionPacket(screenID, typeOfAction, scrollDelta);
    }

    public static void encode(final SSpecialActionPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.typeOfAction);
        buffer.writeDouble(message.scrollDelta);
    }

    public static void handle(final SSpecialActionPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
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
                    ServerActions.emptyTank(message.scrollDelta, serverPlayerEntity, serverPlayerEntity.level, message.screenID);
                }

                else if(message.typeOfAction == Reference.OPEN_SCREEN)
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