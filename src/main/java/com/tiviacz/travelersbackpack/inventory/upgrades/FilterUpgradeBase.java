package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterUpgradeBase<T, F extends FilterSettingsBase> extends UpgradeBase<T> implements IFilter {
    protected final ItemStackHandler filter;
    protected final List<Runnable> changeListeners = new ArrayList<>();
    private final int filterSlotCount;
    private final F filterSettings;

    public FilterUpgradeBase(UpgradeManager manager, int dataHolderSlot, Point openTabSize, int filterSlotCount, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, openTabSize);
        this.filterSlotCount = filterSlotCount;
        this.filterSettings = createFilterSettings(manager, filter, filterTags);
        this.filter = createFilter(filter);
    }

    public F getFilterSettings() {
        return this.filterSettings;
    }

    @Override
    public List<Integer> getFilter() {
        return getDataHolderStack().getOrDefault(ModDataComponents.FILTER_SETTINGS, List.of(1, 0, 1));
    }

    @Override
    public int getFilterSlotCount() {
        return this.filterSlotCount;
    }

    @Override
    public void updateSettings() {
        getFilterSettings().updateSettings(getFilter());
    }

    public boolean hasTagSelector() {
        return true;
    }

    public boolean isTagSelector() {
        if(!hasTagSelector()) {
            return false;
        }
        return getFilter().get(1) == 2;
    }

    public ItemStack getFirstFilterStack() {
        return this.filter.getStackInSlot(0);
    }

    public void addChangeListener(Runnable listener) {
        if(!this.changeListeners.contains(listener)) {
            this.changeListeners.add(listener);
        }
    }

    @Override
    public Point getTabSize() {
        Point tabSize = super.getTabSize();
        if(isTabOpened()) {
            if(isTagSelector()) {
                return new Point(tabSize.x() + 21, tabSize.y());
            }
        }
        return tabSize;
    }


    public abstract F createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags);

    protected abstract ItemStackHandler createFilter(NonNullList<ItemStack> filter);
}