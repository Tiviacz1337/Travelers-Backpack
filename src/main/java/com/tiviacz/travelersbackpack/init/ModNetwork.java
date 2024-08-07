package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.network.*;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork
{
    public static void register(final PayloadRegistrar registrar)
    {
        //Client
        registrar.playToClient(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket.STREAM_CODEC, ClientboundSyncAttachmentPacket::handle);
        registrar.playToClient(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket.STREAM_CODEC, ClientboundUpdateRecipePacket::handle);
        registrar.playToClient(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket.STREAM_CODEC, ClientboundSendMessagePacket::handle);
        registrar.playToClient(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket.STREAM_CODEC, ClientboundSyncItemStackPacket::handle);

        //Server
        registrar.playToServer(ServerboundAbilitySliderPacket.TYPE, ServerboundAbilitySliderPacket.STREAM_CODEC, ServerboundAbilitySliderPacket::handle);
        registrar.playToServer(ServerboundEquipBackpackPacket.TYPE, ServerboundEquipBackpackPacket.STREAM_CODEC, ServerboundEquipBackpackPacket::handle);
        registrar.playToServer(ServerboundMemoryPacket.TYPE, ServerboundMemoryPacket.STREAM_CODEC, ServerboundMemoryPacket::handle);
        registrar.playToServer(ServerboundSettingsPacket.TYPE, ServerboundSettingsPacket.STREAM_CODEC, ServerboundSettingsPacket::handle);
        registrar.playToServer(ServerboundSleepingBagPacket.TYPE, ServerboundSleepingBagPacket.STREAM_CODEC, ServerboundSleepingBagPacket::handle);
        registrar.playToServer(ServerboundSlotPacket.TYPE, ServerboundSlotPacket.STREAM_CODEC, ServerboundSlotPacket::handle);
        registrar.playToServer(ServerboundSorterPacket.TYPE, ServerboundSorterPacket.STREAM_CODEC, ServerboundSorterPacket::handle);
        registrar.playToServer(ServerboundSpecialActionPacket.TYPE, ServerboundSpecialActionPacket.STREAM_CODEC, ServerboundSpecialActionPacket::handle);
    }
}