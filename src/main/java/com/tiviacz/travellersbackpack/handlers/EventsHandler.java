package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.common.ServerActions;
import com.tiviacz.travellersbackpack.gui.GuiOverlay;
import com.tiviacz.travellersbackpack.gui.container.ContainerTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotTool;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.items.ItemHose;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;
import com.tiviacz.travellersbackpack.network.CycleToolPacket;
import com.tiviacz.travellersbackpack.network.GuiPacket;
import com.tiviacz.travellersbackpack.network.client.SyncPlayerDataPacket;
import com.tiviacz.travellersbackpack.proxy.ClientProxy;
import com.tiviacz.travellersbackpack.util.BackpackUtils;
import com.tiviacz.travellersbackpack.util.IHasModel;
import com.tiviacz.travellersbackpack.util.NBTUtils;
import com.tiviacz.travellersbackpack.wearable.Wearable;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class EventsHandler 
{
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
	}
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ModItems.ITEMS)
		{
			if(item instanceof IHasModel)
			{
				((IHasModel)item).registerModels();
			}
		}
		
		for(Block block : ModBlocks.BLOCKS)
		{
			if(block instanceof IHasModel)
			{
				((IHasModel)block).registerModels();
			}
		}
	}
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
		if(event.getModID().equals(TravellersBackpack.MODID))
        {
			ConfigManager.sync(TravellersBackpack.MODID, Config.Type.INSTANCE);
        }
    }
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void stitcherEventPre(TextureStitchEvent.Pre event) 
	{
		//Milk
	    ResourceLocation milk_still = new ResourceLocation(TravellersBackpack.MODID + ":blocks/milk_still");
	    ResourceLocation milk_flow = new ResourceLocation(TravellersBackpack.MODID + ":blocks/milk_flow");
	    
	    event.getMap().registerSprite(milk_still);
	    event.getMap().registerSprite(milk_flow);
	    
	    //Mushroom Stew
	//    ResourceLocation mushroom_stew_still = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_still");
	//    ResourceLocation mushroom_stew_flow = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_flow");
	    
	//    event.getMap().registerSprite(mushroom_stew_still);
	//    event.getMap().registerSprite(mushroom_stew_flow);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
	{
		if(ConfigHandler.enableOverlay)
        {
			if(event.getType() == ElementType.EXPERIENCE)
			{
				GuiOverlay gui = new GuiOverlay();
			}
        }
	}
	
/*	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) 
	{
		if(!ConfigHandler.canInteractWithOtherPlayersBackpack || !(event.getTarget() instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = event.getEntityPlayer();
		EntityPlayer target = (EntityPlayer)event.getTarget();
	
		if(!WearableUtils.canInteractWithEquippedBackpack(player, target))
		{
			return;
		}

		if(!player.world.isRemote && WearableUtils.canInteractWithEquippedBackpack(player, target)) 
		{
			NBTTagCompound data = target.getEntityData().getCompoundTag("Wearable");
			
			if(data != null) 
			{
		//		player.openGui(TravellersBackpack.INSTANCE, modGuiId, world, x, y, z);
			} 
		}
	} */
	
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    public static void handleKeyInputEvent(KeyInputEvent event)
    {
		KeyBinding key1 = ClientProxy.openBackpack;
		KeyBinding key2 = ClientProxy.toggleTank;
		
		if(key1.isPressed())
		{
			if(Minecraft.getMinecraft().player != null)
			{
				TravellersBackpack.NETWORK.sendToServer(new GuiPacket(GuiPacket.Handler.BACKPACK_GUI, GuiPacket.Handler.FROM_KEYBIND));
			}
		}
		
		if(key2.isPressed())
		{
			if(Minecraft.getMinecraft().player != null)
			{
				if(NBTUtils.hasWearingTag(Minecraft.getMinecraft().player))
				{
					TravellersBackpack.NETWORK.sendToServer(new CycleToolPacket(0, CycleToolPacket.Handler.TOGGLE_HOSE_TANK));
				}
			}
		}
    }
	
	@SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void mouseWheelDetect(MouseEvent event)
    {
		Minecraft mc = Minecraft.getMinecraft();
	    int dWheel = event.getDwheel();
	        
	    if(dWheel != 0)
	    {
	    	EntityPlayerSP player = mc.player;
	            
	    	if(player != null && !player.isDead && player.isSneaking())
	    	{
	    		ItemStack backpack = WearableUtils.getWearingBackpack(player);
	                
	    		if(backpack != null && backpack.getItem() instanceof ItemTravellersBackpack)
	    		{
	    			if(player.getHeldItemMainhand() != null)
	    			{
	    				ItemStack heldItem = player.getHeldItemMainhand();
	
	    				if(ConfigHandler.enableToolCycling)
	    				{
	    					if(SlotTool.isValid(heldItem))
	    					{
	    						TravellersBackpack.NETWORK.sendToServer(new CycleToolPacket(dWheel, CycleToolPacket.Handler.CYCLE_TOOL_ACTION));
	    						event.setCanceled(true);
	    					}
	    				}
	                        
	    				if(heldItem.getItem() instanceof ItemHose)
	    				{
	    					if(heldItem.getTagCompound() != null)
	    					{
	    						TravellersBackpack.NETWORK.sendToServer(new CycleToolPacket(dWheel, CycleToolPacket.Handler.SWITCH_HOSE_ACTION));
	    						event.setCanceled(true);
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
    }
}