package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSendMessagePacket(boolean drop, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "send_message");
    public static final Type<ClientboundSendMessagePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSendMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientboundSendMessagePacket::drop,
            BlockPos.STREAM_CODEC, ClientboundSendMessagePacket::pos,
            ClientboundSendMessagePacket::new
    );

    public static void handle(final ClientboundSendMessagePacket message, IPayloadContext ctx) {
        if(ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                if(TravelersBackpackConfig.CLIENT.sendBackpackCoordinatesMessage.get()) {
                    if(Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.translatable(message.drop ? "information.travelersbackpack.backpack_drop" : "information.travelersbackpack.backpack_coords", message.pos().getX(), message.pos().getY(), message.pos().getZ()));
                    }
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}