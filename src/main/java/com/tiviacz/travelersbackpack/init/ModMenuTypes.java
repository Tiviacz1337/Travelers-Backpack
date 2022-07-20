package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, TravelersBackpack.MODID);

    public static final RegistryObject<MenuType<TravelersBackpackBlockEntityMenu>> TRAVELERS_BACKPACK_TILE = MENU_TYPES.register("travelers_backpack_block_entity", () -> IForgeContainerType.create(TravelersBackpackBlockEntityMenu::new));
    public static final RegistryObject<MenuType<TravelersBackpackItemMenu>> TRAVELERS_BACKPACK_ITEM = MENU_TYPES.register("travelers_backpack_item", () -> IForgeContainerType.create(TravelersBackpackItemMenu::new));
}