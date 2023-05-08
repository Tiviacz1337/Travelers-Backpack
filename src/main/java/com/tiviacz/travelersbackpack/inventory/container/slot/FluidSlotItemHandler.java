package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;

public class FluidSlotItemHandler extends SlotItemHandler
{
    private final int index;
    private final ITravelersBackpackInventory inventory;

    public FluidSlotItemHandler(ITravelersBackpackInventory inventory, int index, int xPosition, int yPosition)
    {
        super(inventory.getInventory(), index, xPosition, yPosition);
        this.index = index;
        this.inventory = inventory;
    }

    @Override
    public boolean mayPickup(PlayerEntity playerIn)
    {
        if(inventory.getTier().getOrdinal() <= 1)
        {
            if(index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
            {
                return this.hasItem();
            }
        }
        return true;
    }

    @Override
    public boolean isActive()
    {
        if(inventory.getTier().getOrdinal() <= 1)
        {
            if(index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
            {
                return this.hasItem();
            }
        }
        return true;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);

        if(index == this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
        {
            return false;
        }

        if(stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE)
        {
            return true;
        }

        return container.isPresent();
    }

 /*   public static boolean isValid(ItemStack stack)
    {
        LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);

        if(stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE)
        {
            return true;
        }

        return container.isPresent();
    } */

  /*  public static boolean checkFluid(ItemStack stack, FluidTank leftTank, FluidTank rightTank)
    {
      /*  if(stack.getItem() instanceof PotionItem)
        {
            FluidStack fluidStack = new FluidStack(ModFluids.POTION, 250);
            FluidUtils.setFluidStackNBT(stack, fluidStack);

            if(leftTank.getFluid() != null || leftTank.getFluidAmount() != 0)
            {
                if(leftTank.getFluid().isFluidEqual(fluidStack))
                {
                    if(leftTank.getFluidAmount() == leftTank.getCapacity())
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
            else
            {
                if(rightTank.getFluid() != null || rightTank.getFluidAmount() != 0)
                {
                    if(!rightTank.getFluid().isFluidEqual(fluidStack) || rightTank.getFluidAmount() == rightTank.getCapacity())
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
        }

        if(stack.getItem() == Items.GLASS_BOTTLE)
        {
            if(leftTank.getFluid() != null && leftTank.getFluidAmount() > 0)
            {
                return true;
            }

            if(leftTank.getFluid() == null && rightTank.getFluid() == null)
            {
                return true;
            }
            return false;
        }  */
        //    else
        //   {
     /*   LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);

        if(container != null)
        {
            if(leftTank.getFluid() != null || leftTank.getFluidAmount() != 0)
            {
                if(leftTank.getFluid().isFluidEqual(container.map(iFluidHandlerItem -> iFluidHandlerItem.getFluidInTank(0)).orElse(null)))
                {
                    return leftTank.getFluidAmount() != leftTank.getCapacity();
                }
            }
            else
            {
                if(rightTank.getFluid() != null || rightTank.getFluidAmount() != 0)
                {
                    if(container.map(iFluidHandlerItem -> iFluidHandlerItem.getFluidInTank(0)).orElse(null) == null)
                    {
                        return false;
                    }

                    else return !rightTank.getFluid().isFluidEqual(container.map(iFluidHandlerItem -> iFluidHandlerItem.getFluidInTank(0)).orElse(null)) || rightTank.getFluidAmount() == rightTank.getCapacity();
                }
                else
                {
                    return true;
                }
            }
        }
        //    }
        return false;
    } */

 /*   @Override
    public void putStack(ItemStack stack)
    {
        super.putStack(stack);
        //if(stack.getItem() == Items.AIR)
        //{
        //    return;
        //}

        //this.inventory.getInventory().setStackInSlot(this.index, stack);
        //this.onSlotChanged();
    } */


    @Override
    public void setChanged()
    {
        super.setChanged();
        inventory.updateTankSlots();
    }
}