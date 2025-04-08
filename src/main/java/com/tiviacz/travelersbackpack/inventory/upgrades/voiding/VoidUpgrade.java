package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.TrashSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VoidUpgrade extends UpgradeBase<VoidUpgrade> implements IFilter, IEnable {
    public ItemStackHandler filter;
    private final VoidFilterSettings filterSettings;

    public VoidUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 103));
        this.filter = createFilter(filter);
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get();
        this.filterSettings = new VoidFilterSettings(manager.getWrapper().getStorage(), filter.stream().skip(1).limit(activeSlotCount).filter(stack -> !stack.isEmpty()).toList(), getFilter());
    }

    @Override
    public List<Integer> getFilter() {
        return getDataHolderStack().getOrDefault(ModDataComponents.FILTER_SETTINGS, List.of(0, 0, 1));
    }

    public VoidFilterSettings getFilterSettings() {
        return this.filterSettings;
    }

    public boolean canVoid(ItemStack stack) {
        return getFilterSettings().matchesFilter(null, stack) && isEnabled(this);
    }

    @Override
    public void updateSettings() {
        this.filterSettings.updateSettings(getFilter());
    }

    @Override
    public int getFilterSlotCount() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new VoidWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(j + i * 3 == 0) {
                    slots.add(new TrashSlot(this, this.filter, j + i * 3, x + 7 + j * 18, y + 44 + i * 18, activeSlotCount));
                } else {
                    slots.add(new FilterSlotItemHandler(this, this.filter, j + i * 3, x + 7 + j * 18, y + 44 + i * 18, activeSlotCount) {
                        @Override
                        public boolean isActive() {
                            return super.isActive() && getFilter().get(VoidFilterSettings.ALLOW_MODE) != VoidFilterSettings.MATCH_CONTENTS;
                        }

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

    private ItemStackHandler createFilter(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(9, filter));

                getFilterSettings().updateFilter(getDataHolderStack().get(ModDataComponents.BACKPACK_CONTAINER).getItems());
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
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