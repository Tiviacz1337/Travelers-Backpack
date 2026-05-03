package com.tiviacz.travelersbackpack.compat.vinurl;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Method;

public record ServerboundVinURLStartPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ServerboundVinURLStartPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "start_vinurl"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundVinURLStartPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ServerboundVinURLStartPacket::stack,
                    ServerboundVinURLStartPacket::new
            );


    public static void handle(ServerboundVinURLStartPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if(TravelersBackpack.vinurlLoaded) {
                if(ctx.player().level() instanceof ServerLevel serverLevel) {
                    try {
                        Class<?> vinSoundClass = Class.forName("com.vinurl.api.VinURLSound");
                        Method playMethod = vinSoundClass.getMethod("playFor", net.minecraft.server.level.ServerLevel.class, net.minecraft.world.item.ItemStack.class, java.util.UUID.class);
                        playMethod.invoke(null, serverLevel, message.stack, ctx.player().getUUID());
                    } catch(Exception e) {
                        TravelersBackpack.LOGGER.error("Couldn't play the VinURL sound, report it to Traveler's Backpack Developer!", e);
                    }
                }
                //VinURLSound.playFor(ctx.player().level(), message.stack, ctx.player().getUUID());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}