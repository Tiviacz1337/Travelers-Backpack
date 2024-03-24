package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import net.blay09.mods.craftingtweaks.api.ButtonAlignment;
import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;

public class TravelersBackpackCraftingGridProvider implements CraftingGridProvider
{
    public TravelersBackpackCraftingGridProvider()
    {
        CraftingTweaksAPI.registerCraftingGridProvider(this);
    }

    public static void registerClient()
    {
        MinecraftClient.getInstance().execute(TravelersBackpackCraftingGridAddition::registerCraftingTweaksAddition);
    }

    @Override
    public String getModId()
    {
        return "travelersbackpack";
    }

    @Override
    public boolean requiresServerSide()
    {
        return false;
    }

    @Override
    public boolean handles(ScreenHandler menu)
    {
        return true;
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder builder, ScreenHandler menu)
    {
        if(menu instanceof TravelersBackpackBaseScreenHandler backpackMenu)
        {
            builder.addGrid(backpackMenu.inventory.getCombinedInventory().size() - 8, 9).setButtonAlignment(ButtonAlignment.RIGHT).hideAllTweakButtons();
        }
    }
}