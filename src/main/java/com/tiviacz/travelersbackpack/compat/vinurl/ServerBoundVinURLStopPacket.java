package com.tiviacz.travelersbackpack.compat.vinurl;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.vinurl.api.VinURLSound;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.tiviacz.travelersbackpack.TravelersBackpack.vinurlLoaded;

public record ServerBoundVinURLStopPacket(ItemStack stack, boolean cancelable) implements CustomPacketPayload {
	public static final Type<ServerBoundVinURLStopPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "stop_vinurl"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundVinURLStopPacket> STREAM_CODEC =
		StreamCodec.composite(
			ItemStack.STREAM_CODEC, ServerBoundVinURLStopPacket::stack,
			ByteBufCodecs.BOOL, ServerBoundVinURLStopPacket::cancelable,
			ServerBoundVinURLStopPacket::new
		);


	public static void handle(ServerBoundVinURLStopPacket message, ServerPlayNetworking.Context ctx) {
		ctx.player().getServer().execute(() -> {
			if (vinurlLoaded) {
				VinURLSound.stopFor(ctx.player().serverLevel(), message.stack, ctx.player().getUUID(), message.cancelable);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}