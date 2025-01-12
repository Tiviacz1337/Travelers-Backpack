package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record ServerboundSlotPacket(byte selectType, Slots slotsData) implements CustomPacketPayload {
    public static final Type<ServerboundSlotPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSlotPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ServerboundSlotPacket::selectType,
            Slots.STREAM_CODEC, ServerboundSlotPacket::slotsData,
            ServerboundSlotPacket::new
    );

    public static final byte UNSORTABLES = (byte)0;
    public static final byte MEMORY = (byte)1;

    public static void handle(final ServerboundSlotPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackSettingsMenu menu) {
                if(message.selectType() == UNSORTABLES) {
                    //#TODO send only indexes and for memory only indexes and boolean for each, then get ItemStack from slot
                    menu.getWrapper().setUnsortableSlots(message.slotsData().unsortables());
                }
                if(message.selectType() == MEMORY) {
                    menu.getWrapper().setMemorySlots(message.slotsData().memory());
                }

                //Update backpack data on clients
                menu.getWrapper().sendDataToClients(ModDataComponents.SLOTS);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
