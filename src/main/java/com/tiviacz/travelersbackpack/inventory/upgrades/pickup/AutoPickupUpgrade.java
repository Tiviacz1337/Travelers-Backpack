package com.tiviacz.travelersbackpack.inventory.upgrades.pickup;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class AutoPickupUpgrade extends FilterUpgradeBase<AutoPickupUpgrade, AutoPickupFilterSettings> implements IEnable {
    public AutoPickupUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, new Point(66, 49),
                TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.filterSlotCount.get(),
                TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.slotsInRow.get(), filter, filterTags);
    }

    public boolean canPickup(ItemStack stack) {
        return getFilterSettings().matchesFilter(null, stack) && isEnabled(this);
    }

    @Override
    public AutoPickupFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new AutoPickupFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter(), filterTags);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new AutoPickupWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    protected FilterHandler createFilter(NonNullList<ItemStack> stacks, int size) {
        return new FilterHandler(stacks, size) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(size, filter));

                getFilterSettings().updateFilter(getDataHolderStack().get(ModDataComponents.BACKPACK_CONTAINER).getItems());
                getFilterSettings().updateFilterTags(getDataHolderStack().get(ModDataComponents.FILTER_TAGS));
                changeListeners.forEach(Runnable::run);
            }
        };
    }
}