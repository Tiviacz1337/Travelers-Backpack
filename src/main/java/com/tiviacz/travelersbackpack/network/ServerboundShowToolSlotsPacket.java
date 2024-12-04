package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record ServerboundShowToolSlotsPacket(boolean show) implements CustomPacketPayload {
    public static final Type<ServerboundShowToolSlotsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "show_tool_slots"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundShowToolSlotsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundShowToolSlotsPacket::show,
            ServerboundShowToolSlotsPacket::new
    );

    public static void handle(final ServerboundShowToolSlotsPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                menu.getWrapper().setShowToolSlots(message.show());
                menu.getWrapper().requestMenuUpdate(false);

                //Update backpack data on clients
                menu.getWrapper().sendDataToClients(ModDataComponents.SHOW_TOOL_SLOTS);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}