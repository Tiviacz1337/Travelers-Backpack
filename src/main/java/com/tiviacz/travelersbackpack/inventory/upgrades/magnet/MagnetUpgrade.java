package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

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
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MagnetUpgrade extends UpgradeBase<MagnetUpgrade> implements IFilter, IEnable, ITickableUpgrade {
    private static final int COOLDOWN = 30;
    public ItemStackHandler filter;
    private final MagnetFilterSettings filterSettings;

    public MagnetUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 103));
        this.filter = createFilter(filter);
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get();
        this.filterSettings = new MagnetFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(activeSlotCount).filter(stack -> !stack.isEmpty()).toList(), getFilter());
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
        //return NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.FILTER_SETTINGS, List.of(1, 0, 1));
        // return getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.FILTER_SETTINGS.get(), List.of(1, 0, 1));
    }

    public MagnetFilterSettings getFilterSettings() {
        return this.filterSettings;
    }

    @Override
    public boolean isEnabled() {
        return NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.UPGRADE_ENABLED, true);
        // return getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.UPGRADE_ENABLED.get(), true);
    }

    @Override
    public void updateSettings() {
        this.filterSettings.updateSettings(getFilter());
    }

    @Override
    public int getFilterSlotCount() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase createWidget(BackpackScreen screen, int x, int y) {
        return new MagnetWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                slots.add(new FilterSlotItemHandler(this, this.filter, j + i * 3, x + 7 + j * 18, y + 44 + i * 18, activeSlotCount) {
                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        return menu.getWrapper().isOwner(menu.player) && super.mayPlace(pStack);
                    }
                });
            }
        }
        return slots;
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(currentTick % getCooldown() != 0) {
            return;
        }
        teleportNearbyItems(player, level);
        setCooldown(COOLDOWN);
    }

    public void teleportNearbyItems(Player player, Level level) {
        if(isEnabled()) {
            if(level.isClientSide) return;
            int radius = TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.pullRange.get();
            AABB area = new AABB(player.position().add(-radius, -radius, -radius), player.position().add(radius, radius, radius));
            List<ItemEntity> items = level.getEntities(EntityType.ITEM, area,
                    item -> item.isAlive() && (!level.isClientSide || item.tickCount > 1) &&
                            (item.thrower == null || (!item.thrower.equals(player.getUUID()) || item.tickCount > 80)) &&
                            !item.getItem().isEmpty() && !item.getPersistentData().contains("PreventRemoteMovement") && this.getFilterSettings().canPickup(item.getItem()));
            items.forEach(item -> item.setPos(player.getX(), player.getY(), player.getZ()));
        }
    }

    private ItemStackHandler createFilter(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemStack stack = getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot());

                //Crash prevent for TS (???)
                if(stack.isEmpty()) return;

                NbtHelper.set(stack, ModDataHelper.BACKPACK_CONTAINER, filter);
                // stack.set(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(stacks.size(), filter));
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