package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundAbilitySliderPacket
{
    private final byte screenID;
    private final boolean sliderValue;

    public ServerboundAbilitySliderPacket(byte screenID, boolean sliderValue)
    {
        this.screenID = screenID;
        this.sliderValue = sliderValue;
    }

    public static ServerboundAbilitySliderPacket decode(final FriendlyByteBuf buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean sliderValue = buffer.readBoolean();

        return new ServerboundAbilitySliderPacket(screenID, sliderValue);
    }

    public static void encode(final ServerboundAbilitySliderPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.sliderValue);
    }

    public static void handle(final ServerboundAbilitySliderPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID && CapabilityUtils.isWearingBackpack(serverPlayer))
                {
                    ServerActions.switchAbilitySlider(serverPlayer, message.sliderValue);
                }
                else if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID && serverPlayer.containerMenu instanceof TravelersBackpackBlockEntityMenu menu)
                {
                    ServerActions.switchAbilitySliderBlockEntity(serverPlayer, menu.container.getPosition(), message.sliderValue);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}