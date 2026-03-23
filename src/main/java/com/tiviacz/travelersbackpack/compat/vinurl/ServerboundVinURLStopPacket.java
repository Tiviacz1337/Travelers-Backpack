package com.tiviacz.travelersbackpack.compat.vinurl;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Method;

public record ServerboundVinURLStopPacket(ItemStack stack, boolean cancelable) implements CustomPacketPayload {
    public static final Type<ServerboundVinURLStopPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "stop_vinurl"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundVinURLStopPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ServerboundVinURLStopPacket::stack,
                    ByteBufCodecs.BOOL, ServerboundVinURLStopPacket::cancelable,
                    ServerboundVinURLStopPacket::new
            );


    public static void handle(ServerboundVinURLStopPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if(TravelersBackpack.vinurlLoaded) {
                if(ctx.player().level() instanceof ServerLevel serverLevel) {
                    try {
                        Class<?> vinSoundClass = Class.forName("com.vinurl.api.VinURLSound");
                        Method stopMethod = vinSoundClass.getMethod("stopFor", net.minecraft.server.level.ServerLevel.class, net.minecraft.world.item.ItemStack.class, java.util.UUID.class, boolean.class);
                        stopMethod.invoke(null, serverLevel, message.stack, ctx.player().getUUID(), message.cancelable);
                    } catch (Exception e) {
                        TravelersBackpack.LOGGER.error("Couldn't stop the VinURL sound, report it to Traveler's Backpack Developer!", e);
                    }
                }
                //VinURLSound.stopFor(ctx.player().level(), message.stack, ctx.player().getUUID(), message.cancelable);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}