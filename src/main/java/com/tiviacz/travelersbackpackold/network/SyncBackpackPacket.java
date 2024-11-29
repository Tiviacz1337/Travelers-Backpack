package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncBackpackPacket(int entityID, ItemStack stack) implements CustomPayload
{
    public static final CustomPayload.Id<SyncBackpackPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "sync_backpack"));
    public static final PacketCodec<RegistryByteBuf, SyncBackpackPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SyncBackpackPacket::entityID, ItemStack.OPTIONAL_PACKET_CODEC, SyncBackpackPacket::stack, SyncBackpackPacket::new);

    public static void apply(SyncBackpackPacket message, ClientPlayNetworking.Context context)
    {
        context.client().execute(() ->
        {
            if(context.client().world != null)
            {
                Entity entity = context.client().world.getEntityById(message.entityID());

                if(entity instanceof PlayerEntity player)
                {
                    ComponentUtils.getComponent(player).setWearable(message.stack());
                    ComponentUtils.getComponent(player).setContents(message.stack());
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