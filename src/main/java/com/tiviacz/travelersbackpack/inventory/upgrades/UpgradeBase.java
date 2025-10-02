package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilterSlots;
import com.tiviacz.travelersbackpack.util.NbtHelper;
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
        return NbtHelper.getOrDefault(this.upgradeManager.getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.TAB_OPEN, false);
    }

    public ItemStack getDataHolderStack() {
        return this.upgradeManager.getUpgradesHandler().getStackInSlot(this.dataHolderSlot);
    }

    @Override
    public Point getTabSize() {
        if(isTabOpened()) {
            if(this instanceof IFilterSlots slots) {
                int x = this.openTabSize.x();
                if(slots.getSlotsInRow() > 3) {
                    x += (slots.getSlotsInRow() - 3) * 18;
                }
                return new Point(x, this.openTabSize.y() + (18 * slots.getRows()));
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

    public void updateDataHolderUnchecked(String dataKey, Object value) {
        ItemStack dataHolderStack = getDataHolderStack().copy();

        //TS fix prevent
        if(dataHolderStack.isEmpty()) return;

        NbtHelper.set(dataHolderStack, dataKey, value);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public void setCooldown(int cooldown) {
        ItemStack dataHolderStack = getDataHolderStack().copy();
        NbtHelper.set(dataHolderStack, ModDataHelper.COOLDOWN, cooldown);
        getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), dataHolderStack);
    }

    public int getCooldown() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.COOLDOWN, 100);
    }

    public boolean hasCooldown() {
        return NbtHelper.has(getDataHolderStack(), ModDataHelper.COOLDOWN);
    }
}