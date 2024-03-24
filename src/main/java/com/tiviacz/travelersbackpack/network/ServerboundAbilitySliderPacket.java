package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record ServerboundAbilitySliderPacket(byte screenID, boolean sliderValue) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "ability_slider");

    public ServerboundAbilitySliderPacket(FriendlyByteBuf friendlyByteBuf)
    {
        this(friendlyByteBuf.readByte(), friendlyByteBuf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeByte(screenID);
        pBuffer.writeBoolean(sliderValue);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ServerboundAbilitySliderPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID && AttachmentUtils.isWearingBackpack(serverPlayer))
                {
                    ServerActions.switchAbilitySlider(serverPlayer, message.sliderValue);
                }
                else if(message.screenID == Reference.BLOCK_ENTITY_SCREEN_ID && serverPlayer.containerMenu instanceof TravelersBackpackBlockEntityMenu menu)
                {
                    ServerActions.switchAbilitySliderBlockEntity(serverPlayer, menu.container.getPosition(), message.sliderValue);
                }
            }
        });
    }
}