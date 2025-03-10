package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpgradeManager {
    public BackpackWrapper wrapper;
    public ItemStackHandler upgradesHandler;
    public Map<Integer, Optional<? extends IUpgrade<?>>> mappedUpgrades;
    public Map<Optional<? extends IUpgrade<?>>, Integer> slotMappedUpgrades;

    public List<UpgradeBase<?>> upgrades = new ArrayList<>();

    public static final byte LOAD_TANKS = 0;
    public static final byte LOAD_CRAFTING = 1;
    public static final byte LOAD_PICKUP = 2;
    public static final byte LOAD_JUKEBOX = 3;
    public static final byte LOAD_FEEDING = 4;
    public static final byte LOAD_MAGNET = 5;
    public static final byte LOAD_VOID = 6;

    //Load all
    public UpgradeManager(BackpackWrapper wrapper) {
        this(wrapper, List.of(LOAD_TANKS, LOAD_CRAFTING, LOAD_PICKUP, LOAD_JUKEBOX, LOAD_FEEDING, LOAD_MAGNET, LOAD_VOID));
    }

    public UpgradeManager(BackpackWrapper wrapper, List<Byte> dataLoad) {
        this.wrapper = wrapper;
        this.upgradesHandler = wrapper.getUpgrades();
        this.mappedUpgrades = new HashMap<>();
        this.slotMappedUpgrades = new HashMap<>();
        initializeUpgrades();
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public ItemStackHandler getUpgradesHandler() {
        return this.upgradesHandler;
    }

    public <T extends UpgradeBase> Optional<T> getUpgrade(Class<T> upgradeClass) {
        return upgrades.stream()
                .filter(upgradeClass::isInstance)
                .map(upgradeClass::cast)
                .findFirst();
    }

    public boolean addUpgrade(UpgradeBase upgrade) {
        if(upgrades.stream().noneMatch(u -> u.getClass().equals(upgrade.getClass()))) {
            return upgrades.add(upgrade);
        }
        return false;
    }

    public void invalidateUpgrade(int slot) {
        Optional<? extends IUpgrade> upgrade = this.mappedUpgrades.get(slot);

        //Error - item in slot is not an upgrade, just return
        if(upgrade == null) {
            return;
        }

        //Update upgrade tracker
        getWrapper().upgradesTracker.setStackInSlot(slot, ItemStack.EMPTY);

        upgrade.ifPresent(upg -> {
            this.mappedUpgrades.remove(slot);
            this.slotMappedUpgrades.remove(upgrade);
            upg.remove();
            upgrades.remove(upg);
        });
    }

    public void initializeUpgrades() {
        for(int i = 0; i < getUpgradesHandler().getSlots(); i++) {
            applyUpgrade(i);
        }
    }

    public void detectedChange(ItemStackHandler tracker, int slot) {
        boolean needsUpdate = false;
        boolean updateTabsOnly = true;

        if(applyUpgrade(slot)) {
            needsUpdate = true;
        }

        //Update whole inventory, because tanks add widgets on the sides of the inventory what changes slot positions
        if(getUpgradesHandler().getStackInSlot(slot).getItem() instanceof UpgradeItem upgradeItem) {
            if(upgradeItem.shouldUpdateAllSlots()) {
                updateTabsOnly = false;
            }
        }

        //Update if tab changed status
        if(getTabStatus(tracker.getStackInSlot(slot)) != getTabStatus(getUpgradesHandler().getStackInSlot(slot))) {
            needsUpdate = true;
            ItemStack stackToSet = getUpgradesHandler().getStackInSlot(slot).copy();
            tracker.setStackInSlot(slot, stackToSet);
        }

        //Recreate upgrades handler, mismatch of sizes
        if(getUpgradeCount() != this.mappedUpgrades.values().size()) {
            this.invalidateUpgrade(slot);
            updateTabsOnly = false;
        }

        //Update menu and screen
        if(needsUpdate) {
            getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
        }
    }

    public boolean applyUpgrade(int slot) {
        AtomicBoolean atomic = new AtomicBoolean(false);
        ItemStack upgradeStack = getUpgradesHandler().getStackInSlot(slot);
        if(upgradeStack.getItem() instanceof UpgradeItem upgradeItem) {
            upgradeItem.getUpgrade().apply(this, slot, upgradeStack).ifPresent(upgrade -> {
                if(addUpgrade(upgrade)) {
                    this.mappedUpgrades.put(slot, Optional.of(upgrade));
                    this.slotMappedUpgrades.put(Optional.of(upgrade), slot);
                    atomic.set(true);
                }
            });
        }
        return atomic.get();
    }

    public int getUpgradeCount() {
        int u = 0;
        for(int i = 0; i < getUpgradesHandler().getSlots(); i++) {
            if(!getUpgradesHandler().getStackInSlot(i).isEmpty()) {
                u++;
            }
        }
        return u;
    }

    public boolean getTabStatus(ItemStack stack) {
        return NbtHelper.getOrDefault(stack, ModDataHelper.TAB_OPEN, false);
    }

    public boolean hasTickingUpgrade() {
        return this.upgrades.stream()
                .filter(upgradeBase -> upgradeBase instanceof ITickableUpgrade && upgradeBase instanceof IEnable)
                .anyMatch(upgrade -> ((IEnable)upgrade).isEnabled());
    }
}