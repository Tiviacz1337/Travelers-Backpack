package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.feeding.FeedingUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.jukebox.JukeboxUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.magnet.MagnetUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.pickup.AutoPickupUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UpgradeManager {

    public BackpackWrapper wrapper;
    public ItemStackHandler upgradesHandler;
    public Map<Integer, Optional<? extends IUpgrade>> mappedUpgrades;
    public Map<Optional<? extends IUpgrade>, Integer> slotMappedUpgrades;

    public Optional<TanksUpgrade> tanksUpgrade = Optional.empty();
    public Optional<CraftingUpgrade> craftingUpgrade = Optional.empty();
    public Optional<AutoPickupUpgrade> pickupUpgrade = Optional.empty();
    public Optional<JukeboxUpgrade> jukeboxUpgrade = Optional.empty();
    public Optional<FeedingUpgrade> feedingUpgrade = Optional.empty();
    public Optional<MagnetUpgrade> magnetUpgrade = Optional.empty();
    public Optional<VoidUpgrade> voidUpgrade = Optional.empty();

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
        initializeUpgrades(dataLoad);
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public ItemStackHandler getUpgradesHandler() {
        return this.upgradesHandler;
    }

    public void initializeUpgrades(List<Byte> dataLoad) {
        for(int i = 0; i < getUpgradesHandler().getSlots(); i++) {
            if(dataLoad.contains(LOAD_TANKS) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.TANKS_UPGRADE && this.tanksUpgrade.isEmpty()) {
                this.createTanksUpgrade(i);
            }

            if(dataLoad.contains(LOAD_CRAFTING) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.CRAFTING_UPGRADE && this.craftingUpgrade.isEmpty()) {
                this.createCraftingUpgrade(i);
            }

            if(dataLoad.contains(LOAD_PICKUP) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.PICKUP_UPGRADE && this.pickupUpgrade.isEmpty()) {
                this.createAutoPickupUpgrade(i);
            }

            if(dataLoad.contains(LOAD_JUKEBOX) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.JUKEBOX_UPGRADE && this.jukeboxUpgrade.isEmpty()) {
                this.createJukeboxUpgrade(i);
            }

            if(dataLoad.contains(LOAD_FEEDING) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.FEEDING_UPGRADE && this.feedingUpgrade.isEmpty()) {
                this.createFeedingUpgrade(i);
            }

            if(dataLoad.contains(LOAD_VOID) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.VOID_UPGRADE && this.voidUpgrade.isEmpty()) {
                this.createVoidUpgrade(i);
            }

            if(dataLoad.contains(LOAD_MAGNET) && getUpgradesHandler().getStackInSlot(i).getItem() == ModItems.MAGNET_UPGRADE && this.magnetUpgrade.isEmpty()) {
                this.createMagnetUpgrade(i);
            }
        }
    }

    public void detectedChange(ItemStackHandler tracker, int slot) {
        boolean needsUpdate = false;
        boolean updateTabsOnly = true;

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.TANKS_UPGRADE) {
            if(this.tanksUpgrade.isEmpty()) {
                this.createTanksUpgrade(slot);
                updateTabsOnly = false;
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.CRAFTING_UPGRADE) {
            if(this.craftingUpgrade.isEmpty()) {
                this.createCraftingUpgrade(slot);
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.PICKUP_UPGRADE) {
            if(this.pickupUpgrade.isEmpty()) {
                this.createAutoPickupUpgrade(slot);
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.JUKEBOX_UPGRADE) {
            if(this.jukeboxUpgrade.isEmpty()) {
                this.createJukeboxUpgrade(slot);
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.FEEDING_UPGRADE) {
            if(this.feedingUpgrade.isEmpty()) {
                this.createFeedingUpgrade(slot);
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.VOID_UPGRADE) {
            if(this.voidUpgrade.isEmpty()) {
                this.createVoidUpgrade(slot);
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        if(getUpgradesHandler().getStackInSlot(slot).getItem() == ModItems.MAGNET_UPGRADE) {
            if(this.magnetUpgrade.isEmpty()) {
                this.createMagnetUpgrade(slot);
                needsUpdate = true;
            }

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        //Update if tab changed status
        if(getTabStatus(tracker.getStackInSlot(slot)) != getTabStatus(getUpgradesHandler().getStackInSlot(slot))) {
            needsUpdate = true;
            ItemStack stackToSet = getUpgradesHandler().getStackInSlot(slot).copy();
            tracker.setStackInSlot(slot, stackToSet);

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }

        //Recreate upgrades handler, mismatch of sizes
        if(getUpgradeCount() != this.mappedUpgrades.values().size()) {
            this.invalidateUpgrade(slot);
            updateTabsOnly = false;

            if(needsUpdate) {
                getWrapper().requestMenuAndScreenUpdate(updateTabsOnly);
            }
        }
    }

    public void createCraftingUpgrade(int i) {
        BackpackContainerContents contents = getUpgradesHandler().getStackInSlot(i).getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
        craftingUpgrade = Optional.of(new CraftingUpgrade(this, i, contents.getItems()));
        this.mappedUpgrades.put(i, craftingUpgrade);
        this.slotMappedUpgrades.put(craftingUpgrade, i);
    }

    public void createTanksUpgrade(int slot) {
        Fluids fluids = getUpgradesHandler().getStackInSlot(slot).getOrDefault(ModDataComponents.FLUIDS, new Fluids(FluidVariantWrapper.blank(), FluidVariantWrapper.blank()));
        tanksUpgrade = Optional.of(new TanksUpgrade(this, slot, fluids));
        this.mappedUpgrades.put(slot, tanksUpgrade);
        this.slotMappedUpgrades.put(tanksUpgrade, slot);
    }

    public void createAutoPickupUpgrade(int slot) {
        BackpackContainerContents filter = getUpgradesHandler().getStackInSlot(slot).getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
        pickupUpgrade = Optional.of(new AutoPickupUpgrade(this, slot, filter.getItems()));
        this.mappedUpgrades.put(slot, pickupUpgrade);
        this.slotMappedUpgrades.put(pickupUpgrade, slot);
    }

    public void createJukeboxUpgrade(int slot) {
        BackpackContainerContents musicDisk = getUpgradesHandler().getStackInSlot(slot).getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(1));
        jukeboxUpgrade = Optional.of(new JukeboxUpgrade(this, slot, musicDisk.getItems()));
        this.mappedUpgrades.put(slot, jukeboxUpgrade);
        this.slotMappedUpgrades.put(jukeboxUpgrade, slot);
    }

    public void createFeedingUpgrade(int slot) {
        BackpackContainerContents filter = getUpgradesHandler().getStackInSlot(slot).getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
        feedingUpgrade = Optional.of(new FeedingUpgrade(this, slot, filter.getItems()));
        this.mappedUpgrades.put(slot, feedingUpgrade);
        this.slotMappedUpgrades.put(feedingUpgrade, slot);
    }

    public void createMagnetUpgrade(int slot) {
        BackpackContainerContents filter = getUpgradesHandler().getStackInSlot(slot).getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
        magnetUpgrade = Optional.of(new MagnetUpgrade(this, slot, filter.getItems()));
        this.mappedUpgrades.put(slot, magnetUpgrade);
        this.slotMappedUpgrades.put(magnetUpgrade, slot);
    }

    public void createVoidUpgrade(int slot) {
        BackpackContainerContents filter = getUpgradesHandler().getStackInSlot(slot).getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
        filter = filter.updateSlot(new BackpackContainerContents.Slot(0, ItemStack.EMPTY.copy())); //#TODO TO REMOVE IN THE FUTURE, KEEP IT NOW TO PREVENT DUPLICATION WHILE UPDATING FROM PREV VERSION
        voidUpgrade = Optional.of(new VoidUpgrade(this, slot, filter.getItems()));
        this.mappedUpgrades.put(slot, voidUpgrade);
        this.slotMappedUpgrades.put(voidUpgrade, slot);
    }

    public void invalidateUpgrade(int slot) {
        Optional<? extends IUpgrade> upgrade = this.mappedUpgrades.get(slot);

        //Update upgrade tracker
        getWrapper().upgradesTracker.setStackInSlot(slot, ItemStack.EMPTY);

        //Error - item in slot is not an upgrade, just return
        if(upgrade == null) {
            return;
        }

        if(upgrade.isPresent()) {
            this.mappedUpgrades.remove(slot);
            this.slotMappedUpgrades.remove(upgrade);
            upgrade.get().remove();
        }
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
        return stack.getOrDefault(ModDataComponents.TAB_OPEN, false);
    }

    public boolean hasTickingUpgrade() {
        boolean hasTickingUpgrade = false;
        if(this.magnetUpgrade.isPresent()) {
            if(this.magnetUpgrade.get().isEnabled()) {
                hasTickingUpgrade = true;
            }
        }
        if(this.feedingUpgrade.isPresent()) {
            if(this.feedingUpgrade.get().isEnabled()) {
                hasTickingUpgrade = true;
            }
        }
        return hasTickingUpgrade;
    }
}
