package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import net.minecraft.world.item.ItemStack;

public abstract class UpgradeBase<T> implements IUpgrade<T> {
    public UpgradeManager upgradeManager;
    public int dataHolderSlot;
    public Point openTabSize;

    public UpgradeBase(UpgradeManager manager, int dataHolderSlot, Point openTabSize) {
        this.upgradeManager = manager;
        this.dataHolderSlot = dataHolderSlot;
        this.openTabSize = openTabSize;
    }

    public UpgradeManager getUpgradeManager() {
        return this.upgradeManager;
    }

    public int getDataHolderSlot() {
        return this.dataHolderSlot;
    }

    @Override
    public boolean isTabOpened() {
        return this.upgradeManager.getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.TAB_OPEN, false);
    }

    public ItemStack getDataHolderStack() {
        return this.upgradeManager.getUpgradesHandler().getStackInSlot(this.dataHolderSlot);
    }

    @Override
    public Point getTabSize() {
        if(isTabOpened()) {
            if(this instanceof IFilter filter) {
                int rowCount = (int)Math.ceil((double)filter.getFilterSlotCount() / 3);
                return new Point(this.openTabSize.x(), this.openTabSize.y() - 18 * (3 - rowCount));
            }
            return this.openTabSize;
        }
        return new Point(24, 24);
    }

    public void setCooldown(int cooldown) {
        ItemStack dataHolderStack = getDataHolderStack().copy();
        dataHolderStack.set(ModDataComponents.COOLDOWN, cooldown);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public int getCooldown() {
        return getDataHolderStack().getOrDefault(ModDataComponents.COOLDOWN, 100); //#TODO 0 jak cos tu
    }
}