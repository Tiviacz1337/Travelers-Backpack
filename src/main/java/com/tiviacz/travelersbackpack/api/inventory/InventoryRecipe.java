package com.tiviacz.travelersbackpack.api.inventory;

import com.tiviacz.travelersbackpack.common.InventoryRecipesRegistry;
import com.tiviacz.travelersbackpack.gui.container.slots.SlotFluid;
import com.tiviacz.travelersbackpack.gui.inventory.IInventoryTanks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class InventoryRecipe
{
    private final ItemStack stackInRecipe;
    private final FluidStack fluidstackRecipe;
    private final int amountRecipe;
    private final ItemStack stackOutRecipe;
    private int recipeID;

    public InventoryRecipe(Item itemInRecipe, Fluid fluidRecipe, int amountRecipe, Item itemOutRecipe)
    {
        this(new ItemStack(itemInRecipe), fluidRecipe, amountRecipe, new ItemStack(itemOutRecipe));
    }

    public InventoryRecipe(Item itemInRecipe, FluidStack fluidstackRecipe, int amountRecipe, Item itemOutRecipe)
    {
        this(new ItemStack(itemInRecipe), fluidstackRecipe, amountRecipe, new ItemStack(itemOutRecipe));
    }

    public InventoryRecipe(ItemStack stackInRecipe, Fluid fluidRecipe, int amountRecipe, ItemStack stackOutRecipe)
    {
        this(stackInRecipe, new FluidStack(fluidRecipe, 1000), amountRecipe, stackOutRecipe);
    }

    /**
     * Use it to add recipes for inventory in backpack.
     * Override canProcess and processRecipe to change behaviour
     * Return SoundEvent
     * @param stackInRecipe Stack to insert in upper fluid slot
     * @param fluidstackRecipe Fluidstack to transfer
     * @param amountRecipe Amount of fluidstack to transfer
     * @param stackOutRecipe Stack to insert in lower fluid slot
     */

    public InventoryRecipe(ItemStack stackInRecipe, FluidStack fluidstackRecipe, int amountRecipe, ItemStack stackOutRecipe)
    {
        this.stackInRecipe = stackInRecipe;
        this.fluidstackRecipe = fluidstackRecipe;
        this.amountRecipe = amountRecipe;
        this.stackOutRecipe = stackOutRecipe;

        if(stackInRecipe != null && fluidstackRecipe != null && stackOutRecipe != null)
        {
            InventoryRecipesRegistry.registerRecipe(this);
        }

        this.addValidFluidItem();
    }

    public boolean canProcess(FluidTank tank, ItemStack stackOut)
    {
        ItemStack stackOutRecipe = this.stackOutRecipe.copy();

        if(this.amountRecipe > 0)
        {
            if(tank.getFluid() == null || tank.getFluid().isFluidEqual(this.fluidstackRecipe))
            {
                if(stackOut.isEmpty())
                {
                    return true;
                }
                else
                {
                    return stackOut.isItemEqual(stackOutRecipe) && stackOut.getCount() + 1 <= stackOut.getMaxStackSize();
                }
            }
        }
        else
        {
            if(tank.getFluid() != null || tank.getFluidAmount() > 0)
            {
                if(stackOut.isEmpty())
                {
                    return true;
                }
                else
                {
                    return stackOut.isItemEqual(stackOutRecipe) && stackOut.getCount() + 1 <= stackOut.getMaxStackSize();
                }
            }
        }
        return false;
    }

    public boolean processRecipe(EntityPlayer player, IInventoryTanks inv, FluidTank tank, int slotIn, int slotOut)
    {
        if(this.amountRecipe > 0)
        {
            if(tank.getFluidAmount() + this.amountRecipe <= tank.getCapacity())
            {
                //Need this still dunno why
                ItemStack stackOutRecipe = this.stackOutRecipe.copy();

                if(!inv.getStackInSlot(slotOut).isEmpty())
                {
                    stackOutRecipe.setCount(inv.getStackInSlot(slotOut).getCount() + 1);
                }

                tank.fill(new FluidStack(this.fluidstackRecipe, this.amountRecipe), true);

                inv.setInventorySlotContents(slotOut, stackOutRecipe);
                inv.decrStackSize(slotIn, 1);
                inv.markTankDirty();

                if(player != null)
                {
                    player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, getSoundEventEmpty() == null ? SoundEvents.ENTITY_PIG_AMBIENT : getSoundEventEmpty(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return true;
            }
        }
        else
        {
            if(tank.getFluidAmount() - this.amountRecipe >= 0)
            {
                ItemStack stackOutRecipe = this.stackOutRecipe.copy();

                if(!inv.getStackInSlot(slotOut).isEmpty())
                {
                    stackOutRecipe.setCount(inv.getStackInSlot(slotOut).getCount() + 1);
                }

                tank.drain(this.amountRecipe * -1, true);

                inv.setInventorySlotContents(slotOut, stackOutRecipe);
                inv.decrStackSize(slotIn, 1);
                inv.markTankDirty();

                if(player != null)
                {
                    player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, getSoundEventFill() == null ? SoundEvents.ENTITY_PIG_AMBIENT: getSoundEventFill(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return false;
            }
        }
        return false;
    }

    public ItemStack getStackIn()
    {
        return this.stackInRecipe;
    }

    public FluidStack getFluidStack()
    {
        return this.fluidstackRecipe;
    }

    public int getAmount()
    {
        return this.amountRecipe;
    }

    public ItemStack getStackOut()
    {
        return this.stackOutRecipe;
    }

    public boolean isRecipeNonNull()
    {
        return this.stackInRecipe != null && this.fluidstackRecipe != null && this.stackOutRecipe != null;
    }

    public void setRecipeID(int id)
    {
        this.recipeID = id;
    }

    public int getRecipeID()
    {
        return this.recipeID;
    }

    public void addValidFluidItem()
    {
        SlotFluid.validItems.add(this.getStackIn().getItem());
    }

    public abstract SoundEvent getSoundEventFill();

    public abstract SoundEvent getSoundEventEmpty();
}