package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateConfigPacket implements IPacket<ClientboundUpdateConfigPacket> {
    private final CompoundTag configTag;

    public ClientboundUpdateConfigPacket(CompoundTag configTag) {
        this.configTag = configTag;
    }

    public static ClientboundUpdateConfigPacket decode(final FriendlyByteBuf buffer) {
        final CompoundTag configTag = buffer.readNbt();
        return new ClientboundUpdateConfigPacket(configTag);
    }

    public void encode(final ClientboundUpdateConfigPacket message, final FriendlyByteBuf buffer) {
        buffer.writeNbt(message.configTag);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.UPDATE_CONFIG_ID;
    }

    public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientboundUpdateConfigPacket message = decode(buf);
        client.execute(() -> {
            TravelersBackpack.LOGGER.info("Syncing config from server to client...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).setConfig(TravelersBackpackConfig.readFromNbt(message.configTag));
        });
    }
}