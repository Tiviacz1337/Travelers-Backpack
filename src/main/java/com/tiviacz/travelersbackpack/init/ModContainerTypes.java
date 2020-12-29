package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes
{
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, TravelersBackpack.MODID);

    public static final RegistryObject<ContainerType<TravelersBackpackTileContainer>> TRAVELERS_BACKPACK_TILE = CONTAINER_TYPES.register("travelers_backpack_tile", () -> IForgeContainerType.create(TravelersBackpackTileContainer::new));
    public static final RegistryObject<ContainerType<TravelersBackpackItemContainer>> TRAVELERS_BACKPACK_ITEM = CONTAINER_TYPES.register("travelers_backpack_item", () -> IForgeContainerType.create(TravelersBackpackItemContainer::new));
}
