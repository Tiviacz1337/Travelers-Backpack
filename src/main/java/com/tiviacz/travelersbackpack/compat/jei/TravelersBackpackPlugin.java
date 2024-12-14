package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.common.Internal;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class TravelersBackpackPlugin implements IModPlugin {
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new ItemTransferHandler(Internal.getServerConnection(), registration.getJeiHelpers().getStackHelper(), registration.getTransferHelper(), new ItemTransferInfo()), RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(new BlockEntityTransferHandler(Internal.getServerConnection(), registration.getJeiHelpers().getStackHelper(), registration.getTransferHelper(), new BlockEntityTransferInfo()), RecipeTypes.CRAFTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BackpackSettingsScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(BackpackSettingsScreen screen) {
                List<Rect2i> ret = new ArrayList<>();
                screen.children().stream().filter(w -> w instanceof WidgetBase).forEach(widget -> {
                    int[] size = ((WidgetBase)widget).getWidgetSizeAndPos();
                    ret.add(new Rect2i(size[0], size[1], size[2], size[3]));
                });
                return ret;
            }
        });
        registration.addGuiContainerHandler(BackpackScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(BackpackScreen screen) {
                List<Rect2i> ret = new ArrayList<>();
                int[] s = screen.settingsWidget.getWidgetSizeAndPos();
                ret.add(new Rect2i(s[0], s[1], s[2], s[3]));

                screen.children().stream().filter(w -> w instanceof UpgradeWidgetBase).forEach(widget -> {
                    int[] size = ((UpgradeWidgetBase)widget).getWidgetSizeAndPos();
                    ret.add(new Rect2i(size[0], size[1], size[2], size[3]));
                });
                screen.upgradeSlots.forEach(slot -> {
                    if(!slot.isHidden()) {
                        int[] size = slot.getUpgradeSlotSizeAndPos();
                        ret.add(new Rect2i(size[0], size[1], size[2], size[3]));
                    }
                });
                return ret;
            }
        });
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelersbackpack");
    }
}