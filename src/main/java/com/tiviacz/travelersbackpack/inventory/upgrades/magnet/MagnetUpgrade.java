package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.*;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetUpgrade extends FilterUpgradeBase<MagnetUpgrade, MagnetFilterSettings> implements IEnable, ITickableUpgrade {
    public MagnetUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, new Point(66, 49),
                TravelersBackpackConfig.getConfig().backpackUpgrades.magnetUpgradeSettings.filterSlotCount,
                TravelersBackpackConfig.getConfig().backpackUpgrades.magnetUpgradeSettings.slotsInRow, filter, filterTags);
    }

    @Override
    public MagnetFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new MagnetFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter(), filterTags);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new MagnetWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public int getTickRate() {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.magnetUpgradeSettings.tickRate;
    }

    public int getPullRange() {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.magnetUpgradeSettings.pullRange;
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(getCooldown() == 0) {
            return;
        }
        if(currentTick % getCooldown() != 0) {
            return;
        }
        if(getUpgradeManager().getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            teleportNearbyItems(Vec3.atBottomCenterOf(pos), level, null);
        } else {
            teleportNearbyItems(player.position(), level, player);
        }

        if(!hasCooldown() || getCooldown() != getTickRate()) {
            setCooldown(getTickRate());
        }
    }

    public void teleportNearbyItems(Vec3 pos, Level level, @Nullable Player player) {
        if(level.isClientSide()) return;
        int radius = getPullRange();
        AABB area = new AABB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
        List<ItemEntity> items = level.getEntities(EntityType.ITEM, area,
                item -> item.isAlive() && (!level.isClientSide() || item.tickCount > 1) &&
                        (item.getOwner() == null || (!item.getOwner().equals(player) || item.tickCount > 80)) &&
                        !item.getItem().isEmpty() /*!item.getEntityData().getPersistentData().contains("PreventRemoteMovement")*/ && this.getFilterSettings().matchesFilter(player, item.getItem()));
        items.forEach(item -> {
            if(player == null) {
                Direction backpackDirection = level.getBlockState(BlockPos.containing(pos)).getValue(TravelersBackpackBlock.FACING);
                item.setPos(pos.relative(backpackDirection, 1.0D));
            } else {
                item.setPos(pos.x(), pos.y(), pos.z());
            }
            item.setNoPickUpDelay();
        });
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
        };
    }
}