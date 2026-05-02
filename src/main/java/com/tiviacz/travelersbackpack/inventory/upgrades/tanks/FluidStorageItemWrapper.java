package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public class FluidStorageItemWrapper implements SingleSlotStorage<FluidVariant> {
    private final ContainerItemContext context;
    private final boolean leftTank;

    public FluidStorageItemWrapper(ContainerItemContext context, boolean leftTank) {
        this.context = context;
        this.leftTank = leftTank;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack currentStack = context.getItemVariant().toStack();
        ItemStack modifiedStack = currentStack.copy();

        BackpackWrapper backpackWrapper = BackpackWrapper.fromStack(modifiedStack);
        var tanksUpgrade = backpackWrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class);

        if(tanksUpgrade.isPresent()) {
            FluidTank tank = getTank(tanksUpgrade.get());
            long inserted = tank.insert(resource, maxAmount, transaction);
            tank.saveToStack(); //Save changes to the stack copy

            if(inserted > 0) {
                ItemVariant newVariant = ItemVariant.of(modifiedStack);
                if(context.exchange(newVariant, 1, transaction) == 1) {
                    return inserted;
                }
            }
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack currentStack = context.getItemVariant().toStack();
        ItemStack modifiedStack = currentStack.copy();

        BackpackWrapper backpackWrapper = BackpackWrapper.fromStack(modifiedStack);
        var tanksUpgrade = backpackWrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class);

        if(tanksUpgrade.isPresent()) {
            FluidTank tank = getTank(tanksUpgrade.get());
            long extracted = tank.extract(resource, maxAmount, transaction);

            if(extracted > 0) {
                tank.saveToStack(); //Save changes to the stack copy
                ItemVariant newVariant = ItemVariant.of(modifiedStack);
                if(context.exchange(newVariant, 1, transaction) == 1) {
                    return extracted;
                }
            }
        }
        return 0;
    }

    public TanksUpgrade getTanksUpgrade(ContainerItemContext context) {
        ItemStack backpack = context.getItemVariant().toStack();
        BackpackWrapper backpackWrapper = BackpackWrapper.fromStack(backpack);
        return backpackWrapper.getUpgrade(TanksUpgrade.class).get();
    }

    @Override
    public boolean isResourceBlank() {
        return this.getResource().isBlank();
    }

    @Override
    public FluidVariant getResource() {
        return getTank(getTanksUpgrade(context)).getResource();
    }

    @Override
    public long getAmount() {
        return getTank(getTanksUpgrade(context)).getAmount();
    }

    @Override
    public long getCapacity() {
        return getTank(getTanksUpgrade(context)).getCapacity();
    }

    public FluidTank getTank(TanksUpgrade tanksUpgrade) {
        return leftTank ? tanksUpgrade.getLeftTank() : tanksUpgrade.getRightTank();
    }
}