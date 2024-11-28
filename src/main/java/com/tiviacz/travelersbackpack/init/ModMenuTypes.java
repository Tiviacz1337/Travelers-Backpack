package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, TravelersBackpack.MODID);

    public static final RegistryObject<MenuType<BackpackItemMenu>> BACKPACK_MENU = MENU_TYPES.register("backpack_item", () -> IForgeMenuType.create((i, inv, buf) -> new BackpackItemMenu(i, inv, new RegistryFriendlyByteBuf(buf, inv.player.registryAccess()))));
    public static final RegistryObject<MenuType<BackpackBlockEntityMenu>> BACKPACK_BLOCK_MENU = MENU_TYPES.register("backpack_block", () -> IForgeMenuType.create((i, inv, buf) -> new BackpackBlockEntityMenu(i, inv, new RegistryFriendlyByteBuf(buf, inv.player.registryAccess()))));
    public static final RegistryObject<MenuType<BackpackSettingsMenu>> BACKPACK_SETTINGS_MENU = MENU_TYPES.register("backpack_settings", () -> IForgeMenuType.create((i, inv, buf) -> new BackpackSettingsMenu(i, inv, new RegistryFriendlyByteBuf(buf, inv.player.registryAccess()))));
}