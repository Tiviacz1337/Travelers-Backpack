package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.world.item.ItemStack;

public abstract class UpgradeBase implements IUpgrade {
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
        return NbtHelper.getOrDefault(this.upgradeManager.getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.TAB_OPEN, false);
        // return this.upgradeManager.getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.TAB_OPEN.get(), false);
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
        NbtHelper.set(dataHolderStack, ModDataHelper.COOLDOWN, cooldown);
        //dataHolderStack.set(ModDataComponents.COOLDOWN.get(), cooldown);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public int getCooldown() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.COOLDOWN, 100);
    }
}