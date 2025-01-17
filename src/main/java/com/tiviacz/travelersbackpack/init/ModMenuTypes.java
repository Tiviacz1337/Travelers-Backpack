package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModMenuTypes {
    public static ExtendedScreenHandlerType<BackpackItemMenu> BACKPACK_MENU = new ExtendedScreenHandlerType<>(BackpackItemMenu::new);
    public static ExtendedScreenHandlerType<BackpackBlockEntityMenu> BACKPACK_BLOCK_MENU = new ExtendedScreenHandlerType<>(BackpackBlockEntityMenu::new);
    public static ExtendedScreenHandlerType<BackpackSettingsMenu> BACKPACK_SETTINGS_MENU = new ExtendedScreenHandlerType<>(BackpackSettingsMenu::new);

    public static void init() {
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(TravelersBackpack.MODID, "backpack_item"), BACKPACK_MENU);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(TravelersBackpack.MODID, "backpack_block"), BACKPACK_BLOCK_MENU);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(TravelersBackpack.MODID, "backpack_settings"), BACKPACK_SETTINGS_MENU);
    }
}