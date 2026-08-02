package com.tiviacz.travelersbackpack.compat.vinurl;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.vinurl.api.VinURLSound;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public record ServerboundVinURLStopPacket(ItemStack stack, boolean cancelable) implements CustomPacketPayload {
    public static final Type<ServerboundVinURLStopPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "stop_vinurl"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundVinURLStopPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ServerboundVinURLStopPacket::stack,
                    ByteBufCodecs.BOOL, ServerboundVinURLStopPacket::cancelable,
                    ServerboundVinURLStopPacket::new
            );


    public static void handle(ServerboundVinURLStopPacket message, ServerPlayNetworking.Context ctx) {
        ctx.server().execute(() -> {
            if(TravelersBackpack.vinurlLoaded) {
                if(ctx.player().level() instanceof ServerLevel serverLevel) {
                    VinURLSound.stopFor(serverLevel, message.stack, ctx.player().getId(), message.cancelable);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}