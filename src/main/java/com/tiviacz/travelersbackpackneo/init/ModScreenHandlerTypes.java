package com.tiviacz.travelersbackpackneo.init;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpackold.inventory.screen.TravelersBackpackItemScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlerTypes
{
    public static ExtendedScreenHandlerType<TravelersBackpackBlockEntityScreenHandler, BlockEntityScreenData> TRAVELERS_BACKPACK_BLOCK_ENTITY = new ExtendedScreenHandlerType<>(TravelersBackpackBlockEntityScreenHandler::new, BlockEntityScreenData.PACKET_CODEC);
    public static ExtendedScreenHandlerType<TravelersBackpackItemScreenHandler, ItemScreenData> TRAVELERS_BACKPACK_ITEM = new ExtendedScreenHandlerType<>(TravelersBackpackItemScreenHandler::new, ItemScreenData.PACKET_CODEC);

    public static void init()
    {
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(TravelersBackpack.MODID, "travelers_backpack_block_entity"), TRAVELERS_BACKPACK_BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(TravelersBackpack.MODID, "travelers_backpack_item"), TRAVELERS_BACKPACK_ITEM);
    }

    public record ItemScreenData(byte screenID, int entityID)
    {
        public static final PacketCodec<RegistryByteBuf, ItemScreenData> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BYTE, ItemScreenData::screenID, PacketCodecs.INTEGER, ItemScreenData::entityID, ItemScreenData::new);
    }

    public record BlockEntityScreenData(BlockPos pos)
    {
        public static final PacketCodec<RegistryByteBuf, BlockEntityScreenData> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, BlockEntityScreenData::pos, BlockEntityScreenData::new);
    }
}