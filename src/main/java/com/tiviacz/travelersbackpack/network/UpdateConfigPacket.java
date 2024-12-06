package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfigData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateConfigPacket(CompoundTag compound) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<UpdateConfigPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "update_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateConfigPacket> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, UpdateConfigPacket::compound, UpdateConfigPacket::new);

    public static void apply(UpdateConfigPacket message, ClientPlayNetworking.Context context)
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
