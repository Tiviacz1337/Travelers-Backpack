package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SlotPacket
{
    private final byte screenID;
    private final boolean isActive;
    private final int[] selectedSlots;

    public SlotPacket(byte screenID, boolean isActive, int[] selectedSlots)
    {
        this.screenID = screenID;
        this.isActive = isActive;
        this.selectedSlots = selectedSlots;
    }

    public static SlotPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean isActive = buffer.readBoolean();
        final int[] selectedSlots = buffer.readVarIntArray();

        return new SlotPacket(screenID, isActive, selectedSlots);
    }

    public static void encode(final SlotPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.isActive);
        buffer.writeVarIntArray(message.selectedSlots);
    }

    public static void handle(final SlotPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = CapabilityUtils.getBackpackInv(serverPlayer).getSlotManager();
                    manager.setActive(message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setActive(!message.isActive);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackItemMenu)serverPlayer.containerMenu).container.getSlotManager();
                    manager.setActive(message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setActive(!message.isActive);
                }
                if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackBlockEntityMenu)serverPlayer.containerMenu).container.getSlotManager();
                    manager.setActive(message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setActive(!message.isActive);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}