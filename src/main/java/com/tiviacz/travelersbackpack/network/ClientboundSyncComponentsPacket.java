package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ClientboundSyncComponentsPacket {
    private final int entityID;
    private final DataComponentMap map;

    public ClientboundSyncComponentsPacket(int entityID, DataComponentMap map) {
        this.entityID = entityID;
        this.map = map;
    }

    public static ClientboundSyncComponentsPacket decode(final RegistryFriendlyByteBuf buffer) {
        final int entityID = buffer.readInt();
        final DataComponentMap map = ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC).decode(buffer);
        return new ClientboundSyncComponentsPacket(entityID, map);
    }

    public static void encode(final ClientboundSyncComponentsPacket message, final RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC).encode(buffer, message.map);
    }

    public static void handle(final ClientboundSyncComponentsPacket message, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            final Player playerEntity = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);
            ITravelersBackpack data = AttachmentUtils.getCapability(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));
            if(data != null) {
                data.applyComponents(message.map);
            }
        });
        ctx.setPacketHandled(true);
    }
}
