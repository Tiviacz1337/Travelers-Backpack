package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ServerboundMemoryPacket implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "memory");

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

    public static ServerboundMemoryPacket read(final FriendlyByteBuf buffer)
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

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeByte(screenID);
        pBuffer.writeBoolean(isActive);
        pBuffer.writeVarIntArray(selectedSlots);

        for(int i = 0; i < selectedSlots.length; i++)
        {
            pBuffer.writeItem(stacks[i]);
        }
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ServerboundMemoryPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = AttachmentUtils.getBackpackInv(serverPlayer).getSlotManager();
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
    }
}