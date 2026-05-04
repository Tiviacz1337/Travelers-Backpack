package com.tiviacz.travelersbackpack.inventory.transfer;

import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ItemFluidTankWrapper extends ItemAccessResourceHandler<FluidResource> {
    private final Item validItem;
    private final TanksUpgrade upgrade;

    public ItemFluidTankWrapper(ItemAccess access, TanksUpgrade upgrade) {
        super(access, 2);
        this.validItem = access.getResource().getItem();
        this.upgrade = upgrade;
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
        if(accessResource.is(validItem)) {
            if(index == 0) {
                return upgrade.getLeftTank().getResource(0);
            } else {
                return upgrade.getRightTank().getResource(0);
            }
        } else {
            return FluidResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        if(accessResource.is(validItem)) {
            if(index == 0) {
                return upgrade.getLeftTank().getAmountAsInt(0);
            } else {
                return upgrade.getRightTank().getAmountAsInt(0);
            }
        } else {
            return 0;
        }
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        ItemContainerContents upgrades = accessResource.get(ModDataComponents.UPGRADES);
        if(upgrades == null) {
            return accessResource;
        }
        NonNullList<ItemStack> stacks = NonNullList.withSize(upgrades.getSlots(), ItemStack.EMPTY);
        upgrades.copyInto(stacks);
        ItemStack tanksCopy = null;
        int slot = 0;
        for(int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if(stack.getItem() instanceof TanksUpgradeItem) {
                Fluids fluids = stack.getOrDefault(ModDataComponents.FLUIDS, new Fluids(FluidStack.EMPTY, FluidStack.EMPTY));
                tanksCopy = stack.copy();
                tanksCopy.set(ModDataComponents.FLUIDS, new Fluids(index == 0 ? newResource.toStack(newAmount) : fluids.leftFluidStack().copy(), index == 1 ? newResource.toStack(newAmount) : fluids.rightFluidStack().copy()));
                slot = i;
            }
        }

        stacks.set(slot, tanksCopy);
        return accessResource.with(ModDataComponents.UPGRADES, ItemContainerContents.fromItems(stacks));
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return itemAccess.getResource().is(validItem);
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        return upgrade.getUpgradeManager().getWrapper().getBackpackTankCapacity();
    }
}