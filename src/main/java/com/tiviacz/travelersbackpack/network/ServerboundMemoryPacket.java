package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundMemoryPacket
{
    private final byte screenID;
    private final boolean isActive;
    private final int[] selectedSlots;
    private final ItemStack[] stacks;

    public ServerboundMemoryPacket(byte screenID, boolean isActive, int[] selectedSlots, ItemStack[] stacks)
    {
        this.screenID = screenID;
        this.isActive = isActive;
        this.selectedSlots = selectedSlots;
        this.stacks = stacks;
    }

    public static ServerboundMemoryPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean isActive = buffer.readBoolean();
        final int[] selectedSlots = buffer.readVarIntArray();
        final ItemStack[] stacks = new ItemStack[selectedSlots.length];

        for(int i = 0; i < selectedSlots.length; i++)
        {
            stacks[i] = buffer.readItem();
        }

        return new ServerboundMemoryPacket(screenID, isActive, selectedSlots, stacks);
    }

    public static void encode(final ServerboundMemoryPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.isActive);
        buffer.writeVarIntArray(message.selectedSlots);

        for(int i = 0; i < message.selectedSlots.length; i++)
        {
            buffer.writeItem(message.stacks[i]);
        }
    }

    public static void handle(final ServerboundMemoryPacket message, final CustomPayloadEvent.Context ctx)
    {
        ctx.enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.getSender();

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
                    SlotManager manager = ((TravelersBackpackItemMenu)serverPlayer.containerMenu).container.getSlotManager();
                    manager.setSelectorActive(SlotManager.MEMORY, message.isActive);
                    manager.setMemorySlots(message.selectedSlots, message.stacks, true);
                    manager.setSelectorActive(SlotManager.MEMORY, !message.isActive);
                }
                if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackBlockEntityMenu)serverPlayer.containerMenu).container.getSlotManager();
                    manager.setSelectorActive(SlotManager.MEMORY, message.isActive);
                    manager.setMemorySlots(message.selectedSlots, message.stacks, true);
                    manager.setSelectorActive(SlotManager.MEMORY, !message.isActive);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}