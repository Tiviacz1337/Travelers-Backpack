package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.common.ServerActions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SleepingBagPacket(BlockPos pos) implements CustomPayload
{
    public static final CustomPayload.Id<SleepingBagPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "sleeping_bag"));
    public static final PacketCodec<RegistryByteBuf, SleepingBagPacket> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SleepingBagPacket::pos, SleepingBagPacket::new);

    public static void apply(SleepingBagPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() -> ServerActions.toggleSleepingBag(context.player(), message.pos()));
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}