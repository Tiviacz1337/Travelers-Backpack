package com.tiviacz.travelersbackpack.inventory.transfer;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class BackpackFluidsHandler extends FluidStacksResourceHandler {
    public BackpackFluidsHandler(int capacity) {
        super(1, capacity);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setFluid(FluidStack fluidStack) {
        set(0, FluidResource.of(fluidStack), fluidStack.getAmount());
    }

    public FluidStack getFluid() {
        return getResource(0).toStack(getAmountAsInt(0));
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean isEmpty() {
        return getResource(0).isEmpty();
    }

    public int getFluidAmount() {
        return getAmountAsInt(0);
    }
}