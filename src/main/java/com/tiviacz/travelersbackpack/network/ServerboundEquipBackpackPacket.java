package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundEquipBackpackPacket
{
    private final boolean equip;

    public ServerboundEquipBackpackPacket(boolean equip)
    {
        this.equip = equip;
    }

    public static ServerboundEquipBackpackPacket decode(final FriendlyByteBuf buffer)
    {
        final boolean equip = buffer.readBoolean();

        return new ServerboundEquipBackpackPacket(equip);
    }

    public static void encode(final ServerboundEquipBackpackPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(message.equip);
    }

    public static void handle(final ServerboundEquipBackpackPacket message, final CustomPayloadEvent.Context ctx)
    {
        ctx.enqueueWork(() ->
        {
            final ServerPlayer serverPlayer = ctx.getSender();

            if(serverPlayer != null)
            {
                if(message.equip)
                {
                    if(!TravelersBackpack.enableCurios())
                    {
                        if(!CapabilityUtils.isWearingBackpack(serverPlayer))
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
                        if(CapabilityUtils.isWearingBackpack(serverPlayer))
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

        ctx.setPacketHandled(true);
    }
}