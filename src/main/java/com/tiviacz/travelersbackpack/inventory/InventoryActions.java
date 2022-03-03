package com.tiviacz.travelersbackpack.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

public class InventoryActions
{
    public static boolean transferContainerTank(ITravelersBackpackContainer container, FluidTank tank, int slotIn, @Nonnull Player player)
    {
        ItemStackHandler itemStackHandler = container.getHandler();

        ItemStack stackIn = itemStackHandler.getStackInSlot(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        LazyOptional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(stackIn);

        if(fluidHandler.isPresent())
        {
            Optional<FluidStack> fluidstack = FluidUtil.getFluidContained(stackIn);

            //Container ===> Tank

            if(fluidstack.isPresent() && fluidstack.map(FluidStack::getAmount).orElse(0) > 0)
            {
                int amount = fluidstack.map(FluidStack::getAmount).orElse(0);

                if(tank.getFluidAmount() + amount > tank.getCapacity()) return false;
                if(tank.getFluidAmount() > 0 && !tank.getFluid().isFluidEqual(fluidstack.orElse(FluidStack.EMPTY))) return false;

                //Copies
                ItemStack stackInCopy = stackIn.copy();
                FluidTank tankCopy = new FluidTank(tank.getCapacity());
                tankCopy.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                ItemStack stackOut = FluidUtil.tryEmptyContainer(stackInCopy, tankCopy, amount, player, false).getResult();

                if(stackOut.isEmpty()) return false;

                ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);

                if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem())
                {
                    if(slotOutStack.getItem() == stackOut.getItem())
                    {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    }

                    FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, true);

                    itemStackHandler.setStackInSlot(slotOut, stackOut);
                    container.removeItem(slotIn, 1);
                    container.setTankChanged();

                    return true;
                }
            }

            //Tank ===> Container

            if(tank.isEmpty() || tank.getFluidAmount() <= 0) return false;

            if(isFluidEqual(stackIn, tank))
            {
                int amount = FluidUtil.getFluidHandler(stackIn).map(iFluidHandlerItem -> iFluidHandlerItem.getTankCapacity(0)).orElse(0);

                ItemStack stackInCopy = stackIn.copy();
                FluidTank tankCopy = new FluidTank(tank.getCapacity());
                tankCopy.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                ItemStack stackOut = FluidUtil.tryFillContainer(stackInCopy, tankCopy, amount, player, true).getResult();

                if(stackOut.isEmpty()) return false;

                ItemStack slotOutStack = itemStackHandler.getStackInSlot(slotOut);

                if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem())
                {
                    if(slotOutStack.getItem() == stackOut.getItem())
                    {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    }

                    FluidUtil.tryFillContainer(stackIn, tank, amount, player, true);

                    itemStackHandler.setStackInSlot(slotOut, stackOut);
                    container.removeItem(slotIn, 1);
                    container.setTankChanged();

                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isFluidEqual(ItemStack stackIn, FluidTank tank)
    {
        if(FluidUtil.getFluidContained(stackIn).isPresent() && FluidUtil.getFluidContained(stackIn).map(FluidStack::getAmount).orElse(0) > 0)
        {
            return FluidUtil.getFluidContained(stackIn).map(fluidstack -> fluidstack.isFluidEqual(tank.getFluid())).orElse(false);
        }
        else return !FluidUtil.getFluidContained(stackIn).isPresent();
    }
}
