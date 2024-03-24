package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record ServerboundSpecialActionPacket(byte screenID, byte typeOfAction, double scrollDelta) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "special_action");

    public ServerboundSpecialActionPacket(final FriendlyByteBuf buffer)
    {
        this(buffer.readByte(), buffer.readByte(), buffer.readDouble());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeByte(screenID);
        pBuffer.writeByte(typeOfAction);
        pBuffer.writeDouble(scrollDelta);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ServerboundSpecialActionPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
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
                    ServerActions.emptyTank(message.scrollDelta, serverPlayer, serverPlayer.serverLevel(), message.screenID);
                }

                else if(message.typeOfAction == Reference.OPEN_SCREEN)
                {
                    if(AttachmentUtils.isWearingBackpack(serverPlayer))
                    {
                        TravelersBackpackContainer.openGUI(serverPlayer, AttachmentUtils.getWearingBackpack(serverPlayer), Reference.WEARABLE_SCREEN_ID);
                    }
                }
            }
        });
    }
}