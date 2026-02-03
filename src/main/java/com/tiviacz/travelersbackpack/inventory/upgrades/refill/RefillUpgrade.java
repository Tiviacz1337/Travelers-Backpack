package com.tiviacz.travelersbackpack.inventory.upgrades.refill;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
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
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilterSlots;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RefillUpgrade extends UpgradeBase<RefillUpgrade> implements IEnable, ITickableUpgrade, IFilterSlots {
    public static final double REFILL_RANGE = 3.0D;
    private final FilterHandler filter;

    public RefillUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 28));
        this.filter = createFilter(filter, getFilterSlotCount());
    }

    @Override
    public int getFilterSlotCount() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.refillUpgradeSettings.filterSlotCount.get();
    }

    @Override
    public int getSlotsInRow() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.refillUpgradeSettings.slotsInRow.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new UpgradeWidgetBase<>(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), new Point(137, 0), "screen.travelersbackpack.refill_upgrade");
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        for(int i = 0; i < getRows(); i++) {
            for(int j = 0; j < getSlotsInRow(i); j++) {
                positions.add(Pair.of(x + 7 + j * 18, y + 23 + i * 18));
            }
        }
        return positions;
    }

    @Override
    public List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        for(int i = 0; i < getRows(); i++) {
            for(int j = 0; j < getSlotsInRow(i); j++) {
                slots.add(new FilterSlotItemHandler(this, this.filter, j + i * getSlotsInRow(), x + 7 + j * 18, y + 23 + i * 18, getFilterSlotCount()) {
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
        if(getCooldown() == 0) {
            return;
        }
        if(currentTick % getCooldown() != 0) {
            return;
        }

        if(getUpgradeManager().getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            level.getEntities(EntityType.PLAYER, new AABB(pos).inflate(REFILL_RANGE), p -> true).forEach(p -> p.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((playerInvHandler -> tryRefillItems(playerInvHandler, p))));
        } else {
            player.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((playerInvHandler -> tryRefillItems(playerInvHandler, player)));
        }

        if(!hasCooldown() || getCooldown() != getTickRate()) {
            setCooldown(getTickRate());
        }
    }

    public void tryRefillItems(IItemHandler playerInv, Player player) {
        InventoryHelper.iterateHandler(this.filter, (slot, filterStack) -> {
            if(!filterStack.isEmpty()) {
                refill(playerInv, player, filterStack);
            }
        });
    }

    public void refill(IItemHandler playerInv, Player player, ItemStack filterStack) {
        Pair<Integer, Integer> pair = countAndSupply(playerInv, filterStack, player); //Current Count and Slot
        int missingCount = getMissingCount(filterStack, pair.getFirst());
        if(pair.getFirst() >= filterStack.getMaxStackSize() || missingCount <= 0) {
            return;
        }

        boolean standalone = false;
        if(pair.getFirst() == 0) {
            int count = pair.getFirst();
            pair = Pair.of(count, player.getInventory().getFreeSlot());
            standalone = true;
        }

        if(pair.getSecond() == -1) {
            return;
        }

        //Load storage if not loaded in artificial wrapper
        getUpgradeManager().getWrapper().loadAdditionally(BackpackWrapper.STORAGE_ID);

        //Extract the missing count from backpack
        ItemStackHandler backpackStorage = upgradeManager.getWrapper().getStorage();
        ItemStack extracted = InventoryHelper.extractFromBackpack(backpackStorage, filterStack, missingCount, true);

        if(extracted.isEmpty()) {
            return;
        }

        boolean addItem = true;
        int extractedCount = extracted.getCount();

        if(!standalone) {
            player.getInventory().getItem(pair.getSecond()).grow(extractedCount);
        } else {
            addItem = player.addItem(extracted);
        }

        //Actually remove the items from backpack
        if(addItem) {
            InventoryHelper.extractFromBackpack(backpackStorage, filterStack, extractedCount, false);
        }
    }

    private Pair<Integer, Integer> countAndSupply(IItemHandler playerInv, ItemStack filterStack, Player player) {
        AtomicInteger count = new AtomicInteger();
        AtomicInteger supplySlot = new AtomicInteger(-1);
        InventoryHelper.iterateHandler(playerInv, (slot, stack) -> {
            if(ItemStack.isSameItemSameTags(stack, filterStack)) {
                if(supplySlot.get() == -1) {
                    supplySlot.set(slot);
                }
                count.addAndGet(stack.getCount());
            }
        });
        if(player.containerMenu != null) {
            if(ItemStack.isSameItemSameTags(player.containerMenu.getCarried(), filterStack)) {
                count.addAndGet(player.containerMenu.getCarried().getCount());
            }
        }
        return Pair.of(count.get(), supplySlot.get());
    }

    private int getMissingCount(ItemStack filterStack, int count) {
        return filterStack.getMaxStackSize() - count;
    }

    @Override
    public int getTickRate() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.refillUpgradeSettings.tickRate.get();
    }

    protected FilterHandler createFilter(NonNullList<ItemStack> stacks, int size) {
        return new FilterHandler(stacks, size) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataHelper.BACKPACK_CONTAINER, filter);
            }
        };
    }
}