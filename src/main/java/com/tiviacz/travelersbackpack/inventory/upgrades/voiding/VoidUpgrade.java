package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public class VoidUpgrade extends FilterUpgradeBase<VoidUpgrade, VoidFilterSettings> implements IEnable {
    public VoidUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, new Point(66, 49),
                TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get(),
                TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.slotsInRow.get(), filter, filterTags);
    }

    @Override
    public List<Integer> getFilter() {
        return getDataHolderStack().getOrDefault(ModDataComponents.FILTER_SETTINGS, List.of(0, 0, 1));
    }

    @Override
    public VoidFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new VoidFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter(), filterTags, manager.getWrapper().getRegistryAccess());
    }

    public boolean canVoid(ItemStack stack) {
        return getFilterSettings().matchesFilter(null, stack) && isEnabled(this);
    }

    @Override
    protected FilterHandler createFilter(NonNullList<ItemStack> stacks, int size) {
        return new FilterHandler(stacks, size) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                updateDataHolderUnchecked(ModDataComponents.BACKPACK_CONTAINER.get(), ItemContainerContents.fromItems(filter.copyToList()));

                getFilterSettings().updateFilter(filter.copyToList());
                getFilterSettings().updateFilterTags(getDataHolderStack().get(ModDataComponents.FILTER_TAGS));
                changeListeners.forEach(Runnable::run);
            }
        };
    }
}