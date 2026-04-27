package com.tiviacz.travelersbackpack.inventory.transfer;

import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ItemFluidTankWrapper implements ResourceHandler<FluidResource> {
    private final ItemStack backpack;
    private final TanksUpgrade upgrade;

    public ItemFluidTankWrapper(ItemStack backpack, TanksUpgrade upgrade) {
        this.backpack = backpack;
        this.upgrade = upgrade;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public FluidResource getResource(int index) {
        if(index == 0) {
            return upgrade.getLeftTank().getResource(0);
        }
        return upgrade.getRightTank().getResource(0);
    }

    @Override
    public long getAmountAsLong(int index) {
        if(index == 0) {
            return upgrade.getLeftTank().getAmountAsLong(0);
        }
        return upgrade.getRightTank().getAmountAsLong(0);
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        if(index == 0) {
            return upgrade.getLeftTank().getCapacityAsLong(0, resource);
        }
        return upgrade.getRightTank().getCapacityAsLong(0, resource);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if(index == 0) {
            return upgrade.getLeftTank().isValid(0, resource);
        }
        return upgrade.getRightTank().isValid(0, resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if(this.backpack.getCount() > 1) {
            return 0;
        }
        try(var tx = Transaction.open(transaction)) {
            int moved = upgrade.getLeftTank().insert(0, resource, amount, tx);
            if(moved > 0) {
                tx.commit();
                return moved;
            } else {
                moved = upgrade.getRightTank().insert(0, resource, amount, tx);
                if(moved > 0) {
                    tx.commit();
                    return moved;
                }
            }
        }
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if(this.backpack.getCount() > 1) {
            return 0;
        }
        try(var tx = Transaction.open(transaction)) {
            int moved = upgrade.getLeftTank().extract(0, resource, amount, tx);
            if(moved > 0) {
                tx.commit();
                return moved;
            } else {
                moved = upgrade.getRightTank().extract(0, resource, amount, tx);
                if(moved > 0) {
                    tx.commit();
                    return moved;
                }
            }
        }
        return 0;
    }
}