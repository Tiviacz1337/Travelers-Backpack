package com.tiviacz.travelersbackpack.compat.vinurl;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.vinurl.api.VinURLSound;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public record ServerboundVinURLStartPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ServerboundVinURLStartPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "start_vinurl"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundVinURLStartPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ServerboundVinURLStartPacket::stack,
                    ServerboundVinURLStartPacket::new
            );


    public static void handle(ServerboundVinURLStartPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            if(TravelersBackpack.vinurlLoaded) {
                if(ctx.player().level() instanceof ServerLevel serverLevel) {
                    VinURLSound.playFor(serverLevel, message.stack, ctx.player().getUUID());
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}