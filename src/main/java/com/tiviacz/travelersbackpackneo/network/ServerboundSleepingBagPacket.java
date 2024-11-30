package com.tiviacz.travelersbackpackneo.network;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSleepingBagPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ServerboundSleepingBagPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sleeping_bag"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundSleepingBagPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundSleepingBagPacket::pos,
            ServerboundSleepingBagPacket::new
    );

    public static void handle(final ServerboundSleepingBagPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer) {
                ServerActions.toggleSleepingBag(serverPlayer, message.pos);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}