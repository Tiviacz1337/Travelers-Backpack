package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TravelersBackpack.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORAGE_SLOTS =
            register("storage_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> UPGRADE_SLOTS =
            register("upgrade_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TOOL_SLOTS =
            register("tool_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TIER =
            register("tier", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Fluids>> FLUIDS =
            register("fluids", builder -> builder.persistent(Fluids.CODEC).networkSynchronized(Fluids.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TAB_OPEN =
            register("tab_open", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> FILTER_SETTINGS =
            register("filter_settings", builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> FILTER_TAGS =
            register("filter_tags", builder -> builder.persistent(Codec.STRING.listOf()).networkSynchronized(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> UPGRADE_ENABLED =
            register("upgrade_enabled", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHIFT_CLICK_TO_BACKPACK =
            register("shift_click_to_backpack", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHOW_TOOL_SLOTS =
            register("show_tool_slots", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHOW_MORE_BUTTONS =
            register("show_more_buttons", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_PLAYING =
            register("is_playing", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COOLDOWN =
            register("cooldown", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StarterUpgrades>> STARTER_UPGRADES =
            register("starter_upgrades", builder -> builder.persistent(StarterUpgrades.CODEC).networkSynchronized(StarterUpgrades.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RenderInfo>> RENDER_INFO =
            register("render_info", builder -> builder.persistent(RenderInfo.CODEC).networkSynchronized(RenderInfo.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BackpackContainerContents>> BACKPACK_CONTAINER =
            register("backpack_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BackpackContainerContents>> UPGRADES =
            register("upgrades", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BackpackContainerContents>> TOOLS_CONTAINER =
            register("tools_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SLEEPING_BAG_COLOR =
            register("sleeping_bag_color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ABILITY_ENABLED =
            register("ability_enabled", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_VISIBLE =
            register("is_visible", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Slots>> SLOTS =
            register("slots", builder -> builder.persistent(Slots.CODEC).networkSynchronized(Slots.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> UPGRADE_TICK_INTERVAL =
            register("upgrade_tick_interval", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> HOSE_MODES =
            register("hose_modes", builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    //Smelting
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> BURN_FINISH_TIME =
            register("burn_finish_time", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> COOKING_FINISH_TIME =
            register("cooking_finish_time", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BURN_TOTAL_TIME =
            register("burn_total_time", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COOKING_TOTAL_TIME =
            register("cooking_total_time", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    //#TODO FOR REMOVAL
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidTanksOld>> FLUID_TANKS =
            register("fluid_tanks", builder -> builder.persistent(FluidTanksOld.CODEC).networkSynchronized(FluidTanksOld.STREAM_CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String p_332092_, UnaryOperator<DataComponentType.Builder<T>> p_331261_) {
        return DATA_COMPONENT_TYPES.register(p_332092_, () -> p_331261_.apply(DataComponentType.builder()).build());
    }
}