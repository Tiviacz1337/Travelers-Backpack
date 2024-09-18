package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.BackpackContainerComponent;
import com.tiviacz.travelersbackpack.components.FluidTanks;
import com.tiviacz.travelersbackpack.components.Settings;
import com.tiviacz.travelersbackpack.components.Slots;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModComponentTypes
{
    public static final ComponentType<Integer> TIER = ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final ComponentType<BackpackContainerComponent> BACKPACK_CONTAINER = ComponentType.<BackpackContainerComponent>builder().codec(BackpackContainerComponent.CODEC).packetCodec(BackpackContainerComponent.PACKET_CODEC).build();
    public static final ComponentType<BackpackContainerComponent> CRAFTING_CONTAINER = ComponentType.<BackpackContainerComponent>builder().codec(BackpackContainerComponent.CODEC).packetCodec(BackpackContainerComponent.PACKET_CODEC).build();
    public static final ComponentType<BackpackContainerComponent> TOOLS_CONTAINER = ComponentType.<BackpackContainerComponent>builder().codec(BackpackContainerComponent.CODEC).packetCodec(BackpackContainerComponent.PACKET_CODEC).build();
    public static final ComponentType<FluidTanks> FLUID_TANKS = ComponentType.<FluidTanks>builder().codec(FluidTanks.CODEC).packetCodec(FluidTanks.PACKET_CODEC).build();
    public static final ComponentType<Integer> SLEEPING_BAG_COLOR = ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final ComponentType<Boolean> ABILITY_SWITCH = ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final ComponentType<List<List<Byte>>> SETTINGS = ComponentType.<List<List<Byte>>>builder().codec(Settings.CODEC).packetCodec(Settings.PACKET_CODEC).build();
    public static final ComponentType<Boolean> VISIBILITY = ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final ComponentType<Slots> SLOTS = ComponentType.<Slots>builder().codec(Slots.CODEC).packetCodec(Slots.PACKET_CODEC).build();
    public static final ComponentType<Integer> LAST_TIME = ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final ComponentType<List<Integer>> HOSE_MODES = ComponentType.<List<Integer>>builder().codec(Codec.INT.listOf()).packetCodec(PacketCodecs.INTEGER.collect(PacketCodecs.toList())).build();

    public static void init()
    {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "tier"), TIER);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "backpack_container"), BACKPACK_CONTAINER);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "crafting_container"), CRAFTING_CONTAINER);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "tools_container"), TOOLS_CONTAINER);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "fluid_tanks"), FLUID_TANKS);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "sleeping_bag_color"), SLEEPING_BAG_COLOR);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "ability_switch"), ABILITY_SWITCH);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "settings"), SETTINGS);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "visibility"), VISIBILITY);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "slots"), SLOTS);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "last_time"), LAST_TIME);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TravelersBackpack.MODID, "hose_modes"), HOSE_MODES);
    }
}