package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static SlotPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean isActive = buffer.readBoolean();
        final int[] selectedSlots = buffer.readVarIntArray();

        return new SlotPacket(screenID, isActive, selectedSlots);
    }

    public static void encode(final SlotPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.isActive);
        buffer.writeVarIntArray(message.selectedSlots);
    }

    public static void handle(final SlotPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = CapabilityUtils.getBackpackInv(serverPlayerEntity).getSlotManager();
                    manager.setActive(message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setActive(!message.isActive);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackItemContainer)serverPlayerEntity.containerMenu).inventory.getSlotManager();
                    manager.setActive(message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setActive(!message.isActive);
                }
                if(message.screenID == Reference.TILE_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackTileContainer)serverPlayerEntity.containerMenu).inventory.getSlotManager();
                    manager.setActive(message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setActive(!message.isActive);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}