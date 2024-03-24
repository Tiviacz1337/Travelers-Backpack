package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record ServerboundSorterPacket(byte screenID, byte button, boolean shiftPressed) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "sorter");

    public ServerboundSorterPacket(final FriendlyByteBuf buffer)
    {
        this(buffer.readByte(), buffer.readByte(), buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeByte(screenID);
        pBuffer.writeByte(button);
        pBuffer.writeBoolean(shiftPressed);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ServerboundSorterPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
            {
                ServerActions.sortBackpack(serverPlayer, message.screenID, message.button, message.shiftPressed);
            }
        });
    }
}