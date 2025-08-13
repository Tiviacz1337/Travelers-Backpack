package com.tiviacz.travelersbackpack.inventory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpgradeManager {
    public final BackpackWrapper wrapper;
    public final ItemStackHandler upgradesHandler;
    public BiMap<Integer, Optional<UpgradeBase<?>>> mappedUpgrades;
    public List<UpgradeBase<?>> upgrades = new ArrayList<>();

    public UpgradeManager(BackpackWrapper wrapper) {
        this.wrapper = wrapper;
        this.upgradesHandler = wrapper.getUpgrades();
        this.mappedUpgrades = HashBiMap.create();
        initializeUpgrades();
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public ItemStackHandler getUpgradesHandler() {
        return this.upgradesHandler;
    }

    public boolean hasUpgradeInSlot(int slot) {
        return this.mappedUpgrades.containsKey(slot);
    }

    public <T extends UpgradeBase<T>> Optional<T> getUpgrade(Class<T> upgradeClass) {
        return upgrades.stream()
                .filter(upgradeClass::isInstance)
                .map(upgradeClass::cast)
                .findFirst();
    }

    public boolean canAddUpgrade(UpgradeItem upgradeItem) {
        return upgrades.stream().noneMatch(u -> u.getClass().equals(upgradeItem.getUpgradeClass()));
    }

    public boolean invalidateUpgrade(int slot) {
        Optional<UpgradeBase<?>> upgrade = this.mappedUpgrades.get(slot);

        //Update upgrade tracker
        getWrapper().upgradesTracker.setStackInSlot(slot, ItemStack.EMPTY);

        //Error - item in slot is not an upgrade, just return
        if(upgrade == null) {
            return false;
        }

        upgrade.ifPresent(upg -> {
            this.mappedUpgrades.remove(slot);
            upg.remove();
            upgrades.remove(upg);
        });
        return true;
    }

    public void initializeUpgrades() {
        for(int i = 0; i < getUpgradesHandler().getSlots(); i++) {
            applyUpgrade(i);
        }
    }

    public void detectedChange(ItemStackHandler tracker, int slot) {
        boolean needsUpdate = applyUpgrade(slot);

        //Update if tab changed status
        if(getTabStatus(tracker.getStackInSlot(slot)) != getTabStatus(getUpgradesHandler().getStackInSlot(slot)) || isTagSelector(getUpgradesHandler().getStackInSlot(slot), tracker.getStackInSlot(slot))) {
            needsUpdate = true;
            ItemStack stackToSet = getUpgradesHandler().getStackInSlot(slot).copy();
            tracker.setStackInSlot(slot, stackToSet);
        }

        if(mappedUpgrades.containsKey(slot)) {
            if(!(getUpgradesHandler().getStackInSlot(slot).getItem() instanceof UpgradeItem)) {
                needsUpdate = this.invalidateUpgrade(slot);
            }
        }

        //Update menu and screen
        if(needsUpdate) {
            if(!getWrapper().getPlayersUsing().isEmpty()) {
                getWrapper().getPlayersUsing().stream().filter(player -> !player.level().isClientSide).forEach(player -> player.containerMenu.broadcastChanges());
            }
            getWrapper().requestMenuAndScreenUpdate();
        }
    }

    public boolean applyUpgrade(int slot) {
        AtomicBoolean atomic = new AtomicBoolean(false);
        ItemStack upgradeStack = getUpgradesHandler().getStackInSlot(slot);
        if(upgradeStack.getItem() instanceof UpgradeItem upgradeItem) {
            if(canAddUpgrade(upgradeItem)) {
                upgradeItem.getUpgrade().apply(this, slot, upgradeStack).ifPresent(upgrade -> {
                    this.upgrades.add(upgrade);
                    this.mappedUpgrades.put(slot, Optional.of(upgrade));
                    atomic.set(true);
                });
            }
        }
        return atomic.get();
    }

    public boolean getTabStatus(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TAB_OPEN, false);
    }

    public boolean isTagSelector(ItemStack current, ItemStack tracker) {
        return isTagSelector(current) ^ isTagSelector(tracker);
    }

    public boolean isTagSelector(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FILTER_SETTINGS, List.of(1, 0, 1)).get(1) == 2;
    }

    public boolean hasTickingUpgrade() {
        return this.upgrades.stream()
                .filter(upgradeBase -> upgradeBase instanceof ITickableUpgrade && upgradeBase instanceof IEnable)
                .anyMatch(upgrade -> ((IEnable)upgrade).isEnabled(upgrade));
    }
}