package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpackneo.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpackneo.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpackneo.inventory.menu.BackpackBaseMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IUpgrade {
    void remove();

    public default void onUpgradeRemoved(ItemStack removedStack) {

    }

    @Environment(EnvType.CLIENT)
    WidgetBase createWidget(BackpackScreen screen, int x, int y);

    List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y);

    public default void initializeContainers(BackpackBaseMenu menu, BackpackWrapper wrapper) {

    }

    Point getTabSize();

    boolean isTabOpened();
}