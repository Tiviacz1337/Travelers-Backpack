package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundSleepingBagPacket {
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

    public static void encode(final ServerboundSleepingBagPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.isEquipped);
    }

    public static void handle(final ServerboundSleepingBagPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if(player instanceof ServerPlayer serverPlayer) {
                ServerActions.toggleSleepingBag(serverPlayer, message.pos, message.isEquipped);
            }
        });

        ctx.setPacketHandled(true);
    }
}