package com.tiviacz.travellersbackpack.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.capability.IBackpack;
import com.tiviacz.travellersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModFluids;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.items.ItemHose;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapability;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapabilityMP;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	private static final Map<UUID, NBTTagCompound> extendedPlayerData = new HashMap<UUID, NBTTagCompound>();
	private static final Map<UUID, NBTTagCompound> extendedBackpackData = new HashMap<UUID, NBTTagCompound>();
	
	public static void cycleTool(EntityPlayer player, int direction)
    {
		if(CapabilityUtils.isWearingBackpack(player))
		{
			InventoryTravellersBackpack backpack = CapabilityUtils.getBackpackInv(player);
	        ItemStack heldItem = player.getHeldItemMainhand();
	        backpack.openInventory(player);
	            
	        if(direction < 0)
	        {
	        	player.setHeldItem(EnumHand.MAIN_HAND, backpack.getStackInSlot(Reference.TOOL_UPPER));
	        	backpack.setInventorySlotContents(Reference.TOOL_UPPER, backpack.getStackInSlot(Reference.TOOL_LOWER));
	        	backpack.setInventorySlotContents(Reference.TOOL_LOWER, heldItem);
	
	        }   
	        else
	        {
	        	if(direction > 0)
	        	{
	        		player.setHeldItem(EnumHand.MAIN_HAND, backpack.getStackInSlot(Reference.TOOL_LOWER));
	        		backpack.setInventorySlotContents(Reference.TOOL_LOWER, backpack.getStackInSlot(Reference.TOOL_UPPER));
	        		backpack.setInventorySlotContents(Reference.TOOL_UPPER, heldItem);
	        	}
	        } 
	        
	        backpack.markDirty();
	        backpack.closeInventory(player);
	        
	        //Sync
	        ItemStack wearable = CapabilityUtils.getWearingBackpack(player).copy();
	        TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(wearable.writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
			TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(wearable.writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
	        
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
	
	public static void emptyTank(int tankType, EntityPlayer player, World world)
	{
		IInventoryTravellersBackpack inv = CapabilityUtils.getBackpackInv(player);
		FluidTank tank = tankType == 1 ? inv.getLeftTank() : inv.getRightTank();
	    world.playSound(null, player.getPosition(), tank.getFluid().getFluid().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
		tank.drain(4000, true);
		inv.markTankDirty();
		player.closeScreen();
		
		//Sync
		TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(CapabilityUtils.getWearingBackpack(player).writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
		TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(CapabilityUtils.getWearingBackpack(player).writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
	}
	
	public static boolean setFluidEffect(World world, EntityPlayer player, FluidTank tank)
    {
		FluidStack fluidStack = tank.getFluid();
        boolean done = false;
        
        if(fluidStack != null && FluidEffectRegistry.hasFluidEffect(fluidStack.getFluid()))
        {
        	if(fluidStack.getFluid() == ModFluids.POTION && fluidStack.amount >= Reference.POTION)
        	{
        		done = FluidEffectRegistry.executeFluidEffectsForFluid(fluidStack, player, world);
        	}
        	else
        	{
        		if(fluidStack.amount >= Reference.BUCKET)
        		{
        			done = FluidEffectRegistry.executeFluidEffectsForFluid(fluidStack, player, world);
        		}
        	}
        }
        
        return done;
    }
	
	public static void toggleSleepingBag(EntityPlayer player, int x, int y, int z)
    {
        World world = player.world;
        BlockPos pos = new BlockPos(x,y,z);
        
        if(world.getTileEntity(pos) instanceof TileEntityTravellersBackpack)
        {
        	TileEntityTravellersBackpack te = (TileEntityTravellersBackpack)world.getTileEntity(pos);
        	
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
		IBackpack cap = CapabilityUtils.getCapability(player);
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
				TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(stack.writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
				TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(stack.writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
			}
			player.closeScreen();
		}
	}
	
	public static void unequipBackpack(EntityPlayer player)
	{
		IBackpack cap = CapabilityUtils.getCapability(player);
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
				TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(ItemStack.EMPTY.writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
				TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(ItemStack.EMPTY.writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
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
				IBackpack cap = CapabilityUtils.getCapability(player);
				
				ItemStack newStack = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, stack.getMetadata() == 7 ? 24 : 51);
				newStack.setTagCompound(stack.getTagCompound());
				cap.removeWearable();
				cap.setWearable(newStack);
				player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1.0F, 1.0F);
				
				//Sync
				TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(newStack.writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
				TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(newStack.writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
			}
		}
	}
	
	public static void storePlayerProps(EntityPlayer player)
    {
        try
        {
            IBackpack cap = CapabilityUtils.getCapability(player);
            
            if(cap.hasWearable())
            {
            	ItemStack stack = cap.getWearable().copy();
            	extendedPlayerData.put(player.getUniqueID(), stack.writeToNBT(new NBTTagCompound()));
            }
            else
            {
            	return;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
	
	public static void storeBackpackProps(EntityPlayer player)
    {
        try
        {
        	IBackpack cap = CapabilityUtils.getCapability(player);
            
            if(cap.hasWearable())
            {
            	ItemStack stack = cap.getWearable().copy();
            	extendedBackpackData.put(player.getUniqueID(), stack.writeToNBT(new NBTTagCompound()));
            }
            else
            {
            	return;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static NBTTagCompound extractPlayerProps(UUID playerID)
    {
        return extendedPlayerData.remove(playerID);
    }
    
    public static Map<UUID, NBTTagCompound> getPlayerMap()
    {
    	return extendedPlayerData;
    }
    
    public static Map<UUID, NBTTagCompound> getBackpackMap()
    {
    	return extendedBackpackData;
    }
    
    public static void dropItemStackInWorld(World world, ItemStack stack, BlockPos pos)
    {
    	if(!world.isRemote)
    	{
    		world.spawnEntity(new EntityItem(world, pos.getX() + (int)world.rand.nextFloat(), pos.getY() + (int)world.rand.nextFloat(), pos.getZ() + (int)world.rand.nextFloat(), stack));
    	}
    }
}