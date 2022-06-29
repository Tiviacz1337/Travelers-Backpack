package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EquipBackpackPacket
{
    private final boolean valid;

    public EquipBackpackPacket(boolean valid)
    {
        this.valid = valid;
    }

    public static EquipBackpackPacket decode(final FriendlyByteBuf buffer)
    {
        final boolean valid = buffer.readBoolean();

        return new EquipBackpackPacket(valid);
    }

    public static void encode(final EquipBackpackPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(message.valid);
    }

    public static void handle(final EquipBackpackPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayer serverPlayer = ctx.get().getSender();

            if(serverPlayer != null && message.valid && !TravelersBackpack.enableCurios())
            {
                if(!CapabilityUtils.isWearingBackpack(serverPlayer))
                {
                    ServerActions.equipBackpack(serverPlayer);
                }
                else
                {
                    serverPlayer.closeContainer();
                    serverPlayer.sendMessage(new TranslatableComponent(Reference.OTHER_BACKPACK), serverPlayer.getUUID());
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

