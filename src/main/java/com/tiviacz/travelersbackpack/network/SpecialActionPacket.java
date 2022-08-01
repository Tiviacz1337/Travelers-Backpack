package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SpecialActionPacket
{
    private final double scrollDelta;
    private final int typeOfAction;
    private final byte screenID;
    private final BlockPos pos;

    public SpecialActionPacket(double scrollDelta, int typeOfAction, byte screenID, BlockPos pos)
    {
        this.scrollDelta = scrollDelta;
        this.typeOfAction = typeOfAction;
        this.screenID = screenID;

        this.pos = pos;
    }

    public static SpecialActionPacket decode(final PacketBuffer buffer)
    {
        final double scrollDelta = buffer.readDouble();
        final int typeOfAction = buffer.readInt();
        final byte screenID = buffer.readByte();
        BlockPos pos = null;

        if(buffer.writerIndex() == 22) pos = buffer.readBlockPos();

        return new SpecialActionPacket(scrollDelta, typeOfAction, screenID, pos);
    }

    public static void encode(final SpecialActionPacket message, final PacketBuffer buffer)
    {
        buffer.writeDouble(message.scrollDelta);
        buffer.writeInt(message.typeOfAction);
        buffer.writeByte(message.screenID);

        if(message.screenID == Reference.TILE_SCREEN_ID) buffer.writeBlockPos(message.pos);
    }

    public static void handle(final SpecialActionPacket message, final Supplier<NetworkEvent.Context> ctx)
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
                    ServerActions.emptyTank(message.scrollDelta, serverPlayerEntity, serverPlayerEntity.level, message.screenID, message.pos);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}