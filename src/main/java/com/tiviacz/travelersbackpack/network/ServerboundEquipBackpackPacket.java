package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record ServerboundEquipBackpackPacket(boolean equip) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "equip_backpack");

    public ServerboundEquipBackpackPacket(FriendlyByteBuf friendlyByteBuf)
    {
        this(friendlyByteBuf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer)
    {
        pBuffer.writeBoolean(equip);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ServerboundEquipBackpackPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            final Optional<Player> player = ctx.player();

            if(player.isPresent() && player.get() instanceof ServerPlayer serverPlayer)
            {
                if(message.equip)
                {
                    if(!TravelersBackpack.enableCurios())
                    {
                        if(!AttachmentUtils.isWearingBackpack(serverPlayer))
                        {
                            ServerActions.equipBackpack(serverPlayer);
                        }
                        else
                        {
                            serverPlayer.closeContainer();
                            serverPlayer.sendSystemMessage(Component.translatable(Reference.OTHER_BACKPACK));
                        }
                    }
                }
                else
                {
                    if(!TravelersBackpack.enableCurios())
                    {
                        if(AttachmentUtils.isWearingBackpack(serverPlayer))
                        {
                            ServerActions.unequipBackpack(serverPlayer);
                        }
                        else
                        {
                            serverPlayer.closeContainer();
                            serverPlayer.sendSystemMessage(Component.translatable(Reference.NO_BACKPACK));
                        }
                    }
                }
            }
        });
    }
}