package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import net.blay09.mods.craftingtweaks.api.ButtonAlignment;
import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class TravelersBackpackCraftingGridProvider implements CraftingGridProvider
{
    public TravelersBackpackCraftingGridProvider()
    {
        CraftingTweaksAPI.registerCraftingGridProvider(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TravelersBackpackCraftingGridAddition::registerCraftingTweaksAddition);
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
    public boolean handles(AbstractContainerMenu menu)
    {
        return true;
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu)
    {
        if(menu instanceof TravelersBackpackBaseMenu backpackMenu)
        {
            builder.addGrid(backpackMenu.container.getCombinedHandler().getSlots() - 8, 9).setButtonAlignment(ButtonAlignment.RIGHT).hideAllTweakButtons();
        }
    }
}