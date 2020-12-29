package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class TravelersBackpackPlugin implements IModPlugin
{
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(TravelersBackpackItemContainer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 0, 91);
        registration.addRecipeTransferHandler(TravelersBackpackTileContainer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 0, 91);
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(TravelersBackpack.MODID, "travelersbackpack");
    }
}