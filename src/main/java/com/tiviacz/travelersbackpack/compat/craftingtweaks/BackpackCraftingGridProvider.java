package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.blay09.mods.craftingtweaks.api.ButtonAlignment;
import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class BackpackCraftingGridProvider implements CraftingGridProvider {
    @Override
    public String getModId() {
        return "travelersbackpack";
    }

    @Override
    public boolean requiresServerSide() {
        return true;
    }

    @Override
    public boolean handles(AbstractContainerMenu menu) {
        return menu instanceof BackpackBaseMenu;
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder craftingGridBuilder, AbstractContainerMenu abstractContainerMenu) {
        if(abstractContainerMenu instanceof BackpackBaseMenu backpackMenu) {
            craftingGridBuilder.addGrid(backpackMenu.CRAFTING_GRID_START, 9).setButtonAlignment(ButtonAlignment.RIGHT).hideAllTweakButtons();
        }
    }
}