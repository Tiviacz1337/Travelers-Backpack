package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundSleepingBagPacket implements IPacket<ServerboundSleepingBagPacket> {
    private final BlockPos pos;
    private final boolean isEquipped;

    public ServerboundSleepingBagPacket(BlockPos pos, boolean isEquipped) {
        this.pos = pos;
        this.isEquipped = isEquipped;
    }

    public static ServerboundSleepingBagPacket decode(final FriendlyByteBuf buffer) {
        final BlockPos pos = buffer.readBlockPos();
        boolean isEquipped = buffer.readBoolean();

        return new ServerboundSleepingBagPacket(pos, isEquipped);
    }

    public void encode(final ServerboundSleepingBagPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.isEquipped);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SLEEPING_BAG_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundSleepingBagPacket message = decode(buf);
        server.execute(() -> {
            ServerActions.toggleSleepingBag(player, message.pos, message.isEquipped);
        });
    }
}