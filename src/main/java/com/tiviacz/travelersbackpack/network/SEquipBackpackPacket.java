package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SEquipBackpackPacket
{
    private final boolean equip;

    public SEquipBackpackPacket(boolean equip)
    {
        this.equip = equip;
    }

    public static SEquipBackpackPacket decode(final PacketBuffer buffer)
    {
        final boolean equip = buffer.readBoolean();

        return new SEquipBackpackPacket(equip);
    }

    public static void encode(final SEquipBackpackPacket message, final PacketBuffer buffer)
    {
        buffer.writeBoolean(message.equip);
    }

    public static void handle(final SEquipBackpackPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.equip)
                {
                    if(!TravelersBackpack.enableCurios())
                    {
                        if(!CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                        {
                            ServerActions.equipBackpack(serverPlayerEntity);
                        }
                        else
                        {
                            serverPlayerEntity.closeContainer();
                            serverPlayerEntity.sendMessage(new TranslationTextComponent(Reference.OTHER_BACKPACK), serverPlayerEntity.getUUID());
                        }
                    }
                }
                else
                {
                    if(!TravelersBackpack.enableCurios())
                    {
                        if(CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                        {
                            ServerActions.unequipBackpack(serverPlayerEntity);
                        }
                        else
                        {
                            serverPlayerEntity.closeContainer();
                            serverPlayerEntity.sendMessage(new TranslationTextComponent(Reference.NO_BACKPACK), serverPlayerEntity.getUUID());
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}