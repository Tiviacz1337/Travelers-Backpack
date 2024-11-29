package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpackold.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpackold.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record SlotPacket(byte screenID, boolean isActive, List<Integer> selectedSlots) implements CustomPayload
{
    public static final CustomPayload.Id<SlotPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "slot"));
    public static final PacketCodec<RegistryByteBuf, SlotPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BYTE, SlotPacket::screenID, PacketCodecs.BOOL, SlotPacket::isActive, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), SlotPacket::selectedSlots, SlotPacket::new);

    public static void apply(SlotPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                if(message.screenID() == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = ComponentUtils.getBackpackInv(context.player()).getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive());
                    manager.setUnsortableSlots(message.selectedSlots(), true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive());
                }
                if(message.screenID() == Reference.ITEM_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackItemScreenHandler)context.player().currentScreenHandler).inventory.getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive());
                    manager.setUnsortableSlots(message.selectedSlots(), true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive());
                }
                if(message.screenID() == Reference.BLOCK_ENTITY_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackBlockEntityScreenHandler)context.player().currentScreenHandler).inventory.getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive());
                    manager.setUnsortableSlots(message.selectedSlots(), true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive());
                }
            }
        });
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}