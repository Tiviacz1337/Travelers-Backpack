package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSyncAttachmentPacket(int entityID, boolean isPlayer, ItemStack stack) implements CustomPacketPayload
{
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_attachment");
    public static final Type<ClientboundSyncAttachmentPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncAttachmentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncAttachmentPacket::entityID,
            ByteBufCodecs.BOOL, ClientboundSyncAttachmentPacket::isPlayer,
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSyncAttachmentPacket::stack,
            ClientboundSyncAttachmentPacket::new
    );

    public static void handle(final ClientboundSyncAttachmentPacket message, IPayloadContext ctx)
    {
        if(ctx.flow().isClientbound())
        {
            ctx.enqueueWork(() -> {
                if(message.isPlayer)
                {
                    final Player playerEntity = (Player) Minecraft.getInstance().player.level().getEntity(message.entityID);
                    ITravelersBackpack data = AttachmentUtils.getAttachment(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));

                    if(data != null)
                    {
                        data.setWearable(message.stack());
                        data.setContents(message.stack());
                    }
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}