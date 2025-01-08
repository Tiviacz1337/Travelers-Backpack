package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
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

    public static ClientboundSyncComponentsPacket decode(final FriendlyByteBuf buffer) {
        final int entityID = buffer.readInt();
        final CompoundTag map = buffer.readNbt();
        return new ClientboundSyncComponentsPacket(entityID, map);
    }

    public static void encode(final ClientboundSyncComponentsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeNbt(message.map);
    }

    public static void handle(final ClientboundSyncComponentsPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final Player playerEntity = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
            ITravelersBackpack data = CapabilityUtils.getCapability(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));
            if(data != null) {
                data.applyComponents(message.map);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
