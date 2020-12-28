package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travelersbackpack.gui.inventory.IInventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.ItemHose;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ServerActions 
{
	public static void cycleTool(EntityPlayer player, int direction)
    {
		if(CapabilityUtils.isWearingBackpack(player))
		{
			InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
	        ItemStack heldItem = player.getHeldItemMainhand();
	            
	        if(direction < 0)
	        {
	        	player.setHeldItem(EnumHand.MAIN_HAND, inv.getStackInSlot(Reference.TOOL_UPPER));
				inv.setInventorySlotContents(Reference.TOOL_UPPER, inv.getStackInSlot(Reference.TOOL_LOWER));
				inv.setInventorySlotContents(Reference.TOOL_LOWER, heldItem);
	
	        }   
	        else
	        {
	        	if(direction > 0)
	        	{
	        		player.setHeldItem(EnumHand.MAIN_HAND, inv.getStackInSlot(Reference.TOOL_LOWER));
					inv.setInventorySlotContents(Reference.TOOL_LOWER, inv.getStackInSlot(Reference.TOOL_UPPER));
					inv.setInventorySlotContents(Reference.TOOL_UPPER, heldItem);
	        	}
	        }
			CapabilityUtils.synchronise(player);
			CapabilityUtils.synchroniseToOthers(player);
	        inv.markDirty();
		}
    }
	
	public static void emptyTank(int tankType, EntityPlayer player, World world)
	{
		IInventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
		FluidTank tank = tankType == 1 ? inv.getLeftTank() : inv.getRightTank();
	    world.playSound(null, player.getPosition(), tank.getFluid().getFluid().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
		tank.drain(Reference.BASIC_TANK_CAPACITY, true);
		inv.markTankDirty();
		player.closeScreen();
		
		//Sync
		CapabilityUtils.synchronise(player);
		CapabilityUtils.synchroniseToOthers(player);
	}
	
	public static boolean setFluidEffect(World world, EntityPlayer player, FluidTank tank)
    {
		FluidStack fluidStack = tank.getFluid();
        boolean done = false;

        if(FluidEffectRegistry.hasFluidEffectAndCanExecute(fluidStack, world, player))
		{
			done = FluidEffectRegistry.executeFluidEffectsForFluid(fluidStack, player, world);
		}
        return done;
    }
	
	public static void toggleSleepingBag(EntityPlayer player, int x, int y, int z)
    {
        World world = player.world;
        BlockPos pos = new BlockPos(x,y,z);
        
        if(world.getTileEntity(pos) instanceof TileEntityTravelersBackpack)
        {
        	TileEntityTravelersBackpack te = (TileEntityTravelersBackpack)world.getTileEntity(pos);
        	
        	if(!te.isSleepingBagDeployed())
            {
        		if(te.deploySleepingBag(world, pos))
        		{
        			player.closeScreen();
        		}
        		else
            	{
            		player.sendMessage(new TextComponentTranslation(I18n.translateToLocalFormatted("actions.deploy_sleeping_bag")));
            	}
            }
        	else
        	{
        		te.removeSleepingBag(world);
        	} 
            player.closeScreen();
        }
    }
	
	public static void equipBackpack(EntityPlayer player)
	{
		ITravelersBackpack cap = CapabilityUtils.getCapability(player);
		World world = player.world;
		
		if(!world.isRemote)
		{
			if(!cap.hasWearable())
			{
				ItemStack stack = player.getHeldItemMainhand().copy();
				cap.setWearable(stack);
				player.getHeldItemMainhand().shrink(1);
				world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
				
				//Sync
				CapabilityUtils.synchronise(player);
				CapabilityUtils.synchroniseToOthers(player);
			}
			player.closeScreen();
		}
	}
	
	public static void unequipBackpack(EntityPlayer player)
	{
		ITravelersBackpack cap = CapabilityUtils.getCapability(player);
		World world = player.world;
		
		CapabilityUtils.onUnequipped(world, player, cap.getWearable());
		
		if(!world.isRemote)
		{	
			ItemStack wearable = cap.getWearable().copy();
			
			if(!player.inventory.addItemStackToInventory(wearable))
			{
				player.sendMessage(new TextComponentTranslation("actions.unequip_backpack.nospace"));
				player.closeScreen();
				
				return;
			} 
			
			if(cap.hasWearable())
			{
				cap.removeWearable();
				world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
				
				//Sync
				CapabilityUtils.synchronise(player);
				CapabilityUtils.synchroniseToOthers(player);
			}
			
			player.closeScreen();
		}
	}
	
	public static void electrify(EntityPlayer player)
	{
		if(CapabilityUtils.isWearingBackpack(player))
		{
			ItemStack stack = CapabilityUtils.getWearingBackpack(player);
			
			if(stack.getMetadata() == 7 || stack.getMetadata() == 53)
			{
				ITravelersBackpack cap = CapabilityUtils.getCapability(player);
				
				ItemStack newStack = new ItemStack(ModItems.TRAVELERS_BACKPACK, 1, stack.getMetadata() == 7 ? 24 : 51);
				newStack.setTagCompound(stack.getTagCompound());
				cap.removeWearable();
				cap.setWearable(newStack);
				player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1.0F, 1.0F);
				
				//Sync
				CapabilityUtils.synchronise(player);
				CapabilityUtils.synchroniseToOthers(player);
			}
		}
	}

	public static void switchHoseMode(EntityPlayer player, int direction)
	{
		ItemStack hose = player.getHeldItemMainhand();

		if(hose.getItem() instanceof ItemHose)
		{
			if(hose.getTagCompound() != null)
			{
				int mode = ItemHose.getHoseMode(hose);

				if(direction > 0)
				{
					mode = mode + 1;

					if(mode == 4)
					{
						mode = 1;
					}
				}

				else if(direction < 0)
				{
					mode = mode - 1;

					if(mode == 0)
					{
						mode = 3;
					}
				}
				hose.getTagCompound().setInteger("Mode", mode);
			}
		}
	}

	public static void toggleHoseTank(EntityPlayer player)
	{
		ItemStack hose = player.getHeldItemMainhand();

		if(hose.getItem() instanceof ItemHose)
		{
			if(hose.getTagCompound() != null)
			{
				int tank = ItemHose.getHoseTank(hose);

				if(tank == 1)
				{
					tank = 2;
				}
				else
				{
					tank = 1;
				}

				hose.getTagCompound().setInteger("Tank", tank);
			}
		}
	}
}