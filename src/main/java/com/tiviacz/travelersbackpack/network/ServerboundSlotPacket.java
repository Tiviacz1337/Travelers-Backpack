package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundSlotPacket
{
    private final byte screenID;
    private final boolean isActive;
    private final int[] selectedSlots;

    public ServerboundSlotPacket(byte screenID, boolean isActive, int[] selectedSlots)
    {
        this.screenID = screenID;
        this.isActive = isActive;
        this.selectedSlots = selectedSlots;
    }

    public static ServerboundSlotPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean isActive = buffer.readBoolean();
        final int[] selectedSlots = buffer.readVarIntArray();

        return new ServerboundSlotPacket(screenID, isActive, selectedSlots);
    }

    public static void encode(final ServerboundSlotPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.isActive);
        buffer.writeVarIntArray(message.selectedSlots);
    }

    public static void handle(final ServerboundSlotPacket message, final CustomPayloadEvent.Context ctx)
    {
        ctx.enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.getSender();

            if(serverPlayer != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = CapabilityUtils.getBackpackInv(serverPlayer).getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackItemMenu)serverPlayer.containerMenu).container.getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive);
                }
                if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackBlockEntityMenu)serverPlayer.containerMenu).container.getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}