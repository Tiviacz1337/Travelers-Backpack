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

public class EquipBackpackPacket
{
    private final boolean valid;

    public EquipBackpackPacket(boolean valid)
    {
        this.valid = valid;
    }

    public static EquipBackpackPacket decode(final PacketBuffer buffer)
    {
        final boolean valid = buffer.readBoolean();

        return new EquipBackpackPacket(valid);
    }

    public static void encode(final EquipBackpackPacket message, final PacketBuffer buffer)
    {
        buffer.writeBoolean(message.valid);
    }

    public static void handle(final EquipBackpackPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null && message.valid && !TravelersBackpack.enableCurios())
            {
                if(!CapabilityUtils.isWearingBackpack(serverPlayerEntity))
                {
                    ServerActions.equipBackpack(serverPlayerEntity);
                }
                else
                {
                    serverPlayerEntity.closeContainer();
                    //serverPlayerEntity.closeScreen();
                    serverPlayerEntity.sendMessage(new TranslationTextComponent(Reference.OTHER_BACKPACK), serverPlayerEntity.getUUID());
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

