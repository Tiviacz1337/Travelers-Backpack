package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncItemStackPacket(int entityId, ItemStack stack) implements CustomPayload
{
    public static final CustomPayload.Id<SyncItemStackPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "sync_itemstack"));
    public static final PacketCodec<RegistryByteBuf, SyncItemStackPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SyncItemStackPacket::entityId, ItemStack.OPTIONAL_PACKET_CODEC, SyncItemStackPacket::stack, SyncItemStackPacket::new);

    public static void apply(SyncItemStackPacket message, ClientPlayNetworking.Context context)
    {
        context.client().execute(() ->
        {
            PlayerEntity player = (PlayerEntity)context.client().player.getWorld().getEntityById(message.entityId());

            if(player != null && player.getMainHandStack().getItem() == message.stack().getItem())
            {
                player.getMainHandStack().applyUnvalidatedChanges(message.stack().getComponentChanges());
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}