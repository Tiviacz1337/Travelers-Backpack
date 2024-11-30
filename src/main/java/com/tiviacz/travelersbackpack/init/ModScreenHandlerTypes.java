package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModScreenHandlerTypes {
    public static ExtendedScreenHandlerType<BackpackItemMenu, ItemScreenData> BACKPACK_MENU = new ExtendedScreenHandlerType<>(BackpackItemMenu::new, ItemScreenData.PACKET_CODEC);
    public static ExtendedScreenHandlerType<BackpackBlockEntityMenu, BlockEntityScreenData> BACKPACK_BLOCK_MENU = new ExtendedScreenHandlerType<>(BackpackBlockEntityMenu::new, BlockEntityScreenData.PACKET_CODEC);
    public static ExtendedScreenHandlerType<BackpackSettingsMenu, ItemScreenData> BACKPACK_SETTINGS_MENU = new ExtendedScreenHandlerType<>(BackpackBlockEntityMenu::new, BlockEntityScreenData.PACKET_CODEC);

    public static void init() {
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_item"), BACKPACK_MENU);
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_block"), BACKPACK_BLOCK_MENU);
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_settings"), BACKPACK_SETTINGS_MENU);
    }

    public record ItemScreenData(byte screenID, int entityID, ItemStack stack) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemScreenData> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.BYTE, ItemScreenData::screenID,
                ByteBufCodecs.INT, ItemScreenData::entityID, ItemStack.OPTIONAL_STREAM_CODEC, ItemScreenData::stack, ItemScreenData::new);
    }

    public record BlockEntityScreenData(int entityId, BlockPos pos) {
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityScreenData> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.INT, BlockEntityScreenData::entityId, BlockPos.STREAM_CODEC, BlockEntityScreenData::pos, BlockEntityScreenData::new);
    }
}