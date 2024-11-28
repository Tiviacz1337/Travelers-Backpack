package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TravelersBackpack.MODID);

    public static final RegistryObject<DataComponentType<Integer>> STORAGE_SLOTS =
            register("storage_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<Integer>> UPGRADE_SLOTS =
            register("upgrade_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<Integer>> TOOL_SLOTS =
            register("tool_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<Integer>> TIER =
            register("tier", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<Fluids>> FLUIDS =
            register("fluids", builder -> builder.persistent(Fluids.CODEC).networkSynchronized(Fluids.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<Boolean>> TAB_OPEN =
            register("tab_open", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<List<Integer>>> FILTER_SETTINGS =
            register("filter_settings", builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    public static final RegistryObject<DataComponentType<Boolean>> UPGRADE_ENABLED =
            register("upgrade_enabled", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Boolean>> SHIFT_CLICK_TO_BACKPACK =
            register("shift_click_to_backpack", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Boolean>> SHOW_TOOL_SLOTS =
            register("show_tool_slots", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Boolean>> IS_PLAYING =
            register("is_playing", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Integer>> COOLDOWN =
            register("cooldown", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<List<ItemStack>>> STARTER_UPGRADES =
            register("starter_upgrades", builder -> builder.persistent(ItemStack.CODEC.listOf()).networkSynchronized(ItemStack.LIST_STREAM_CODEC));

    public static final RegistryObject<DataComponentType<RenderInfo>> RENDER_INFO =
            register("render_info", builder -> builder.persistent(RenderInfo.CODEC).networkSynchronized(RenderInfo.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<BackpackContainerContents>> BACKPACK_CONTAINER =
            register("backpack_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<BackpackContainerContents>> UPGRADES =
            register("upgrades", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<BackpackContainerContents>> TOOLS_CONTAINER =
            register("tools_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<Integer>> SLEEPING_BAG_COLOR =
            register("sleeping_bag_color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<Boolean>> ABILITY_ENABLED =
            register("ability_enabled", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Boolean>> IS_VISIBLE =
            register("is_visible", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Slots>> SLOTS =
            register("slots", builder -> builder.persistent(Slots.CODEC).networkSynchronized(Slots.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<Integer>> UPGRADE_TICK_INTERVAL =
            register("upgrade_tick_interval", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<List<Integer>>> HOSE_MODES =
            register("hose_modes", builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    //#TODO FOR REMOVAL
    public static final RegistryObject<DataComponentType<FluidTanksOld>> FLUID_TANKS =
            register("fluid_tanks", builder -> builder.persistent(FluidTanksOld.CODEC).networkSynchronized(FluidTanksOld.STREAM_CODEC));

    private static <T> RegistryObject<DataComponentType<T>> register(String p_332092_, UnaryOperator<DataComponentType.Builder<T>> p_331261_) {
        return DATA_COMPONENT_TYPES.register(p_332092_, () -> p_331261_.apply(DataComponentType.builder()).build());
    }
}