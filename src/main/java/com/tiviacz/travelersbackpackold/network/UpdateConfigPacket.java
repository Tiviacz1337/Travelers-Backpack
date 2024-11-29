package com.tiviacz.travelersbackpackold.network;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfigData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateConfigPacket(NbtCompound compound) implements CustomPayload
{
    public static final CustomPayload.Id<UpdateConfigPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "update_config"));
    public static final PacketCodec<RegistryByteBuf, UpdateConfigPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, UpdateConfigPacket::compound, UpdateConfigPacket::new);

    public static void apply(UpdateConfigPacket message, ClientPlayNetworking.Context context)
    {
        context.client().execute(() ->
        {
            TravelersBackpack.LOGGER.info("Syncing config from server to client...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).setConfig(TravelersBackpackConfig.readFromNbt(message.compound()));
        });
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}
