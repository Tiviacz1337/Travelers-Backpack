package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.network.*;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ModNetwork
{
    public static void register(final IPayloadRegistrar registrar)
    {
        //Client
        registrar.play(ClientboundSyncAttachmentPacket.ID, ClientboundSyncAttachmentPacket::new, handler -> handler.client(ClientboundSyncAttachmentPacket::handle));
        registrar.play(ClientboundUpdateRecipePacket.ID, ClientboundUpdateRecipePacket::read, handler -> handler.client(ClientboundUpdateRecipePacket::handle));
        registrar.play(ClientboundSendMessagePacket.ID, ClientboundSendMessagePacket::new, handler -> handler.client(ClientboundSendMessagePacket::handle));

        //Server
        registrar.play(ServerboundAbilitySliderPacket.ID, ServerboundAbilitySliderPacket::new, handler -> handler.server(ServerboundAbilitySliderPacket::handle));
        registrar.play(ServerboundEquipBackpackPacket.ID, ServerboundEquipBackpackPacket::new, handler -> handler.server(ServerboundEquipBackpackPacket::handle));
        registrar.play(ServerboundMemoryPacket.ID, ServerboundMemoryPacket::read, handler -> handler.server(ServerboundMemoryPacket::handle));
        registrar.play(ServerboundSettingsPacket.ID, ServerboundSettingsPacket::new, handler -> handler.server(ServerboundSettingsPacket::handle));
        registrar.play(ServerboundSleepingBagPacket.ID, ServerboundSleepingBagPacket::new, handler -> handler.server(ServerboundSleepingBagPacket::handle));
        registrar.play(ServerboundSlotPacket.ID, ServerboundSlotPacket::new, handler -> handler.server(ServerboundSlotPacket::handle));
        registrar.play(ServerboundSorterPacket.ID, ServerboundSorterPacket::new, handler -> handler.server(ServerboundSorterPacket::handle));
        registrar.play(ServerboundSpecialActionPacket.ID, ServerboundSpecialActionPacket::new, handler -> handler.server(ServerboundSpecialActionPacket::handle));
    }
}