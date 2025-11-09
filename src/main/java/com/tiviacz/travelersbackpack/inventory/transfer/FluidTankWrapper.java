package com.tiviacz.travelersbackpack.inventory.transfer;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FluidTankWrapper implements ResourceHandler<FluidResource> {
    public final FluidTank parent;
    public FluidTankWrapper(FluidTank parent) {
        this.parent = parent;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        return FluidResource.of(parent.getFluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        return parent.getFluidAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return parent.getCapacity();
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return parent.isFluidValid(index, resource.toStack(getAmountAsInt(index)));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return parent.fill(resource.toStack(amount), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return parent.drain(amount, IFluidHandler.FluidAction.EXECUTE).getAmount();
    }
}