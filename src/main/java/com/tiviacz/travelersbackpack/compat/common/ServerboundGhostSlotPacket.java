package com.tiviacz.travelersbackpack.compat.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ServerboundGhostSlotPacket(ItemStack stack, int slotNumber) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "set_ghost_slot");
    public static final Type<ServerboundGhostSlotPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundGhostSlotPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ServerboundGhostSlotPacket::stack,
            ByteBufCodecs.INT, ServerboundGhostSlotPacket::slotNumber,
            ServerboundGhostSlotPacket::new
    );

    public static void handle(final ServerboundGhostSlotPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if(!(player.containerMenu instanceof BackpackBaseMenu)) {
                return;
            }
            player.containerMenu.getSlot(message.slotNumber).set(message.stack);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}