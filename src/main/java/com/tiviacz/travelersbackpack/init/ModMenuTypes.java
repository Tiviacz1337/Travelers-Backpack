package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, TravelersBackpack.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<TravelersBackpackBlockEntityMenu>> TRAVELERS_BACKPACK_BLOCK_ENTITY = MENU_TYPES.register("travelers_backpack_block_entity", () -> IMenuTypeExtension.create(TravelersBackpackBlockEntityMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<TravelersBackpackItemMenu>> TRAVELERS_BACKPACK_ITEM = MENU_TYPES.register("travelers_backpack_item", () -> IMenuTypeExtension.create(TravelersBackpackItemMenu::new));
}