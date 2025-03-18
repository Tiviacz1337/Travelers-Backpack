package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class ModScreenHandlerTypes {
    public static ExtendedScreenHandlerType<BackpackItemMenu, ItemScreenData> BACKPACK_MENU = new ExtendedScreenHandlerType<>(BackpackItemMenu::new, ItemScreenData.PACKET_CODEC);
    public static ExtendedScreenHandlerType<BackpackBlockEntityMenu, BlockEntityScreenData> BACKPACK_BLOCK_MENU = new ExtendedScreenHandlerType<>(BackpackBlockEntityMenu::new, BlockEntityScreenData.PACKET_CODEC);
    public static ExtendedScreenHandlerType<BackpackSettingsMenu, SettingsScreenData> BACKPACK_SETTINGS_MENU = new ExtendedScreenHandlerType<>(BackpackSettingsMenu::new, SettingsScreenData.PACKET_CODEC);

    public static void init() {
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_item"), BACKPACK_MENU);
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_block"), BACKPACK_BLOCK_MENU);
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_settings"), BACKPACK_SETTINGS_MENU);
    }

    public record ItemScreenData(int screenID, int entityID) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemScreenData> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ItemScreenData::screenID,
                ByteBufCodecs.INT, ItemScreenData::entityID, ItemScreenData::new);
    }

    public record BlockEntityScreenData(int entityId, BlockPos pos) {
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityScreenData> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.INT, BlockEntityScreenData::entityId,
                BlockPos.STREAM_CODEC, BlockEntityScreenData::pos, BlockEntityScreenData::new);
    }

    public record SettingsScreenData(boolean isBlockEntity, int screenId, BlockPos pos, int index) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SettingsScreenData> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, SettingsScreenData::isBlockEntity,
                ByteBufCodecs.INT, SettingsScreenData::screenId, BlockPos.STREAM_CODEC, SettingsScreenData::pos, ByteBufCodecs.INT, SettingsScreenData::index, SettingsScreenData::new);
    }
}