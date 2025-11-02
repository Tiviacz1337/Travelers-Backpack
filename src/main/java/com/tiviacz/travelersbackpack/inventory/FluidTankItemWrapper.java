package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidTankItemWrapper implements IFluidHandlerItem {
    private final ItemStack backpack;
    private final TanksUpgrade upgrade;

    public FluidTankItemWrapper(ItemStack backpack, TanksUpgrade upgrade) {
        this.backpack = backpack;
        this.upgrade = upgrade;
    }

    @Override
    public ItemStack getContainer() {
        return backpack;
    }

    @Override
    public int getTanks() {
        return 2;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if(tank == 0) {
            return upgrade.getLeftTank().getFluid();
        }
        return upgrade.getRightTank().getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        if(tank == 0) {
            return upgrade.getLeftTank().getCapacity();
        }
        return upgrade.getRightTank().getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        if(tank == 0) {
            return upgrade.getLeftTank().isFluidValid(stack);
        }
        return upgrade.getRightTank().isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if(getContainer().getCount() > 1) {
            return 0;
        }
        if(upgrade.getLeftTank().fill(resource, FluidAction.SIMULATE) > 0) {
            return upgrade.getLeftTank().fill(resource, action);
        }
        return upgrade.getRightTank().fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if(getContainer().getCount() > 1) {
            return FluidStack.EMPTY;
        }
        if(!upgrade.getLeftTank().drain(resource, FluidAction.SIMULATE).isEmpty()) {
            return upgrade.getLeftTank().drain(resource, action);
        }
        return upgrade.getRightTank().drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if(getContainer().getCount() > 1) {
            return FluidStack.EMPTY;
        }
        if(!upgrade.getLeftTank().drain(maxDrain, FluidAction.SIMULATE).isEmpty()) {
            return upgrade.getLeftTank().drain(maxDrain, action);
        }
        return upgrade.getRightTank().drain(maxDrain, action);
    }
}