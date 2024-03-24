package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.CraftingWidget;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackCraftingGridAddition implements ICraftingTweaks
{
    @OnlyIn(Dist.CLIENT)
    private TravelersBackpackScreen screen;

    private static final Method ADD_RENDERABLE_WIDGET = ObfuscationReflectionHelper.findMethod(Screen.class, "m_142416_", GuiEventListener.class);
    private final List<Button> buttons = new ArrayList<>();

    public static void registerCraftingTweaksAddition()
    {
        CraftingWidget.setCraftingTweaksAddition(new TravelersBackpackCraftingGridAddition());
    }

    @OnlyIn(Dist.CLIENT)
    private void addButton(Button button)
    {
        buttons.add(button);
        try {
            ADD_RENDERABLE_WIDGET.invoke(screen, button);
        }
        catch(IllegalAccessException | InvocationTargetException e) {
            TravelersBackpack.LOGGER.error("Error calling addButton in Screen class", e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onCraftingSlotsHidden()
    {
        if(buttons.isEmpty())
        {
            return;
        }

        List<GuiEventListener> screenChildren = ObfuscationReflectionHelper.getPrivateValue(Screen.class, screen, "f_96540_");
        List<AbstractWidget> screenRenderables = ObfuscationReflectionHelper.getPrivateValue(Screen.class, screen, "f_169369_");
        if(screenChildren == null || screenRenderables == null)
        {
            return;
        }

        buttons.forEach(screenChildren::remove);
        buttons.forEach(screenRenderables::remove);
        buttons.clear();
    }

    @Override
    public void onCraftingSlotsDisplayed()
    {
        Slot thirdSlot = screen.getMenu().getSlot(screen.container.getCombinedHandler().getSlots() - 6);
        CraftingTweaksProviderManager.getDefaultCraftingGrid(screen.getMenu()).ifPresent(craftingGrid -> {
            addButton(CraftingTweaksClientAPI.createRotateButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 0)));
            addButton(CraftingTweaksClientAPI.createBalanceButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 1)));
            addButton(CraftingTweaksClientAPI.createClearButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 2)));
        });
    }

    @Override
    public void setScreen(TravelersBackpackScreen screen)
    {
        this.screen = screen;
    }

    @OnlyIn(Dist.CLIENT)
    private int getButtonX(Slot thirdSlot)
    {
        return thirdSlot.x + 19;
    }

    @OnlyIn(Dist.CLIENT)
    private int getButtonY(Slot thirdSlot, int index)
    {
        return thirdSlot.y + 18 * index;
    }
}