package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.component.ITravelersBackpack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ClientboundSyncAttachmentPacket(int entityID, ItemStack backpack,
                                              boolean removeData) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_attachment");
    public static final Type<ClientboundSyncAttachmentPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncAttachmentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncAttachmentPacket::entityID,
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSyncAttachmentPacket::backpack,
            ByteBufCodecs.BOOL, ClientboundSyncAttachmentPacket::removeData,
            ClientboundSyncAttachmentPacket::new
    );

    public ClientboundSyncAttachmentPacket(int entityID, ItemStack serverBackpack) {
        this(entityID, serverBackpack, false);
    }

    public static void handle(final ClientboundSyncAttachmentPacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            final Player playerEntity = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
            ITravelersBackpack data = ComponentUtils.getComponent(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));

            if(data != null) {
                if(message.removeData()) {
                    data.remove();
                } else {
                    data.updateBackpack(message.backpack());
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}