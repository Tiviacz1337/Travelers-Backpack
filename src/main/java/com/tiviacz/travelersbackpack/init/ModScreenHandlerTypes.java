package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModScreenHandlerTypes
{
    public static ExtendedScreenHandlerType<TravelersBackpackBlockEntityScreenHandler> TRAVELERS_BACKPACK_BLOCK_ENTITY = new ExtendedScreenHandlerType<>(TravelersBackpackBlockEntityScreenHandler::new);
    public static ExtendedScreenHandlerType<TravelersBackpackItemScreenHandler> TRAVELERS_BACKPACK_ITEM = new ExtendedScreenHandlerType<>(TravelersBackpackItemScreenHandler::new);

    public static void init()
    {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(TravelersBackpack.MODID, "travelers_backpack_block_entity"), TRAVELERS_BACKPACK_BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(TravelersBackpack.MODID, "travelers_backpack_item"), TRAVELERS_BACKPACK_ITEM);
    }
}