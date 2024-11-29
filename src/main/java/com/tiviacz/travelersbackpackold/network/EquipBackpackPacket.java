package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.common.ServerActions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EquipBackpackPacket(boolean equip) implements CustomPayload
{
    public static final CustomPayload.Id<EquipBackpackPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "equip_backpack"));
    public static final PacketCodec<RegistryByteBuf, EquipBackpackPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOL, EquipBackpackPacket::equip, EquipBackpackPacket::new);

    public static void apply(EquipBackpackPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                if(message.equip())
                {
                    ServerActions.equipBackpack(context.player());
                }
                else
                {
                    ServerActions.unequipBackpack(context.player());
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