package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.FluidTanks;
import com.tiviacz.travelersbackpack.components.Settings;
import com.tiviacz.travelersbackpack.components.Slots;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponents
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TravelersBackpack.MODID);

    public static final RegistryObject<DataComponentType<Integer>> TIER =
            register("tier", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<BackpackContainerContents>> BACKPACK_CONTAINER =
            register("backpack_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<BackpackContainerContents>> CRAFTING_CONTAINER =
            register("crafting_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<BackpackContainerContents>> TOOLS_CONTAINER =
            register("tools_container", builder -> builder.persistent(BackpackContainerContents.CODEC).networkSynchronized(BackpackContainerContents.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<FluidTanks>> FLUID_TANKS =
            register("fluid_tanks", builder -> builder.persistent(FluidTanks.CODEC).networkSynchronized(FluidTanks.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<Integer>> SLEEPING_BAG_COLOR =
            register("sleeping_bag_color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<Boolean>> ABILITY =
            register("ability_switch", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<List<List<Byte>>>> SETTINGS =
            register("settings", builder -> builder.persistent(Settings.CODEC).networkSynchronized(Settings.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<Boolean>> VISIBILITY =
            register("visibility", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final RegistryObject<DataComponentType<Slots>> SLOTS =
            register("slots", builder -> builder.persistent(Slots.CODEC).networkSynchronized(Slots.STREAM_CODEC));

    public static final RegistryObject<DataComponentType<Integer>> LAST_TIME =
            register("last_time", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistryObject<DataComponentType<List<Integer>>> HOSE_MODES =
            register("hose_modes", builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    private static <T> RegistryObject<DataComponentType<T>> register(String p_332092_, UnaryOperator<DataComponentType.Builder<T>> p_331261_) {
        return DATA_COMPONENT_TYPES.register(p_332092_, () -> p_331261_.apply(DataComponentType.builder()).build());
    }
}
