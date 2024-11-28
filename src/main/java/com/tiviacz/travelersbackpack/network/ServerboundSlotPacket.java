package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundSlotPacket {
    private final byte selectType;
    private final Slots slotsData;

    public ServerboundSlotPacket(byte selectType, Slots slotsData) {
        this.selectType = selectType;
        this.slotsData = slotsData;
    }

    public static ServerboundSlotPacket decode(final RegistryFriendlyByteBuf buffer) {
        final byte selectType = buffer.readByte();
        final Slots slotsData = Slots.STREAM_CODEC.decode(buffer);

        return new ServerboundSlotPacket(selectType, slotsData);
    }

    public static void encode(final ServerboundSlotPacket message, final RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(message.selectType);
        Slots.STREAM_CODEC.encode(buffer, message.slotsData);
    }

    public static final byte UNSORTABLES = (byte)0;
    public static final byte MEMORY = (byte)1;

    public static void handle(final ServerboundSlotPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackSettingsMenu menu) {
                if(message.selectType == UNSORTABLES) {
                    menu.getWrapper().setUnsortableSlots(message.slotsData.unsortables());
                }
                if(message.selectType == MEMORY) {
                    menu.getWrapper().setMemorySlots(message.slotsData.memory());
                }

                //Update backpack data on clients
                menu.getWrapper().sendDataToClients(ModDataComponents.SLOTS.get());
            }
        });

        ctx.setPacketHandled(true);
    }
}