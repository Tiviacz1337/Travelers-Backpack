package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SendMessagePacket(boolean drop, BlockPos pos) implements CustomPayload
{
    public static final CustomPayload.Id<SendMessagePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "send_message"));
    public static final PacketCodec<RegistryByteBuf, SendMessagePacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SendMessagePacket::drop, BlockPos.PACKET_CODEC, SendMessagePacket::pos, SendMessagePacket::new);

    public static void apply(SendMessagePacket message, ClientPlayNetworking.Context context)
    {
        context.client().execute(() ->
        {
            if(TravelersBackpackConfig.getConfig().client.sendBackpackCoordinatesMessage)
            {
                if(MinecraftClient.getInstance().player != null)
                {
                    MinecraftClient.getInstance().player.sendMessage(Text.translatable(message.drop() ? "information.travelersbackpack.backpack_drop" : "information.travelersbackpack.backpack_coords", message.pos().getX(), message.pos().getY(), message.pos().getZ()));
                }
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}