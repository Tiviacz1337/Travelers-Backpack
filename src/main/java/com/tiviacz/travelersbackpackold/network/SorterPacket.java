package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.common.ServerActions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SorterPacket(byte screenID, byte button, boolean shiftPressed) implements CustomPayload
{
    public static final CustomPayload.Id<SorterPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "sorter"));
    public static final PacketCodec<RegistryByteBuf, SorterPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BYTE, SorterPacket::screenID, PacketCodecs.BYTE, SorterPacket::button, PacketCodecs.BOOL, SorterPacket::shiftPressed, SorterPacket::new);

    public static void apply(SorterPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                ServerActions.sortBackpack(context.player(), message.screenID(), message.button(), message.shiftPressed());
            }
        });
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}