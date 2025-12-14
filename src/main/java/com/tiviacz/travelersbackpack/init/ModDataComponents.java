package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.*;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public class ModDataComponents {
    public static final DataComponentType<Integer> TIER = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Integer> STORAGE_SLOTS = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Integer> UPGRADE_SLOTS = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Integer> TOOL_SLOTS = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Fluids> FLUIDS = DataComponentType.<Fluids>builder().persistent(Fluids.CODEC).networkSynchronized(Fluids.STREAM_CODEC).build();
    public static final DataComponentType<Boolean> TAB_OPEN = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<List<Integer>> FILTER_SETTINGS = DataComponentType.<List<Integer>>builder().persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())).build();
    public static final DataComponentType<List<String>> FILTER_TAGS = DataComponentType.<List<String>>builder().persistent(Codec.STRING.listOf()).networkSynchronized(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())).build();
    public static final DataComponentType<Boolean> UPGRADE_ENABLED = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> SHIFT_CLICK_TO_BACKPACK = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> SHOW_TOOL_SLOTS = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> SHOW_MORE_BUTTONS = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> IS_PLAYING = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Integer> COOLDOWN = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<ItemContainerContents> STARTER_UPGRADES = DataComponentType.<ItemContainerContents>builder().persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).build();
    public static final DataComponentType<RenderInfo> RENDER_INFO = DataComponentType.<RenderInfo>builder().persistent(RenderInfo.CODEC).networkSynchronized(RenderInfo.STREAM_CODEC).build();
    public static final DataComponentType<BackpackContainerContents> BACKPACK_CONTAINER = DataComponentType.<BackpackContainerContents>builder().persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC).build();
    public static final DataComponentType<BackpackContainerContents> UPGRADES = DataComponentType.<BackpackContainerContents>builder().persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC).build();
    public static final DataComponentType<BackpackContainerContents> TOOLS_CONTAINER = DataComponentType.<BackpackContainerContents>builder().persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC).build();
    public static final DataComponentType<Integer> SLEEPING_BAG_COLOR = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Integer> SORT_TYPE = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Boolean> ABILITY_ENABLED = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Boolean> IS_VISIBLE = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Slots> SLOTS = DataComponentType.<Slots>builder().persistent(Slots.CODEC).networkSynchronized(Slots.STREAM_CODEC).build();
    public static final DataComponentType<Integer> UPGRADE_TICK_INTERVAL = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<List<Integer>> HOSE_MODES = DataComponentType.<List<Integer>>builder().persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())).build();

    //Smelting
    public static final DataComponentType<Long> BURN_FINISH_TIME = DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build();
    public static final DataComponentType<Long> COOKING_FINISH_TIME = DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build();
    public static final DataComponentType<Integer> BURN_TOTAL_TIME = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Integer> COOKING_TOTAL_TIME = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();

    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "tier"), TIER);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "storage_slots"), STORAGE_SLOTS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "upgrade_slots"), UPGRADE_SLOTS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "tool_slots"), TOOL_SLOTS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "fluids"), FLUIDS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "tab_open"), TAB_OPEN);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "filter_settings"), FILTER_SETTINGS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "filter_tags"), FILTER_TAGS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "upgrade_enabled"), UPGRADE_ENABLED);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "shift_click_to_backpack"), SHIFT_CLICK_TO_BACKPACK);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "show_tool_slots"), SHOW_TOOL_SLOTS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "show_more_buttons"), SHOW_MORE_BUTTONS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "is_playing"), IS_PLAYING);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cooldown"), COOLDOWN);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "starter_upgrades"), STARTER_UPGRADES);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "render_info"), RENDER_INFO);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_container"), BACKPACK_CONTAINER);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "upgrades"), UPGRADES);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "tools_container"), TOOLS_CONTAINER);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sleeping_bag_color"), SLEEPING_BAG_COLOR);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sort_type"), SORT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "ability_enabled"), ABILITY_ENABLED);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "is_visible"), IS_VISIBLE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "slots"), SLOTS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "upgrade_tick_interval"), UPGRADE_TICK_INTERVAL);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hose_modes"), HOSE_MODES);

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "burn_finish_time"), BURN_FINISH_TIME);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cooking_finish_time"), COOKING_FINISH_TIME);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "burn_total_time"), BURN_TOTAL_TIME);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "cooking_total_time"), COOKING_TOTAL_TIME);
    }
}