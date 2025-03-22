package com.tiviacz.travelersbackpack.inventory.upgrades.pickup;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AutoPickupUpgrade extends UpgradeBase<AutoPickupUpgrade> implements IFilter, IEnable {
    public ItemStackHandler filter;
    private final AutoPickupFilterSettings filterSettings;

    public AutoPickupUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 103));
        this.filter = createFilter(filter);
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.filterSlotCount.get();
        this.filterSettings = new AutoPickupFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(activeSlotCount).filter(stack -> !stack.isEmpty()).toList(), getFilter());
    }

    @Override
    public List<Integer> getFilter() {
        List<Integer> filter = NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.FILTER_SETTINGS, List.of(1, 0, 1));
        //Conversion error fix - #TODO to remove
        if(filter.size() != 3) {
            NbtHelper.remove(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.FILTER_SETTINGS);
            filter = List.of(1, 0, 1);
        }
        return filter;
    }

    public AutoPickupFilterSettings getFilterSettings() {
        return this.filterSettings;
    }

    public boolean canPickup(ItemStack stack) {
        return getFilterSettings().canPickup(stack) && isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.UPGRADE_ENABLED, true);
    }

    @Override
    public void updateSettings() {
        this.filterSettings.updateSettings(getFilter());
    }

    @Override
    public int getFilterSlotCount() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.filterSlotCount.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase createWidget(BackpackScreen screen, int x, int y) {
        return new AutoPickupWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.filterSlotCount.get();
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
                ItemStack stack = getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot());

                //Crash prevent for TS (???)
                if(stack.isEmpty()) return;

                NbtHelper.set(stack, ModDataHelper.BACKPACK_CONTAINER, filter);
                //  stack.set(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(9, filter));
                getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), stack);

                getFilterSettings().updateFilter(NbtHelper.get(stack, ModDataHelper.BACKPACK_CONTAINER));
                // getFilterSettings().updateFilter(stack.get(ModDataComponents.BACKPACK_CONTAINER.get()).getItems());
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }
}