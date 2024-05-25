package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ClientboundSendMessagePacket(boolean drop, BlockPos pos) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "send_message");

    public ClientboundSendMessagePacket(FriendlyByteBuf friendlyByteBuf)
    {
        this(friendlyByteBuf.readBoolean(), friendlyByteBuf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf)
    {
        friendlyByteBuf.writeBoolean(drop);
        friendlyByteBuf.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ClientboundSendMessagePacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            if(TravelersBackpackConfig.CLIENT.sendBackpackCoordinatesMessage.get())
            {
                if(Minecraft.getInstance().player != null)
                {
                    Minecraft.getInstance().player.sendSystemMessage(Component.translatable(message.drop ? "information.travelersbackpack.backpack_drop" : "information.travelersbackpack.backpack_coords", message.pos().getX(), message.pos().getY(), message.pos().getZ()));
                }
            }
        });
    }
}