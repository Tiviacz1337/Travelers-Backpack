package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncComponentsPacket {
    private final int entityID;
    private final CompoundTag map;

    public ClientboundSyncComponentsPacket(int entityID, CompoundTag map) {
        this.entityID = entityID;
        this.map = map;
    }

    public static ClientboundSyncComponentsPacket decode(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();
        CompoundTag map = buffer.readNbt();
        return new ClientboundSyncComponentsPacket(entityID, map);
    }

    public static void encode(ClientboundSyncComponentsPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeNbt(message.map);
    }

    public static void handle(ClientboundSyncComponentsPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
            CapabilityUtils.getCapability(player).ifPresent(data -> data.applyComponents(message.map));
        });
        ctx.get().setPacketHandled(true);
    }
}