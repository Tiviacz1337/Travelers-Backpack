package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSyncComponentsPacket(int entityID, DataComponentMap map) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_components");
    public static final Type<ClientboundSyncComponentsPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncComponentsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncComponentsPacket::entityID,
            ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC), ClientboundSyncComponentsPacket::map,
            ClientboundSyncComponentsPacket::new
    );

    public static void handle(final ClientboundSyncComponentsPacket message, IPayloadContext ctx) {
        if(ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                final Player playerEntity = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
                ITravelersBackpack data = AttachmentUtils.getAttachment(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));
                if(data != null) {
                    data.applyComponents(message.map());
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
