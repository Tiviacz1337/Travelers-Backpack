package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record ServerboundEquipBackpackPacket(boolean equip) implements CustomPacketPayload {
    public static final Type<ServerboundEquipBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "equip_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEquipBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundEquipBackpackPacket::equip,
            ServerboundEquipBackpackPacket::new
    );

    public static void handle(final ServerboundEquipBackpackPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if (player instanceof ServerPlayer serverPlayer) {
                if (message.equip()) {
                    ServerActions.equipBackpack(serverPlayer);
                } else {
                    ServerActions.unequipBackpack(serverPlayer);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}