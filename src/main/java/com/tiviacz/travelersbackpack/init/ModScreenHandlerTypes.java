package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlerTypes
{
    public static ScreenHandlerType<TravelersBackpackBlockEntityScreenHandler> TRAVELERS_BACKPACK_BLOCK_ENTITY;
    public static ScreenHandlerType<TravelersBackpackItemScreenHandler> TRAVELERS_BACKPACK_ITEM;

    public static void init()
    {
        TRAVELERS_BACKPACK_BLOCK_ENTITY = ScreenHandlerRegistry.registerExtended(new Identifier(TravelersBackpack.MODID, "travelers_backpack_block_entity"), TravelersBackpackBlockEntityScreenHandler::new);
        TRAVELERS_BACKPACK_ITEM = ScreenHandlerRegistry.registerExtended(new Identifier(TravelersBackpack.MODID, "travelers_backpack_item"), TravelersBackpackItemScreenHandler::new);
    }
}