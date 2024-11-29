package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.common.ServerActions;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpackold.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AbilitySliderPacket(byte screenID, boolean sliderValue) implements CustomPayload
{
    public static final CustomPayload.Id<AbilitySliderPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "ability_slider"));
    public static final PacketCodec<RegistryByteBuf, AbilitySliderPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BYTE, AbilitySliderPacket::screenID, PacketCodecs.BOOL, AbilitySliderPacket::sliderValue, AbilitySliderPacket::new);

    public static void apply(AbilitySliderPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                if(message.screenID() == Reference.WEARABLE_SCREEN_ID && ComponentUtils.isWearingBackpack(context.player()))
                {
                    ServerActions.switchAbilitySlider(context.player(), message.sliderValue());
                }
                else if(message.screenID() == Reference.BLOCK_ENTITY_SCREEN_ID && context.player().currentScreenHandler instanceof TravelersBackpackBlockEntityScreenHandler)
                {
                    ServerActions.switchAbilitySliderBlockEntity(context.player(), ((TravelersBackpackBlockEntityScreenHandler)context.player().currentScreenHandler).inventory.getPosition(), message.sliderValue());
                }
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}
