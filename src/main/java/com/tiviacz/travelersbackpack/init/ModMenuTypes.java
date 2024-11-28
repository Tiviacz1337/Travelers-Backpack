package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, TravelersBackpack.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<BackpackItemMenu>> BACKPACK_MENU = MENU_TYPES.register("backpack_item", () -> IMenuTypeExtension.create(BackpackItemMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BackpackBlockEntityMenu>> BACKPACK_BLOCK_MENU = MENU_TYPES.register("backpack_block", () -> IMenuTypeExtension.create(BackpackBlockEntityMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BackpackSettingsMenu>> BACKPACK_SETTINGS_MENU = MENU_TYPES.register("backpack_settings", () -> IMenuTypeExtension.create(BackpackSettingsMenu::new));
}