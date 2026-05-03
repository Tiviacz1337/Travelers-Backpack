package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class FluidTankItemWrapper implements IFluidHandlerItem {
    private final ItemStack backpack;
    private final TanksUpgrade upgrade;
    private final int capacity;

    public FluidTankItemWrapper(ItemStack backpack, TanksUpgrade upgrade) {
        this.backpack = backpack;
        this.upgrade = upgrade;
        this.capacity = upgrade.getUpgradeManager().getWrapper().getBackpackTankCapacity();
    }

    @Override
    public ItemStack getContainer() {
        return backpack;
    }

    @Override
    public int getTanks() {
        return 2;
    }

    public Pair<FluidStack, Integer> getFluidAndTank(@Nullable FluidStack fluidStack) {
        FluidStack left = getFluidInTank(0);
        FluidStack right = getFluidInTank(1);

        if(fluidStack == null) {
            if(!upgrade.getLeftTank().drain(getTankCapacity(0), FluidAction.SIMULATE).isEmpty()) {
                return Pair.of(left, 0);
            } else {
                return Pair.of(right, 1);
            }
        }
        if(FluidStack.isSameFluidSameComponents(upgrade.getLeftTank().getFluid(), fluidStack)) {
            return Pair.of(left, 0);
        }
        if(FluidStack.isSameFluidSameComponents(upgrade.getRightTank().getFluid(), fluidStack)) {
            return Pair.of(right, 1);
        }
        if(upgrade.getLeftTank().getFluid().isEmpty()) {
            return Pair.of(left, 0);
        }
        if(upgrade.getRightTank().getFluid().isEmpty()) {
            return Pair.of(right, 1);
        }
        return Pair.of(left, 0);
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if(tank == 0) {
            return upgrade.getLeftTank().getFluid().copy();
        }
        return upgrade.getRightTank().getFluid().copy();
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
    public int fill(FluidStack resource, FluidAction doFill) {
        if(backpack.getCount() != 1 || resource.isEmpty()) {
            return 0;
        }

        Pair<FluidStack, Integer> fluidAndTank = getFluidAndTank(resource);
        FluidStack contained = fluidAndTank.getFirst();
        int tank = fluidAndTank.getSecond();
        if(contained.isEmpty()) {
            int fillAmount = Math.min(capacity, resource.getAmount());

            if(doFill.execute()) {
                setFluid(resource.copyWithAmount(fillAmount), tank);
            }
            return fillAmount;
        } else {
            if(FluidStack.isSameFluidSameComponents(contained, resource)) {
                int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());

                if(doFill.execute() && fillAmount > 0) {
                    contained.grow(fillAmount);
                    setFluid(contained, tank);
                }
                return fillAmount;
            }
            return 0;
        }
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        Pair<FluidStack, Integer> fluidAndTank = getFluidAndTank(resource);
        FluidStack contained = fluidAndTank.getFirst();

        if(backpack.getCount() != 1 || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, contained)) {
            return FluidStack.EMPTY;
        }

        int maxDrain = resource.getAmount();
        if(maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        int drainAmount = Math.min(contained.getAmount(), maxDrain);
        FluidStack drained = contained.copyWithAmount(drainAmount);

        if(action.execute()) {
            contained.shrink(drainAmount);
            setFluid(contained, fluidAndTank.getSecond());
        }
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if(backpack.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        Pair<FluidStack, Integer> contained = getFluidAndTank(null);
        if(contained.getFirst().isEmpty()) {
            return FluidStack.EMPTY;
        }

        int drainAmount = Math.min(contained.getFirst().getAmount(), maxDrain);
        FluidStack drained = contained.getFirst().copyWithAmount(drainAmount);

        if(action.execute()) {
            contained.getFirst().shrink(drainAmount);
            setFluid(contained.getFirst(), contained.getSecond());
        }

        return drained;
    }

    public void setFluid(FluidStack fluidStack, int tank) {
        //0 - left, 1 - right
        BackpackContainerContents upgrades = backpack.get(ModDataComponents.UPGRADES);
        if(upgrades == null) {
            return;
        }
        NonNullList<ItemStack> stacks = NonNullList.withSize(upgrades.getItems().size(), ItemStack.EMPTY);
        upgrades.copyInto(stacks);
        ItemStack tanksCopy = null;
        int slot = 0;
        for(int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if(stack.getItem() instanceof TanksUpgradeItem) {
                Fluids fluids = stack.getOrDefault(ModDataComponents.FLUIDS, new Fluids(FluidStack.EMPTY, FluidStack.EMPTY));
                tanksCopy = stack.copy();
                tanksCopy.set(ModDataComponents.FLUIDS, new Fluids(tank == 0 ? fluidStack : fluids.leftFluidStack().copy(), tank == 1 ? fluidStack : fluids.rightFluidStack().copy()));
                slot = i;
            }
        }

        stacks.set(slot, tanksCopy);
        backpack.set(ModDataComponents.UPGRADES, BackpackContainerContents.fromItems(stacks.size(), stacks));
    }
}