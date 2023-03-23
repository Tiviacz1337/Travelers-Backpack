package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class InventoryActions
{
    public static boolean transferContainerTank(ITravelersBackpackInventory inv, FluidTank tank, int slotIn, @Nullable PlayerEntity player)
    {
        ItemStackHandler inventory = inv.getInventory();

        ItemStack stackIn = inventory.getStackInSlot(slotIn);
        int slotOut = slotIn + 1;

        if(tank == null || stackIn.isEmpty() || stackIn.getItem() == Items.AIR) return false;

        // --- POTION PART ---
        if(stackIn.getItem() instanceof PotionItem && stackIn.getItem() != Items.GLASS_BOTTLE)
        {
            int amount = Reference.POTION;
            FluidStack fluidStack = new FluidStack(ModFluids.POTION_FLUID.get(), amount);
            FluidUtils.setFluidStackNBT(stackIn, fluidStack);

            if(tank.isEmpty() || FluidStack.areFluidStackTagsEqual(tank.getFluid(), fluidStack))
            {
                if(tank.getFluidAmount() + amount <= tank.getCapacity())
                {
                    ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                    ItemStack currentStackOut = inventory.getStackInSlot(slotOut);

                    if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem())
                    {
                        if(currentStackOut.getItem() == bottle.getItem())
                        {
                            if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

                            bottle.setCount(inventory.getStackInSlot(slotOut).getCount() + 1);
                        }

                        tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        inv.decrStackSize(slotIn, 1);
                        inventory.setStackInSlot(slotOut, bottle);
                        inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);

                        if(player != null)
                        {
                            player.level.playSound(null, player.position().x(), player.position().y() + 0.5, player.position().z(), SoundEvents.BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        }

                        return true;
                    }
                }
            }
        }

        if(stackIn.getItem() == Items.GLASS_BOTTLE)
        {
            if(tank.getFluid().getFluid() == ModFluids.POTION_FLUID.get() && tank.getFluidAmount() >= Reference.POTION)
            {
                ItemStack stackOut = FluidUtils.getItemStackFromFluidStack(tank.getFluid());
                ItemStack currentStackOut = inventory.getStackInSlot(slotOut);

                if(currentStackOut.isEmpty())
                {
                    tank.drain(Reference.POTION, IFluidHandler.FluidAction.EXECUTE);
                    inv.decrStackSize(slotIn, 1);
                    inventory.setStackInSlot(slotOut, stackOut);
                    inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);

                    if(player != null)
                    {
                        player.level.playSound(null, player.position().x(), player.position().y() + 0.5, player.position().z(), SoundEvents.BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }

                    return true;
                }
            }
        }
        // --- POTION PART ---

        LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stackIn);

        if(container.isPresent())
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

                ItemStack slotOutStack = inventory.getStackInSlot(slotOut);

                if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem())
                {
                    if(slotOutStack.getItem() == stackOut.getItem())
                    {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    }

                    if(stackInCopy.getItem() == Items.WATER_BUCKET && EnchantmentHelper.getEnchantments(stackInCopy).containsKey(Enchantments.INFINITY_ARROWS))
                    {
                        stackOut = stackInCopy;
                    }

                    FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, true);

                    inventory.setStackInSlot(slotOut, stackOut);
                    inv.decrStackSize(slotIn, 1);
                    inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);

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

                ItemStack slotOutStack = inventory.getStackInSlot(slotOut);

                if(slotOutStack.isEmpty() || slotOutStack.getItem() == stackOut.getItem())
                {
                    if(slotOutStack.getItem() == stackOut.getItem())
                    {
                        stackOut.setCount(slotOutStack.getCount() + 1);

                        if(stackOut.getCount() > slotOutStack.getMaxStackSize()) return false;
                    }

                    FluidUtil.tryFillContainer(stackIn, tank, amount, player, true);

                    inventory.setStackInSlot(slotOut, stackOut);
                    inv.decrStackSize(slotIn, 1);
                    inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);

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