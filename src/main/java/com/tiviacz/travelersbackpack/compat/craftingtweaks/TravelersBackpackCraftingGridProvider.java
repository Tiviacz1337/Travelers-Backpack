package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.fml.loading.FMLEnvironment;

public class TravelersBackpackCraftingGridProvider {//implements CraftingGridProvider {

    public TravelersBackpackCraftingGridProvider() {
        //CraftingTweaksAPI.registerCraftingGridProvider(this);
        if(FMLEnvironment.dist.isClient()) {
            // TravelersBackpackCraftingGridAddition.registerCraftingTweaksAddition();
        }
    }

    //@Override
    public String getModId() {
        return "travelersbackpack";
    }

    //@Override
    public boolean requiresServerSide() {
        return false;
    }

    //@Override
    public boolean handles(AbstractContainerMenu menu) {
        return true;
    }

    //@Override
    public void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu) {
        //if(menu instanceof TravelersBackpackBaseMenu backpackMenu) {
        //    builder.addGrid(backpackMenu.container.getCombinedHandler().getSlots() - 8, 9).setButtonAlignment(ButtonAlignment.RIGHT).hideAllTweakButtons();
        //}
    }
}