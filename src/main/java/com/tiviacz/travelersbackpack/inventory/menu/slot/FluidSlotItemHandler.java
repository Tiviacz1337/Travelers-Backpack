package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FluidSlotItemHandler extends SlotItemHandler
{
    private final int index;
    private final ITravelersBackpackContainer container;

    public FluidSlotItemHandler(ITravelersBackpackContainer container, int index, int xPosition, int yPosition)
    {
        super(container.getFluidSlotsHandler(), index, xPosition, yPosition);
        this.index = index;
        this.container = container;
    }

    @Override
    public boolean mayPickup(Player playerIn)
    {
        if(container.getTier().getOrdinal() <= 1)
        {
            if(index == container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
            {
                return this.hasItem();
            }
        }
        return true;
    }

    @Override
    public boolean isActive()
    {
        if(container.getTier().getOrdinal() <= 1)
        {
            if(index == container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
            {
                return this.hasItem();
            }
        }
        return true;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);

        if(index == this.container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == this.container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
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
        container.updateTankSlots();
    }
}