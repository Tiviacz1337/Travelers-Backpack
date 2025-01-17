package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundEquipBackpackPacket implements IPacket<ServerboundEquipBackpackPacket> {
    private final boolean equip;

    public ServerboundEquipBackpackPacket(boolean equip) {
        this.equip = equip;
    }

    public static ServerboundEquipBackpackPacket decode(final FriendlyByteBuf buffer) {
        final boolean equip = buffer.readBoolean();

        return new ServerboundEquipBackpackPacket(equip);
    }

    public void encode(final ServerboundEquipBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.equip);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.EQUIP_BACKPACK_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundEquipBackpackPacket message = decode(buf);

        server.execute(() -> {
            if(message.equip) {
                ServerActions.equipBackpack(player);
            } else {
                ServerActions.unequipBackpack(player);
            }
        });
    }
}