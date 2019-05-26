package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travellersbackpack.common.ServerActions;
import com.tiviacz.travellersbackpack.gui.container.ContainerTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.network.client.SyncPlayerDataPacket;
import com.tiviacz.travellersbackpack.util.BackpackUtils;
import com.tiviacz.travellersbackpack.util.NBTUtils;
import com.tiviacz.travellersbackpack.wearable.Wearable;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@EventBusSubscriber
public class PlayerEventHandler 
{
	@SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) 
	{
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = event.getNewSpawn();

        if(pos != null) 
        {
            Block block = world.getBlockState(pos).getBlock();

            if(!world.isRemote && (block instanceof BlockSleepingBag)) 
            {
                event.setCanceled(true);
            }
        }
    }
	
	@SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event)
    {
		if(!event.getWorld().isRemote)
		{
			if(event.getEntity() instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
				
				NBTTagCompound playerData = ServerActions.extractPlayerProps(player.getUniqueID());
				
				if(playerData != null)
				{
					Wearable instance = new Wearable(player);
					instance.setWearable(new ItemStack(playerData));
					instance.saveDataToPlayer();
				}
			}
		}
    }
	
	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event)
    {
		if(event.getEntity() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
			
			if(!player.world.isRemote)
			{
				if(NBTUtils.hasWearingTag(player))
				{
					if(!player.getEntityWorld().getGameRules().getBoolean("keepInventory"))
					{
						BackpackUtils.onPlayerDeath(player.world, player, WearableUtils.getWearingBackpack(player));
					}
					else
					{
						ServerActions.storePlayerProps(player);
					}
				}
			}
		}
    }
	
	@SubscribeEvent
	public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			
			if(NBTUtils.hasWearingTag(player))
			{
				TravellersBackpack.NETWORK.sendTo(new SyncPlayerDataPacket(player.getEntityData().getCompoundTag("Wearable"), true), player);
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
	{
		if(event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			
			if(event.isEndConquered())
			{
				NBTTagCompound playerData = ServerActions.getBackpackMap().remove(player.getUniqueID());
				
				if(playerData != null)
				{
					Wearable instance = new Wearable(player);
					instance.setWearable(new ItemStack(playerData));
					instance.saveDataToPlayer();
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerInEnd(LivingEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();
			
			if(player.dimension == 1)
			{
				if(WearableUtils.isWearingBackpack(player))
				{
					if(ServerActions.getBackpackMap().isEmpty())
					{
						ServerActions.storeBackpackProps(player);
					}
					
					InventoryTravellersBackpack inv = WearableUtils.getBackpackInv(player);
					
					if(!inv.hasTileEntity())
					{
						if(player.openContainer instanceof ContainerTravellersBackpack)
						{
							ServerActions.storeBackpackProps(player);
						}
					}
				}
				else
				{
					if(!ServerActions.getBackpackMap().isEmpty())
					{
						ServerActions.getBackpackMap().clear();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
    public static void onPlayerTravelsAcrossDimensions(PlayerEvent.PlayerChangedDimensionEvent event)
    {
		if(event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			if(NBTUtils.hasWearingTag(player))
			{
				TravellersBackpack.NETWORK.sendTo(new SyncPlayerDataPacket(player.getEntityData().getCompoundTag("Wearable"), true), player);
			}
		}
    }
}