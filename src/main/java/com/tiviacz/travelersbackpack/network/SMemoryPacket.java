package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SMemoryPacket
{
    private final byte screenID;
    private final boolean isActive;
    private final int[] selectedSlots;
    private final ItemStack[] stacks;

    public SMemoryPacket(byte screenID, boolean isActive, int[] selectedSlots, ItemStack[] stacks)
    {
        this.screenID = screenID;
        this.isActive = isActive;
        this.selectedSlots = selectedSlots;
        this.stacks = stacks;
    }

    public static SMemoryPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean isActive = buffer.readBoolean();
        final int[] selectedSlots = buffer.readVarIntArray();
        final ItemStack[] stacks = new ItemStack[selectedSlots.length];

        for(int i = 0; i < selectedSlots.length; i++)
        {
            stacks[i] = buffer.readItem();
        }

        return new SMemoryPacket(screenID, isActive, selectedSlots, stacks);
    }

    public static void encode(final SMemoryPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.isActive);
        buffer.writeVarIntArray(message.selectedSlots);

        for(int i = 0; i < message.selectedSlots.length; i++)
        {
            buffer.writeItem(message.stacks[i]);
        }
    }

    public static void handle(final SMemoryPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = CapabilityUtils.getBackpackInv(serverPlayer).getSlotManager();
                    manager.setSelectorActive(SlotManager.MEMORY, message.isActive);
                    manager.setMemorySlots(message.selectedSlots, message.stacks, true);
                    manager.setSelectorActive(SlotManager.MEMORY, !message.isActive);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackItemContainer)serverPlayer.containerMenu).inventory.getSlotManager();
                    manager.setSelectorActive(SlotManager.MEMORY, message.isActive);
                    manager.setMemorySlots(message.selectedSlots, message.stacks, true);
                    manager.setSelectorActive(SlotManager.MEMORY, !message.isActive);
                }
                if(message.screenID == Reference.TILE_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackTileContainer)serverPlayer.containerMenu).inventory.getSlotManager();
                    manager.setSelectorActive(SlotManager.MEMORY, message.isActive);
                    manager.setMemorySlots(message.selectedSlots, message.stacks, true);
                    manager.setSelectorActive(SlotManager.MEMORY, !message.isActive);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
