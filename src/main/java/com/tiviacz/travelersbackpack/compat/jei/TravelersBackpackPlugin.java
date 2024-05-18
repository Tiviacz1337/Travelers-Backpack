package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.common.Internal;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class TravelersBackpackPlugin implements IModPlugin
{
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(new ItemTransferHandler(Internal.getServerConnection(), registration.getJeiHelpers().getStackHelper(), registration.getTransferHelper(), new ItemTransferInfo()), RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(new BlockEntityTransferHandler(Internal.getServerConnection(), registration.getJeiHelpers().getStackHelper(), registration.getTransferHelper(), new BlockEntityTransferInfo()), RecipeTypes.CRAFTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(TravelersBackpackHandledScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public List<Rect2i> getGuiExtraAreas(TravelersBackpackHandledScreen gui) {
                List<Rect2i> ret = new ArrayList<>();
                int[] s = gui.settingsWidget.getWidgetSizeAndPos();
                ret.add(new Rect2i(s[0], s[1], s[2], s[3]));
                int[] sort = gui.sortWidget.getWidgetSizeAndPos();
                if(gui.sortWidget.isVisible()) ret.add(new Rect2i(sort[0], sort[1], sort[2], sort[3]));
                int[] memory = gui.memoryWidget.getWidgetSizeAndPos();
                if(gui.memoryWidget.isVisible()) ret.add(new Rect2i(memory[0], memory[1], memory[2], memory[3]));
                int[] crafting = gui.craftingWidget.getWidgetSizeAndPos();
                if(gui.craftingWidget.isVisible()) ret.add(new Rect2i(crafting[0], crafting[1], crafting[2], crafting[3]));
                return ret;
            }
        });
    }

    @Override
    public Identifier getPluginUid()
    {
        return new Identifier(TravelersBackpack.MODID, "travelersbackpack");
    }
}