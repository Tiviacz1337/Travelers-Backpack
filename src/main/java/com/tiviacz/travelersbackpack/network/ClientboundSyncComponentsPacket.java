package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSyncComponentsPacket(int entityID, DataComponentMap map) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_components");
    public static final Type<ClientboundSyncComponentsPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncComponentsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncComponentsPacket::entityID,
            ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC), ClientboundSyncComponentsPacket::map,
            ClientboundSyncComponentsPacket::new
    );

    public static void handle(ClientboundSyncComponentsPacket message, IPayloadContext ctx) {
        if(ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                Player player = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
                AttachmentUtils.getAttachment(player).ifPresent(data -> data.applyComponents(message.map()));
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
