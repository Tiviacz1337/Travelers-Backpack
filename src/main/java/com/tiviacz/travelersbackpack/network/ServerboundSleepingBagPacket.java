package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record ServerboundSleepingBagPacket(BlockPos pos, boolean isEquipped) implements CustomPacketPayload {
    public static final Type<ServerboundSleepingBagPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sleeping_bag"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundSleepingBagPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundSleepingBagPacket::pos,
            ByteBufCodecs.BOOL, ServerboundSleepingBagPacket::isEquipped,
            ServerboundSleepingBagPacket::new
    );

    public static void handle(final ServerboundSleepingBagPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer) {
                ServerActions.toggleSleepingBag(serverPlayer, message.pos, message.isEquipped);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}