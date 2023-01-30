package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class TravelersBackpackPlugin implements IModPlugin
{
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(TravelersBackpackItemMenu.class, ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), RecipeTypes.CRAFTING, 1, 9, 10, 81);
        registration.addRecipeTransferHandler(TravelersBackpackBlockEntityMenu.class, ModMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY.get(), RecipeTypes.CRAFTING, 1, 9, 10, 81);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(TravelersBackpackScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(TravelersBackpackScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                int[] s = gui.settingsWidget.getWidgetSizeAndPos();
                ret.add(new Rect2i(s[0], s[1], s[2], s[3]));
                int[] sort = gui.sortWidget.getWidgetSizeAndPos();
                if(gui.sortWidget.isVisible()) ret.add(new Rect2i(sort[0], sort[1], sort[2], sort[3]));
                int[] memory = gui.memoryWidget.getWidgetSizeAndPos();
                if(gui.memoryWidget.isVisible()) ret.add(new Rect2i(memory[0], memory[1], memory[2], memory[3]));
                return ret;
            }
        });
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(TravelersBackpack.MODID, "travelersbackpack");
    }
}