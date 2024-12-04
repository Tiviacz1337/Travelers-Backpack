package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundSendMessagePacket(boolean drop, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "send_message");
    public static final Type<ClientboundSendMessagePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSendMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientboundSendMessagePacket::drop,
            BlockPos.STREAM_CODEC, ClientboundSendMessagePacket::pos,
            ClientboundSendMessagePacket::new
    );

    public static void handle(final ClientboundSendMessagePacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            if (TravelersBackpackConfig.getConfig().client.sendBackpackCoordinatesMessage) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.translatable(message.drop ? "information.travelersbackpack.backpack_drop" : "information.travelersbackpack.backpack_coords", message.pos().getX(), message.pos().getY(), message.pos().getZ()));
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}