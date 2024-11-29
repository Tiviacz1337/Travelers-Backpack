package com.tiviacz.travelersbackpackneo.network;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpackneo.common.ServerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSorterPacket(byte screenID, byte button, boolean shiftPressed) implements CustomPacketPayload {
    public static final Type<ServerboundSorterPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sorter"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundSorterPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ServerboundSorterPacket::screenID,
            ByteBufCodecs.BYTE, ServerboundSorterPacket::button,
            ByteBufCodecs.BOOL, ServerboundSorterPacket::shiftPressed,
            ServerboundSorterPacket::new
    );

    public static void handle(final ServerboundSorterPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer) {
                ServerActions.sortBackpack(serverPlayer, message.screenID, message.button, message.shiftPressed);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}