package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.*;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FilterSlotItemHandler;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MagnetUpgrade extends FilterUpgradeBase<MagnetUpgrade, MagnetFilterSettings> implements IEnable, ITickableUpgrade {
    public MagnetUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, new Point(66, 103), TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get(), filter, filterTags);
    }

    @Override
    public MagnetFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new MagnetFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter(), filterTags);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new MagnetWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        int activeSlotCount = TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get();
        if(isTagSelector()) {
            slots.add(new FilterSlotItemHandler(this, this.filter, 0, x + 64, y + 23, 1) {
                @Override
                public boolean isActive() {
                    return super.isActive();
                }

                @Override
                public boolean mayPlace(ItemStack pStack) {
                    return menu.getWrapper().isOwner(menu.player) && super.mayPlace(pStack);
                }
            });
        } else {
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
        }
        return slots;
    }

    @Override
    public int getTickRate() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.tickRate.get();
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(getCooldown() == 0) {
            return;
        }
        if(currentTick % getCooldown() != 0) {
            return;
        }
        teleportNearbyItems(player, level);

        if(!hasCooldown() || getCooldown() != getTickRate()) {
            setCooldown(getTickRate());
        }
    }

    public void teleportNearbyItems(Player player, Level level) {
        if(level.isClientSide) return;
        int radius = TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.pullRange.get();
        AABB area = new AABB(player.position().add(-radius, -radius, -radius), player.position().add(radius, radius, radius));
        List<ItemEntity> items = level.getEntities(EntityType.ITEM, area,
                item -> item.isAlive() && (!level.isClientSide || item.tickCount > 1) &&
                        (item.thrower == null || (!item.thrower.equals(player.getUUID()) || item.tickCount > 80)) &&
                        !item.getItem().isEmpty() && !item.getPersistentData().contains("PreventRemoteMovement") && this.getFilterSettings().matchesFilter(player, item.getItem()));
        items.forEach(item -> {
            item.setPos(player.getX(), player.getY(), player.getZ());
            item.setNoPickUpDelay();
        });
    }

    @Override
    protected ItemStackHandler createFilter(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(stacks.size(), filter));

                getFilterSettings().updateFilter(getDataHolderStack().get(ModDataComponents.BACKPACK_CONTAINER).getItems());
                getFilterSettings().updateFilterTags(getDataHolderStack().get(ModDataComponents.FILTER_TAGS));
                changeListeners.forEach(Runnable::run);
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