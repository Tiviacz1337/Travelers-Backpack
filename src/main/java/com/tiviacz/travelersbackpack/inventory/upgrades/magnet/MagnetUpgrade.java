package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetUpgrade extends FilterUpgradeBase<MagnetUpgrade, MagnetFilterSettings> implements IEnable, ITickableUpgrade {
    public MagnetUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter, List<String> filterTags) {
        super(manager, dataHolderSlot, new Point(66, 49),
                TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get(),
                TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.slotsInRow.get(), filter, filterTags);
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
    public int getTickRate() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.tickRate.get();
    }

    public int getPullRange() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.pullRange.get();
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
        if(level.isClientSide) return;
        int radius = getPullRange();
        AABB area = new AABB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
        List<ItemEntity> items = level.getEntities(EntityType.ITEM, area,
                item -> item.isAlive() && (!level.isClientSide || item.tickCount > 1) &&
                        (item.thrower == null || (!item.thrower.equals(player == null ? null : player.getUUID()) || item.tickCount > 80)) &&
                        !item.getItem().isEmpty() && !item.getPersistentData().contains("PreventRemoteMovement") && this.getFilterSettings().matchesFilter(player, item.getItem()));
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
                updateDataHolderUnchecked(ModDataHelper.BACKPACK_CONTAINER, filter);

                getFilterSettings().updateFilter(NbtHelper.get(getDataHolderStack(), ModDataHelper.BACKPACK_CONTAINER));
                getFilterSettings().updateFilterTags(NbtHelper.get(getDataHolderStack(), ModDataHelper.FILTER_TAGS));
                changeListeners.forEach(Runnable::run);
            }
        };
    }
}