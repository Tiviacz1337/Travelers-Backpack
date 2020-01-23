package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travellersbackpack.capability.BackpackProvider;
import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.capability.IBackpack;
import com.tiviacz.travellersbackpack.common.ServerActions;
import com.tiviacz.travellersbackpack.gui.container.ContainerTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapability;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapabilityMP;
import com.tiviacz.travellersbackpack.util.BackpackUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@EventBusSubscriber(modid = TravellersBackpack.MODID)
public class PlayerEventHandler 
{
	public static final ResourceLocation BACKPACK_CAP = new ResourceLocation(TravellersBackpack.MODID, "travellers_backpack");
	
	@SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(!(event.getObject() instanceof EntityPlayer)) 
        {
        	return;
        }

        event.addCapability(BACKPACK_CAP, new BackpackProvider((EntityPlayer)event.getObject()));
    }
	
	@SubscribeEvent
	public static void onPlayerStruckByLightning(EntityStruckByLightningEvent event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			ServerActions.electrify((EntityPlayer)event.getEntity());
		}
	}
	
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
					IBackpack cap = CapabilityUtils.getCapability(player);
					cap.setWearable(new ItemStack(playerData));
					
					//Sync
					TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(new ItemStack(playerData).writeToNBT(new NBTTagCompound())), player);
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
				if(CapabilityUtils.isWearingBackpack(player))
				{
					if(!player.getEntityWorld().getGameRules().getBoolean("keepInventory"))
					{
						BackpackUtils.onPlayerDeath(player.world, player, CapabilityUtils.getWearingBackpack(player));
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
			
			if(CapabilityUtils.isWearingBackpack(player))
			{
				//Sync
				TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(CapabilityUtils.getWearingBackpack(player).writeToNBT(new NBTTagCompound())), player);
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
					IBackpack cap = CapabilityUtils.getCapability(player);
					cap.setWearable(new ItemStack(playerData));
					
					//Sync
					TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(new ItemStack(playerData).writeToNBT(new NBTTagCompound())), player);
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
				if(CapabilityUtils.isWearingBackpack(player))
				{
					if(ServerActions.getBackpackMap().isEmpty())
					{
						ServerActions.storeBackpackProps(player);
					}
					
					InventoryTravellersBackpack inv = CapabilityUtils.getBackpackInv(player);
					
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

			if(CapabilityUtils.isWearingBackpack(player))
			{
				//Sync
				TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(CapabilityUtils.getWearingBackpack(player).writeToNBT(new NBTTagCompound())), (EntityPlayerMP)player);
			}
		}
    }
	
	@SubscribeEvent
	public static void onPlayerTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event)
	{
		if(event.getTarget() instanceof EntityPlayer)
		{
			EntityPlayer target = (EntityPlayer)event.getTarget();
			
			if(CapabilityUtils.isWearingBackpack(target))
			{
				TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(CapabilityUtils.getWearingBackpack(target).writeToNBT(new NBTTagCompound()), target.getEntityId()), target);
			}
			else
			{
				TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(ItemStack.EMPTY.writeToNBT(new NBTTagCompound()), target.getEntityId()), target);
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.player != null && !event.player.isDead && CapabilityUtils.isWearingBackpack(event.player))
		{
			World world = event.player.getEntityWorld();
			EntityPlayer player = event.player;
					
			if(event.phase == TickEvent.Phase.START)
			{
				CapabilityUtils.onEquippedUpdate(world, player, CapabilityUtils.getWearingBackpack(player));
			}
			
			if(event.phase == TickEvent.Phase.END)
            {
                if(event.side.isServer())
                {
                    EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
                    //Sync (Dunno if it should be here, but we'll see eventually)
                    TravellersBackpack.NETWORK.sendTo(new SyncBackpackCapability(CapabilityUtils.getWearingBackpack(playerMP).writeToNBT(new NBTTagCompound())), (EntityPlayerMP)playerMP);
                }
            }
		}
	}
}