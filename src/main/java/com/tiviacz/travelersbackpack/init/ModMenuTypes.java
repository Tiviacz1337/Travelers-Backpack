package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TravelersBackpack.MODID);

    public static final RegistryObject<MenuType<TravelersBackpackBlockEntityMenu>> TRAVELERS_BACKPACK_BLOCK_ENTITY = MENU_TYPES.register("travelers_backpack_block_entity", () -> IForgeMenuType.create(TravelersBackpackBlockEntityMenu::new));
    public static final RegistryObject<MenuType<TravelersBackpackItemMenu>> TRAVELERS_BACKPACK_ITEM = MENU_TYPES.register("travelers_backpack_item", () -> IForgeMenuType.create(TravelersBackpackItemMenu::new));
}