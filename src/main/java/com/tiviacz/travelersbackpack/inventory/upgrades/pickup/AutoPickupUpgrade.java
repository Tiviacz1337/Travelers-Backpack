package com.tiviacz.travelersbackpack.inventory.upgrades.pickup;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AutoPickupUpgrade extends UpgradeBase<AutoPickupUpgrade> implements IFilter, IEnable {
    public ItemStackHandler filter;
    private final AutoPickupFilterSettings filterSettings;

    public AutoPickupUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 103));
        this.filter = createFilter(filter);
        int activeSlotCount = TravelersBackpackConfig.getConfig().backpackUpgrades.pickupUpgradeSettings.filterSlotCount;
        this.filterSettings = new AutoPickupFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(activeSlotCount).filter(stack -> !stack.isEmpty()).toList(), getFilter());
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

    public AutoPickupFilterSettings getFilterSettings() {
        return this.filterSettings;
    }

    public boolean canPickup(ItemStack stack) {
        return getFilterSettings().matchesFilter(null, stack) && isEnabled(this);
    }

    @Override
    public void updateSettings() {
        this.filterSettings.updateSettings(getFilter());
    }

    @Override
    public int getFilterSlotCount() {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.pickupUpgradeSettings.filterSlotCount;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new AutoPickupWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        int activeSlotCount = TravelersBackpackConfig.getConfig().backpackUpgrades.pickupUpgradeSettings.filterSlotCount;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                slots.add(new FilterSlotItemHandler(this, this.filter, j + i * 3, x + 7 + j * 18, y + 44 + i * 18, activeSlotCount) {
                    @Override
                    public boolean isActive() {
                        return super.isActive() && getFilter().get(AutoPickupFilterSettings.ALLOW_MODE) != AutoPickupFilterSettings.MATCH_CONTENTS;
                    }

                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        return menu.getWrapper().isOwner(menu.player) && super.mayPlace(pStack);
                    }
                });
            }
        }
        return slots;
    }

    private ItemStackHandler createFilter(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataHelper.BACKPACK_CONTAINER, filter);

                getFilterSettings().updateFilter(NbtHelper.get(getDataHolderStack(), ModDataHelper.BACKPACK_CONTAINER));
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }
}