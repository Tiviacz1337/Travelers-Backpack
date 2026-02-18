package com.tiviacz.travelersbackpack.compat.vinurl;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.vinurl.api.VinURLSound;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.tiviacz.travelersbackpack.TravelersBackpack.vinurlLoaded;

public record ServerBoundVinURLStartPacket(ItemStack stack) implements CustomPacketPayload {
	public static final Type<ServerBoundVinURLStartPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "start_vinurl"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundVinURLStartPacket> STREAM_CODEC =
		StreamCodec.composite(
			ItemStack.STREAM_CODEC, ServerBoundVinURLStartPacket::stack,
			ServerBoundVinURLStartPacket::new
		);


	public static void handle(ServerBoundVinURLStartPacket message, ServerPlayNetworking.Context ctx) {
		ctx.player().getServer().execute(() -> {
			if (vinurlLoaded) {
				VinURLSound.playFor(ctx.player().serverLevel(), message.stack, ctx.player().getUUID());
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}