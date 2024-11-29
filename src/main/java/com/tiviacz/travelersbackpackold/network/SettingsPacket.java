package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.inventory.SettingsManager;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpackold.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SettingsPacket(byte screenID, byte dataArray, int place, byte value) implements CustomPayload
{
    public static final CustomPayload.Id<SettingsPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "settings"));
    public static final PacketCodec<RegistryByteBuf, SettingsPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BYTE, SettingsPacket::screenID, PacketCodecs.BYTE, SettingsPacket::dataArray,
            PacketCodecs.INTEGER, SettingsPacket::place, PacketCodecs.BYTE, SettingsPacket::value, SettingsPacket::new);

    public static void apply(SettingsPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                if(message.screenID() == Reference.WEARABLE_SCREEN_ID)
                {
                    SettingsManager manager = ComponentUtils.getBackpackInv(context.player()).getSettingsManager();
                    manager.set(message.dataArray(), message.place(), message.value());
                }
                if(message.screenID() == Reference.ITEM_SCREEN_ID)
                {
                    SettingsManager manager = ((TravelersBackpackItemScreenHandler)context.player().currentScreenHandler).inventory.getSettingsManager();
                    manager.set(message.dataArray(), message.place(), message.value());
                }
                if(message.screenID() == Reference.BLOCK_ENTITY_SCREEN_ID)
                {
                    SettingsManager manager = ((TravelersBackpackBlockEntityScreenHandler)context.player().currentScreenHandler).inventory.getSettingsManager();
                    manager.set(message.dataArray(), message.place(), message.value());
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