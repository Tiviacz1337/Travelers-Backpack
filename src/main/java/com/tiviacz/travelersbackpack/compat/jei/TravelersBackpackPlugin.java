package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class TravelersBackpackPlugin implements IModPlugin
{
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(TravelersBackpackItemMenu.class, ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), RecipeTypes.CRAFTING, 1, 9, 10, 81);
        registration.addRecipeTransferHandler(TravelersBackpackBlockEntityMenu.class, ModMenuTypes.TRAVELERS_BACKPACK_TILE.get(), RecipeTypes.CRAFTING, 1, 9, 10, 81);
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(TravelersBackpack.MODID, "travelersbackpack");
    }
}
