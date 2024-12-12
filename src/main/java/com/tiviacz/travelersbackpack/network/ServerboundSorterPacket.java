package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record ServerboundSorterPacket(byte screenID, byte button, boolean shiftPressed) implements CustomPacketPayload {
    public static final Type<ServerboundSorterPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sorter"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundSorterPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ServerboundSorterPacket::screenID,
            ByteBufCodecs.BYTE, ServerboundSorterPacket::button,
            ByteBufCodecs.BOOL, ServerboundSorterPacket::shiftPressed,
            ServerboundSorterPacket::new
    );

    public static void handle(final ServerboundSorterPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
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