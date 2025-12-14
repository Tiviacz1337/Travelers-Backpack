package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.TrashSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VoidUpgrade extends FilterUpgradeBase<VoidUpgrade, VoidFilterSettings> implements IEnable {
    public VoidUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, new Point(66, 49),
                TravelersBackpackConfig.getConfig().backpackUpgrades.voidUpgradeSettings.filterSlotCount,
                TravelersBackpackConfig.getConfig().backpackUpgrades.voidUpgradeSettings.slotsInRow, filter, filterTags);
    }

    @Override
    public List<Integer> getFilter() {
        return getDataHolderStack().getOrDefault(ModDataComponents.FILTER_SETTINGS, List.of(0, 0, 1));
    }

    @Override
    public VoidFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new VoidFilterSettings(manager.getWrapper().getStorage(), filter.stream().skip(1).limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter(), filterTags);
    }

    public boolean canVoid(ItemStack stack) {
        return getFilterSettings().matchesFilter(null, stack) && isEnabled(this);
    }

    @Override
    public ItemStack getFirstFilterStack() {
        return this.filter.getStackInSlot(1);
    }

    public ItemStack getTrashSlotStack() {
        return this.filter.getStackInSlot(0);
    }

    public void voidTrashSlotStack() {
        this.filter.setStackInSlot(0, ItemStack.EMPTY.copy());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new VoidWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = super.getUpgradeSlots(menu, wrapper, x, y);
        if(!isTagSelector()) {
            slots.replaceAll(slot -> {
                if(slot.x == x + 7 && slot.y == y + 44) {
                    return new TrashSlot(this, this.filter, 0, x + 7, y + 44, 0);
                }
                return slot;
            });
        } else {
            slots.clear();
            slots.add(new TrashSlot(this, this.filter, 0, x + 7, y + 44, 0) {
                @Override
                public boolean isActive() {
                    return false;
                }

                @Override
                public boolean mayPlace(ItemStack pStack) {
                    return false;
                }
            });
            slots.add(new FilterSlotItemHandler(this, this.filter, 1, x + 64, y + 23, 2));
        }
        return slots;
    }

    @Override
    protected FilterHandler createFilter(NonNullList<ItemStack> stacks, int size) {
        return new FilterHandler(stacks, size) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataComponents.BACKPACK_CONTAINER, InventoryHelper.itemsToList(size, filter));

                getFilterSettings().updateFilter(getDataHolderStack().get(ModDataComponents.BACKPACK_CONTAINER).getItems());
                getFilterSettings().updateFilterTags(getDataHolderStack().get(ModDataComponents.FILTER_TAGS));
                changeListeners.forEach(Runnable::run);
            }

            @Override
            public int getSlotLimit(int slot) {
                if(slot == 0) {
                    return 64;
                }
                return 1;
            }
        };
    }
}