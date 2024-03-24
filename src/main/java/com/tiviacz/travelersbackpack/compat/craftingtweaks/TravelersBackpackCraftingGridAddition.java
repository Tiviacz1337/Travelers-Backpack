package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.client.screen.widget.CraftingWidget;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackCraftingGridAddition implements ICraftingTweaks
{
    @Environment(value=EnvType.CLIENT)
    private TravelersBackpackHandledScreen screen;
    private final List<ButtonWidget> buttons = new ArrayList<>();

    public static void registerCraftingTweaksAddition()
    {
        CraftingWidget.setCraftingTweaksAddition(new TravelersBackpackCraftingGridAddition());
    }

    @Environment(value=EnvType.CLIENT)
    private void addButton(ButtonWidget button)
    {
        buttons.add(button);
        screen.addDrawableChild(button);
    }

    @Environment(value=EnvType.CLIENT)
    @Override
    public void onCraftingSlotsHidden()
    {
        if(buttons.isEmpty())
        {
            return;
        }

        List<Element> screenChildren = screen.children;
        List<Drawable> screenDrawables = screen.drawables;
        if(screenChildren == null || screenDrawables == null)
        {
            return;
        }

        buttons.forEach(screenChildren::remove);
        buttons.forEach(screenDrawables::remove);
        buttons.clear();
    }

    @Override
    public void onCraftingSlotsDisplayed()
    {
        Slot thirdSlot = screen.getScreenHandler().getSlot(screen.inventory.getCombinedInventory().size() - 6);
        CraftingTweaksProviderManager.getDefaultCraftingGrid(screen.getScreenHandler()).ifPresent(craftingGrid -> {
            addButton(CraftingTweaksClientAPI.createRotateButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 0)));
            addButton(CraftingTweaksClientAPI.createBalanceButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 1)));
            addButton(CraftingTweaksClientAPI.createClearButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 2)));
        });
    }

    @Override
    public void setScreen(TravelersBackpackHandledScreen screen)
    {
        this.screen = screen;
    }

    @Environment(value=EnvType.CLIENT)
    private int getButtonX(Slot thirdSlot)
    {
        return thirdSlot.x + 19;
    }

    @Environment(value=EnvType.CLIENT)
    private int getButtonY(Slot thirdSlot, int index)
    {
        return thirdSlot.y + 18 * index;
    }
}