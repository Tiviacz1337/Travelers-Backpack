package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateConfigPacket(CompoundTag compound) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundUpdateConfigPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "update_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateConfigPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, ClientboundUpdateConfigPacket::compound, ClientboundUpdateConfigPacket::new);

    public static void handle(ClientboundUpdateConfigPacket message, ClientPlayNetworking.Context context)
    {
        context.client().execute(() ->
        {
            TravelersBackpack.LOGGER.info("Syncing config from server to client...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).setConfig(TravelersBackpackConfig.readFromNbt(message.compound()));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
