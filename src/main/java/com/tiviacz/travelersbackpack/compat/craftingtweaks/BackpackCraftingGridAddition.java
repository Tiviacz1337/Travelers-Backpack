package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingWidget;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.blay09.mods.craftingtweaks.api.TweakType;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class BackpackCraftingGridAddition implements ICraftingTweaks {
    private BackpackScreen screen;
    private final List<AbstractWidget> widgets = new ArrayList<>();

    public static void registerCraftingTweaksAddition() {
        CraftingWidget.setCraftingTweaksAddition(new BackpackCraftingGridAddition());
    }

    private void addButton(AbstractWidget widget) {
        widgets.add(widget);
        screen.addCompatWidget(widget);
    }

    @Override
    public void onCraftingSlotsHidden() {
        if(widgets.isEmpty()) {
            return;
        }

        widgets.forEach(screen::removeCompatWidget);
        widgets.clear();
    }

    @Override
    public void onCraftingSlotsDisplayed() {
        Slot thirdSlot = screen.getMenu().getSlot(screen.getMenu().CRAFTING_GRID_START + 2);
        CraftingTweaksProviderManager.getDefaultCraftingGrid(screen.getMenu()).ifPresent(craftingGrid -> {
            addButton(CraftingTweaksClientAPI.createTweakButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 0), TweakType.Rotate));
            addButton(CraftingTweaksClientAPI.createTweakButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 1), TweakType.Balance));
            addButton(CraftingTweaksClientAPI.createTweakButtonRelative(craftingGrid, screen, getButtonX(thirdSlot), getButtonY(thirdSlot, 2), TweakType.Clear));
        });
    }

    @Override
    public void setScreen(BackpackScreen screen) {
        this.screen = screen;
    }

    private int getButtonX(Slot thirdSlot) {
        return thirdSlot.x + 19;
    }

    private int getButtonY(Slot thirdSlot, int index) {
        return thirdSlot.y + 18 * index;
    }
}