package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundEquipBackpackPacket(boolean equip) implements CustomPacketPayload {
    public static final Type<ServerboundEquipBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "equip_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEquipBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundEquipBackpackPacket::equip,
            ServerboundEquipBackpackPacket::new
    );

    public static void handle(final ServerboundEquipBackpackPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer) {
                if(message.equip()) {
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