package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public interface IUpgrade<T> {
    /**
     * Method to remove all additional things that upgrade might add
     */
    default void remove() {

    }

    /**
     * Called when the upgrade is removed from the backpack via the upgrade tab
     *
     * @param removedStack
     */
    default void onUpgradeRemoved(ItemStack removedStack) {

    }

    @OnlyIn(Dist.CLIENT)
    WidgetBase createWidget(BackpackScreen screen, int x, int y);

    List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y);

    default void initializeContainers(BackpackBaseMenu menu, BackpackWrapper wrapper) {

    }

    Point getTabSize();

    boolean isTabOpened();
}