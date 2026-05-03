package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidTankItemWrapper implements IFluidHandlerItem {
    private ItemStack backpack;
    private TanksUpgrade upgrade;

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
        return 1;
    }

    public FluidStack getFluid() {
        return upgrade.getLeftTank().getFluid().copy();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
        /*if(tank == 0) {
            return upgrade.getLeftTank().getFluid();
        }
        return upgrade.getRightTank().getFluid();*/
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
        if(backpack.getCount() != 1 || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (backpack.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty()) {
            return FluidStack.EMPTY;
        }

        final int drainAmount = Math.min(contained.getAmount(), maxDrain);

        FluidStack drained = contained.copyWithAmount(drainAmount);

        if (action.execute()) {
            contained.shrink(drainAmount);
            saveToContainer(contained);
        }

        return drained;
    }

    //#TODO tweak ...
    public void saveToContainer(FluidStack fluidStack) {
        BackpackContainerContents upgrades = backpack.get(ModDataComponents.UPGRADES);
        NonNullList<ItemStack> stacks = NonNullList.withSize(upgrades.getItems().size(), ItemStack.EMPTY);
        upgrades.copyInto(stacks);
        ItemStack tanksCopy = null;
        int slot = 0;
        for(int i = 0 ; i < stacks.size() ; i++) {
            ItemStack stack = stacks.get(i);
            if(stack.getItem() instanceof TanksUpgradeItem) {
                tanksCopy = stack.copy();
                tanksCopy.set(ModDataComponents.FLUIDS, new Fluids(fluidStack, FluidStack.EMPTY));
                slot = i;
            }
        }

        stacks.set(slot, tanksCopy);
        backpack.set(ModDataComponents.UPGRADES, BackpackContainerContents.fromItems(stacks.size(), stacks));
    }
}