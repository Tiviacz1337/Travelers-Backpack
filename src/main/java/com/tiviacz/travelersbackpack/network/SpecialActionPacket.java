package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

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

    public static SpecialActionPacket decode(final FriendlyByteBuf buffer)
    {
        final double scrollDelta = buffer.readDouble();
        final int typeOfAction = buffer.readInt();
        final byte screenID = buffer.readByte();
        BlockPos pos = null;

        if(buffer.writerIndex() == 22) pos = buffer.readBlockPos();

        return new SpecialActionPacket(scrollDelta, typeOfAction, screenID, pos);
    }

    public static void encode(final SpecialActionPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeDouble(message.scrollDelta);
        buffer.writeInt(message.typeOfAction);
        buffer.writeByte(message.screenID);

        if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID) buffer.writeBlockPos(message.pos);
    }

    public static void handle(final SpecialActionPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
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
                    ServerActions.emptyTank(message.scrollDelta, serverPlayer, serverPlayer.getLevel(), message.screenID, message.pos);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}