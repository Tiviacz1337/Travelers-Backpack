package com.tiviacz.travelersbackpack.gui.inventory;

import com.tiviacz.travelersbackpack.common.InventoryRecipesRegistry;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class InventoryActions 
{
	public static boolean transferContainerTank(IInventoryTanks inventory, FluidTank tank, int slotIn, EntityPlayer player)
	{
		ItemStack stackIn = inventory.getStackInSlot(slotIn);
		int slotOut = slotIn + 1;
		ItemStack currentStackOut = inventory.getStackInSlot(slotOut);

		if(tank == null || stackIn.isEmpty())
		{
			return false;
		}

		if(InventoryRecipesRegistry.hasStackInInventoryRecipeAndCanProcess(player, inventory, stackIn, tank, slotIn))
		{
			return true;
		}

		else if(stackIn.getItem() != Items.GLASS_BOTTLE)
		{
			//POTION PART ---
			if(stackIn.getItem() instanceof ItemPotion)
			{
				int amount = Reference.POTION;
				FluidStack fluidStack = new FluidStack(ModFluids.POTION, amount);
				FluidUtils.setFluidStackNBT(stackIn, fluidStack);

				if(tank.getFluid() == null || FluidStack.areFluidStackTagsEqual(tank.getFluid(), fluidStack))
				{
					if(tank.getFluidAmount() + amount <= tank.getCapacity())
					{
						ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);

						if(currentStackOut.isEmpty() || currentStackOut.getItem() == bottle.getItem())
						{
							if(currentStackOut.getItem() == bottle.getItem())
							{
								if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

								bottle.setCount(inventory.getStackInSlot(slotOut).getCount() + 1);
							}

							tank.fill(fluidStack, true);
							inventory.decrStackSize(slotIn, 1);
							inventory.setInventorySlotContents(slotOut, bottle);
							inventory.markTankDirty();

							if(player != null)
							{
								player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
							}

							return true;
						}
					}
				}
			}
			//POTION PART ---

			//Container ===> Tank

			IFluidHandlerItem container = FluidUtil.getFluidHandler(stackIn);

			if(container == null)
			{
				return false;
			}
			else
			{
				FluidStack fluid = FluidUtil.getFluidContained(stackIn);

				if(fluid != null)
				{
					int amount = fluid.amount;

					if(amount <= 0) return false;

					ItemStack stackOut = FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, false).getResult();

					if(stackOut.isEmpty()) return false;

					if(tank.getFluidAmount() + amount > tank.getCapacity()) return false;
					if(tank.getFluidAmount() > 0 && !tank.getFluid().isFluidEqual(fluid)) return false;

					if(currentStackOut.isEmpty() || currentStackOut.getItem() == stackOut.getItem())
					{
						if(currentStackOut.getItem() == stackOut.getItem())
						{
							if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

							stackOut.setCount(currentStackOut.getCount() + 1);
						}

						FluidUtil.tryEmptyContainer(stackIn, tank, amount, player, true);

						inventory.decrStackSize(slotIn, 1);
						inventory.setInventorySlotContents(slotOut, stackOut);
						inventory.markTankDirty();

						return true;
					}
				}
			}
		}

		//Tank ===> Container

		if(tank.getFluid() == null || tank.getFluidAmount() <= 0)
		{
			return false;
		}
		else
		{
			// --- POTION PART ---
			if(stackIn.getItem() == Items.GLASS_BOTTLE)
			{
				if(tank.getFluid().getFluid() == ModFluids.POTION && tank.getFluidAmount() >= Reference.POTION)
				{
					ItemStack stackOut = FluidUtils.getItemStackFromFluidStack(tank.getFluid());

					if(currentStackOut.isEmpty())
					{
						tank.drain(Reference.POTION, true);
						inventory.decrStackSize(slotIn, 1);
						inventory.setInventorySlotContents(slotOut, stackOut);
						inventory.markTankDirty();

						if(player != null)
						{
							player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
						}

						return true;
					}
				}
			}
			// --- POTION PART ---

			else if(isFluidEqual(stackIn, tank))
			{
				if(tank.getFluid().getFluid() == ModFluids.POTION) return false;

				int amount = FluidUtil.getFluidHandler(stackIn).getTankProperties()[0].getCapacity();
				ItemStack stackOut = FluidUtil.tryFillContainer(stackIn, tank, amount, player, false).getResult();

				if(stackOut.isEmpty()) return false;

				if(currentStackOut.isEmpty() || currentStackOut.getItem() == stackOut.getItem())
				{
					if(currentStackOut.getItem() == stackOut.getItem())
					{
						if(currentStackOut.getCount() + 1 > currentStackOut.getMaxStackSize()) return false;

						stackOut.setCount(currentStackOut.getCount() + 1);
					}

					FluidUtil.tryFillContainer(stackIn, tank, amount, player, true);

					inventory.decrStackSize(slotIn, 1);
					inventory.setInventorySlotContents(slotOut, stackOut);
					inventory.markTankDirty();

					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isFluidEqual(ItemStack stackIn, FluidTank tank)
	{
		if(FluidUtil.getFluidContained(stackIn) != null && FluidUtil.getFluidContained(stackIn).amount > 0)
		{
			return FluidUtil.getFluidContained(stackIn).isFluidEqual(tank.getFluid());
		}
		else return FluidUtil.getFluidContained(stackIn) == null;
	}
}