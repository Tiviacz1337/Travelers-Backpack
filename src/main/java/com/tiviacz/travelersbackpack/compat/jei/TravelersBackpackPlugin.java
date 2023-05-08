package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class TravelersBackpackPlugin implements IModPlugin
{
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(new ItemTransferInfo());
        registration.addRecipeTransferHandler(new TileEntityTransferInfo());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(TravelersBackpackScreen.class, new IGuiContainerHandler<TravelersBackpackScreen>() {
            @Override
            public List<Rectangle2d> getGuiExtraAreas(TravelersBackpackScreen gui) {
                List<Rectangle2d> ret = new ArrayList<>();
                int[] s = gui.settingsWidget.getWidgetSizeAndPos();
                ret.add(new Rectangle2d(s[0], s[1], s[2], s[3]));
                int[] sort = gui.sortWidget.getWidgetSizeAndPos();
                if(gui.sortWidget.isVisible()) ret.add(new Rectangle2d(sort[0], sort[1], sort[2], sort[3]));
                int[] memory = gui.memoryWidget.getWidgetSizeAndPos();
                if(gui.memoryWidget.isVisible()) ret.add(new Rectangle2d(memory[0], memory[1], memory[2], memory[3]));
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