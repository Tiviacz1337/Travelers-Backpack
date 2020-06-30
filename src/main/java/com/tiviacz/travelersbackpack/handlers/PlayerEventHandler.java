package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@EventBusSubscriber(modid = TravelersBackpack.MODID)
public class PlayerEventHandler
{
	@SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
    	if(event.getObject() instanceof EntityPlayer)
    	{
			final TravelersBackpackWearable travelersBackpack = new TravelersBackpackWearable((EntityPlayer) event.getObject());
			event.addCapability(TravelersBackpackCapability.ID, TravelersBackpackCapability.createProvider(travelersBackpack));
		}
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
        World world = event.getEntityPlayer().getEntityWorld();

        if(event.getNewSpawn() != null)
        {
            Block block = world.getBlockState(event.getNewSpawn()).getBlock();

            if(!world.isRemote && !ConfigHandler.server.enableSleepingBagSpawnPoint && block instanceof BlockSleepingBag)
            {
            	event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		World world = event.getEntityPlayer().world;

		if(!world.isRemote)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();

			if(ConfigHandler.server.enableSleepingBagSpawnPoint && BackpackUtils.findSleepingBag(world, player.getPosition()))
			{
				player.setSpawnPoint(player.getPosition(), true);
				player.bedLocation = null;
			}
		}
	}

	@SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event)
    {
    	if(event.getEntity() instanceof EntityPlayer)
		{
			CapabilityUtils.synchronise((EntityPlayer)event.getEntity());
		}
    }
	
	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event)
    {
		if(event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();

			if(CapabilityUtils.isWearingBackpack(player))
			{
				if(!player.getEntityWorld().getGameRules().getBoolean("keepInventory"))
				{
					BackpackUtils.onPlayerDeath(player.world, player, CapabilityUtils.getWearingBackpack(player));
				}
			}
		}
    }

    @SubscribeEvent
    public static void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
	{
		CapabilityUtils.getCapability(event.getEntityPlayer()).setWearable(CapabilityUtils.getCapability(event.getOriginal()).getWearable());
	}

	@SubscribeEvent
	public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		CapabilityUtils.synchronise(event.player);
	}
	
	@SubscribeEvent
    public static void onPlayerTravelsAcrossDimensions(PlayerEvent.PlayerChangedDimensionEvent event)
    {
    	CapabilityUtils.synchronise(event.player);
    }
	
	@SubscribeEvent
	public static void onPlayerTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event)
	{
		if(event.getTarget() instanceof EntityPlayer)
		{
			CapabilityUtils.synchroniseToOthers((EntityPlayer)event.getTarget());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(!event.player.world.isRemote)
		{
			if(!event.player.isDead && CapabilityUtils.isWearingBackpack(event.player))
			{
				World world = event.player.getEntityWorld();
				EntityPlayer player = event.player;
						
				if(event.phase == TickEvent.Phase.START)
				{
					CapabilityUtils.onEquippedUpdate(world, player, CapabilityUtils.getWearingBackpack(player));
				}
			}
		}
	}
}