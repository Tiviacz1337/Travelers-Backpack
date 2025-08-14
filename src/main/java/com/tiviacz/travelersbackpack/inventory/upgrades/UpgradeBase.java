package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

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

    public void updateDataHolderUnchecked(Consumer<ItemStack> updater) {
        ItemStack dataHolderStack = getDataHolderStack().copy();

        //TS fix prevent
        if(dataHolderStack.isEmpty()) return;

        updater.accept(dataHolderStack);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public <D> void updateDataHolderUnchecked(DataComponentType<D> dataKey, D value) {
        ItemStack dataHolderStack = getDataHolderStack().copy();

        //TS fix prevent
        if(dataHolderStack.isEmpty()) return;

        dataHolderStack.set(dataKey, value);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public void setCooldown(int cooldown) {
        ItemStack dataHolderStack = getDataHolderStack().copy();
        dataHolderStack.set(ModDataComponents.COOLDOWN, cooldown);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public int getCooldown() {
        return getDataHolderStack().getOrDefault(ModDataComponents.COOLDOWN, 100);
    }

    public boolean hasCooldown() {
        return getDataHolderStack().has(ModDataComponents.COOLDOWN);
    }
}