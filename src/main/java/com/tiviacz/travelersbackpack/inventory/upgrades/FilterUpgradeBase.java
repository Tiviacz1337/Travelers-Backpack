package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilterSlots;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterUpgradeBase<T, F extends FilterSettingsBase> extends UpgradeBase<T> implements IFilter, IFilterSlots {
    protected final ItemStackHandler filter;
    protected final List<Runnable> changeListeners = new ArrayList<>();
    private final int filterSlotCount;
    private final int slotsInRow;
    private final F filterSettings;

    public FilterUpgradeBase(UpgradeManager manager, int dataHolderSlot, Point openTabSize, int filterSlotCount, int slotsInRow, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, openTabSize);
        this.filterSlotCount = filterSlotCount;
        this.slotsInRow = slotsInRow;
        this.filterSettings = createFilterSettings(manager, filter, filterTags);
        this.filter = createFilter(filter, filterSlotCount);
    }

    public F getFilterSettings() {
        return this.filterSettings;
    }

    @Override
    public List<Integer> getFilter() {
        List<Integer> filter = NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.FILTER_SETTINGS, List.of(1, 0, 1));
        //Conversion error fix - #TODO to remove
        if(filter.size() != 3) {
            filter = List.of(1, 0, 1);
        }
        return filter;
    }

    @Override
    public int getFilterSlotCount() {
        return this.filterSlotCount;
    }

    @Override
    public int getSlotsInRow() {
        return this.slotsInRow;
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
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        if(isTagSelector()) {
            //Tag Selector
            positions.add(Pair.of(x + 64, y + 23));
        } else {
            //Filter Slots
            for(int i = 0; i < getRows(); i++) {
                for(int j = 0; j < getSlotsInRow(i); j++) {
                    positions.add(Pair.of(x + 7 + j * 18, y + 44 + i * 18));
                }
            }
        }
        return positions;
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        if(isTagSelector()) {
            //Tag Selector
            slots.add(new FilterSlotItemHandler(this, this.filter, 0, x + 64, y + 23, 1) {
                @Override
                public boolean mayPlace(ItemStack pStack) {
                    return menu.getWrapper().isOwner(menu.player) && super.mayPlace(pStack);
                }
            });
        } else {
            //Filter Slots
            for(int i = 0; i < getRows(); i++) {
                for(int j = 0; j < getSlotsInRow(i); j++) {
                    slots.add(new FilterSlotItemHandler(this, this.filter, j + i * getSlotsInRow(), x + 7 + j * 18, y + 44 + i * 18, getFilterSlotCount()) {
                        @Override
                        public boolean mayPlace(ItemStack pStack) {
                            return menu.getWrapper().isOwner(menu.player) && super.mayPlace(pStack);
                        }
                    });
                }
            }
        }
        return slots;
    }

    @Override
    public Point getTabSize() {
        Point tabSize = super.getTabSize();
        if(isTabOpened()) {
            if(isTagSelector()) {
                return new Point(87, 103);
            }
            int x = this.openTabSize.x();
            if(getSlotsInRow() > 3) {
                x += (getSlotsInRow() - 3) * 18;
            }
            return new Point(x, this.openTabSize.y() + (18 * getRows())); //+18 has buttons
        }
        return tabSize;
    }

    public abstract F createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags);

    protected abstract FilterHandler createFilter(NonNullList<ItemStack> filter, int size);
}