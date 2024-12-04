package com.tiviacz.travelersbackpackneo.initold;

import com.tiviacz.travelersbackpack.network.*;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    public static void register(final PayloadRegistrar registrar) {
        //Client
        registrar.playToClient(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket.STREAM_CODEC, ClientboundSyncAttachmentPacket::handle);
        registrar.playToClient(ClientboundSyncComponentsPacket.TYPE, ClientboundSyncComponentsPacket.STREAM_CODEC, ClientboundSyncComponentsPacket::handle);
        registrar.playToClient(ClientboundUpdateRecipePacket.TYPE, ClientboundUpdateRecipePacket.STREAM_CODEC, ClientboundUpdateRecipePacket::handle);
        registrar.playToClient(ClientboundSendMessagePacket.TYPE, ClientboundSendMessagePacket.STREAM_CODEC, ClientboundSendMessagePacket::handle);
        registrar.playToClient(ClientboundSyncItemStackPacket.TYPE, ClientboundSyncItemStackPacket.STREAM_CODEC, ClientboundSyncItemStackPacket::handle);

        //Server
        registrar.playToServer(ServerboundAbilitySliderPacket.TYPE, ServerboundAbilitySliderPacket.STREAM_CODEC, ServerboundAbilitySliderPacket::handle);
        registrar.playToServer(ServerboundEquipBackpackPacket.TYPE, ServerboundEquipBackpackPacket.STREAM_CODEC, ServerboundEquipBackpackPacket::handle);
        registrar.playToServer(ServerboundSleepingBagPacket.TYPE, ServerboundSleepingBagPacket.STREAM_CODEC, ServerboundSleepingBagPacket::handle);
        registrar.playToServer(ServerboundSlotPacket.TYPE, ServerboundSlotPacket.STREAM_CODEC, ServerboundSlotPacket::handle);
        registrar.playToServer(ServerboundSorterPacket.TYPE, ServerboundSorterPacket.STREAM_CODEC, ServerboundSorterPacket::handle);
        registrar.playToServer(ServerboundSpecialActionPacket.TYPE, ServerboundSpecialActionPacket.STREAM_CODEC, ServerboundSpecialActionPacket::handle);
        registrar.playToServer(ServerboundTabPacket.TYPE, ServerboundTabPacket.STREAM_CODEC, ServerboundTabPacket::handle);
        registrar.playToServer(ServerboundRemoveUpgradePacket.TYPE, ServerboundRemoveUpgradePacket.STREAM_CODEC, ServerboundRemoveUpgradePacket::handle);
        registrar.playToServer(ServerboundFilterSettingsPacket.TYPE, ServerboundFilterSettingsPacket.STREAM_CODEC, ServerboundFilterSettingsPacket::handle);
        registrar.playToServer(ServerboundShowToolSlotsPacket.TYPE, ServerboundShowToolSlotsPacket.STREAM_CODEC, ServerboundShowToolSlotsPacket::handle);
        registrar.playToServer(ServerboundOpenBackpackPacket.TYPE, ServerboundOpenBackpackPacket.STREAM_CODEC, ServerboundOpenBackpackPacket::handle);
        registrar.playToServer(ServerboundOpenSettingsPacket.TYPE, ServerboundOpenSettingsPacket.STREAM_CODEC, ServerboundOpenSettingsPacket::handle);
        registrar.playToServer(ServerboundFillTankPacket.TYPE, ServerboundFillTankPacket.STREAM_CODEC, ServerboundFillTankPacket::handle);
    }
}