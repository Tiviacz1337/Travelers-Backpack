package com.tiviacz.travelersbackpack.inventory.upgrades.pickup;

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
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoPickupUpgrade extends FilterUpgradeBase<AutoPickupUpgrade, AutoPickupFilterSettings> implements IEnable, ITickableUpgrade {
    public static final double PICKUP_RANGE = 1.0D;

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
    public int getTickRate() {
        return 5;
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(getUpgradeManager().getWrapper().getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID) {
            return;
        }
        if(getCooldown() == 0) {
            return;
        }
        if(currentTick % getCooldown() != 0) {
            return;
        }

        //Collect items
        level.getEntities(EntityType.ITEM, new AABB(pos).inflate(PICKUP_RANGE), item -> this.canPickup(item.getItem())).forEach(item -> tryPickup(item, level, pos));

        if(!hasCooldown() || getCooldown() != getTickRate()) {
            setCooldown(getTickRate());
        }
    }

    //Always check canPickup before
    public boolean tryPickup(ItemEntity itemEntity, Level level, BlockPos pos) {
        ItemStack stack = itemEntity.getItem().copy();
        stack = ItemHandlerHelper.insertItemStacked(getUpgradeManager().getWrapper().getStorageForInputOutput(), stack, false);
        if(stack != itemEntity.getItem()) {
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F);
            itemEntity.setItem(stack);
            return true;
        }
        return false;
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