package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SAbilitySliderPacket
{
    private final byte screenID;
    private final boolean sliderValue;

    public SAbilitySliderPacket(byte screenID, boolean sliderValue)
    {
        this.screenID = screenID;
        this.sliderValue = sliderValue;
    }

    public static SAbilitySliderPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean sliderValue = buffer.readBoolean();

        return new SAbilitySliderPacket(screenID, sliderValue);
    }

    public static void encode(final SAbilitySliderPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.sliderValue);
    }

    public static void handle(final SAbilitySliderPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID && CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                {
                    ServerActions.switchAbilitySlider(serverPlayerEntity, message.sliderValue);
                }
                else if(message.screenID == Reference.TILE_SCREEN_ID && serverPlayerEntity.containerMenu instanceof TravelersBackpackTileContainer)
                {
                    ServerActions.switchAbilitySliderTileEntity(serverPlayerEntity, ((TravelersBackpackTileContainer)serverPlayerEntity.containerMenu).inventory.getPosition(), message.sliderValue);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}