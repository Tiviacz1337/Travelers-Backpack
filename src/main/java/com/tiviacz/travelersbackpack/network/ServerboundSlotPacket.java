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
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record ServerboundSlotPacket(byte screenID, boolean isActive, int[] selectedSlots) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "slot");

    public ServerboundSlotPacket(final FriendlyByteBuf buffer)
    {
        this(buffer.readByte(), buffer.readBoolean(), buffer.readVarIntArray());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeByte(screenID);
        pBuffer.writeBoolean(isActive);
        pBuffer.writeVarIntArray(selectedSlots);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final ServerboundSlotPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() -> {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = AttachmentUtils.getBackpackInv(serverPlayer).getSlotManager();
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
    }
}