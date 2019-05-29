package com.tiviacz.travellersbackpack.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.items.ItemHose;
import com.tiviacz.travellersbackpack.network.client.UpdateInventoryPacket;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.NBTUtils;
import com.tiviacz.travellersbackpack.util.Reference;
import com.tiviacz.travellersbackpack.wearable.Wearable;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
        try
        {
            InventoryTravellersBackpack backpack = WearableUtils.getBackpackInv(player);
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
            
            TravellersBackpack.NETWORK.sendToAll(new UpdateInventoryPacket(WearableUtils.getWearingBackpack(player).getTagCompound()));
            backpack.markDirty();
            backpack.closeInventory(player);
        } catch (Exception oops)
        {
            oops.printStackTrace();
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
	
	public static boolean setFluidEffect(World world, EntityPlayer player, FluidTank tank)
    {
        FluidStack fluid = tank.drain(Reference.BUCKET, false);
        boolean done = false;
        
        if(fluid != null && fluid.amount >= Reference.BUCKET && FluidEffectRegistry.hasFluidEffect(fluid.getFluid()))
        {
            done = FluidEffectRegistry.executeFluidEffectsForFluid(fluid.getFluid(), player, world);
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
		Wearable instance = new Wearable(player);
		World world = player.world;
		
		if(!world.isRemote)
		{
			if(!instance.hasWearable())
			{
				instance.setWearable(player.getHeldItemMainhand());
				instance.saveDataToPlayer();
				player.getHeldItemMainhand().shrink(1);
				world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
			}
			player.closeScreen();
		}
	}
	
	public static void unequipBackpack(EntityPlayer player)
	{
		Wearable instance = new Wearable(player);
		World world = player.world;
		
		if(!world.isRemote)
		{
			if(!player.inventory.addItemStackToInventory(instance.getWearable()))
			{
				player.sendMessage(new TextComponentTranslation("actions.unequip_backpack.nospace"));
				player.closeScreen();
				return;
			}
			
			else if(instance.hasWearable())
			{
				instance.setWearable(null);
				instance.saveDataToPlayer();
				world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
			}
			player.closeScreen();
		}
	}
	
	public static void electrify(EntityPlayer player)
	{
		if(NBTUtils.hasWearingTag(player))
		{
			ItemStack stack = WearableUtils.getWearingBackpack(player);
			
			if(stack.getMetadata() == 7)
			{
				Wearable wearable = new Wearable(player);
				
				ItemStack newStack = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, 24);
				newStack.setTagCompound(stack.getTagCompound());
				wearable.setWearable(null);
				wearable.setWearable(newStack);
				wearable.saveDataToPlayer();
				player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1.0F, 1.0F);
			}
		}
	}
	
	public static void storePlayerProps(EntityPlayer player)
    {
        try
        {
            NBTTagCompound data = player.getEntityData();
            
            if(data.hasKey("Wearable"))
            {
            	extendedPlayerData.put(player.getUniqueID(), data.getCompoundTag("Wearable"));
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
            NBTTagCompound data = player.getEntityData();
            
            if(data.hasKey("Wearable"))
            {
            	extendedBackpackData.put(player.getUniqueID(), data.getCompoundTag("Wearable"));
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