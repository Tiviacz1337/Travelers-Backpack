package com.tiviacz.travelersbackpack.inventory.transfer;

import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
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
            return FluidResource.of(upgrade.getLeftTank().getFluid());
        }
        return FluidResource.of(upgrade.getRightTank().getFluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        if(index == 0) {
            return upgrade.getLeftTank().getFluidAmount();
        }
        return upgrade.getRightTank().getFluidAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        if(index == 0) {
            return upgrade.getLeftTank().getCapacity();
        }
        return upgrade.getRightTank().getCapacity();
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if(index == 0) {
            return upgrade.getLeftTank().isFluidValid(index, resource.toStack(getAmountAsInt(index)));
        }
        return upgrade.getRightTank().isFluidValid(index, resource.toStack(getAmountAsInt(index)));
       //return parent.isFluidValid(index, resource.toStack(getAmountAsInt(index)));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if(this.backpack.getCount() > 1) {
            return 0;
        }
        if(upgrade.getLeftTank().fill(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE) > 0) {
            return upgrade.getLeftTank().fill(resource.toStack(amount), IFluidHandler.FluidAction.EXECUTE);
        }
        return upgrade.getRightTank().fill(resource.toStack(amount), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if(this.backpack.getCount() > 1) {
            return 0;
        }
        if(!upgrade.getLeftTank().drain(amount, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
            return upgrade.getLeftTank().drain(amount, IFluidHandler.FluidAction.EXECUTE).getAmount();
        }
        return upgrade.getRightTank().drain(amount, IFluidHandler.FluidAction.EXECUTE).getAmount();
    }
}
