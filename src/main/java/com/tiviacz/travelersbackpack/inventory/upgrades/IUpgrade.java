package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

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

    default void onUpgradeRemoved(ItemStack removedStack, @Nullable Player player) {
        onUpgradeRemoved(removedStack);
    }

    @OnlyIn(Dist.CLIENT)
    WidgetBase createWidget(BackpackScreen screen, int x, int y);

    List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y);

    List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y);

    default void initializeContainers(BackpackBaseMenu menu, BackpackWrapper wrapper) {

    }

    Point getTabSize();

    boolean isTabOpened();
}