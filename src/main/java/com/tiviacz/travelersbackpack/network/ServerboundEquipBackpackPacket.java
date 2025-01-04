package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundEquipBackpackPacket {
    private final boolean equip;

    public ServerboundEquipBackpackPacket(boolean equip) {
        this.equip = equip;
    }

    public static ServerboundEquipBackpackPacket decode(final FriendlyByteBuf buffer) {
        final boolean equip = buffer.readBoolean();

        return new ServerboundEquipBackpackPacket(equip);
    }

    public static void encode(final ServerboundEquipBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.equip);
    }

    public static void handle(final ServerboundEquipBackpackPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer) {
                if(message.equip) {
                    ServerActions.equipBackpack(serverPlayer);
                } else {
                    ServerActions.unequipBackpack(serverPlayer);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}