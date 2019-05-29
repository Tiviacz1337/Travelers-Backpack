package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travellersbackpack.gui.GuiOverlay;
import com.tiviacz.travellersbackpack.gui.container.slots.SlotTool;
import com.tiviacz.travellersbackpack.items.ItemHose;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;
import com.tiviacz.travellersbackpack.network.CycleToolPacket;
import com.tiviacz.travellersbackpack.network.GuiPacket;
import com.tiviacz.travellersbackpack.proxy.ClientProxy;
import com.tiviacz.travellersbackpack.util.NBTUtils;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ClientEventHandler 
{
	@SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event) 
	{
        final EntityPlayer player = event.getEntityPlayer();
        
        if(player instanceof EntityOtherPlayerMP && player.isPlayerSleeping() && player.bedLocation != null) 
        {
            Block bed = player.world.getBlockState(player.bedLocation).getBlock();
            
            if(bed instanceof BlockSleepingBag) 
            {
                player.renderOffsetY = -0.375F;
            }
        }
    }

	@SubscribeEvent
	public void stitcherEventPre(TextureStitchEvent.Pre event) 
	{
		//Milk
	    ResourceLocation milk_still = new ResourceLocation(TravellersBackpack.MODID, "blocks/milk_still");
	    ResourceLocation milk_flow = new ResourceLocation(TravellersBackpack.MODID, "blocks/milk_flow");
	    
	    event.getMap().registerSprite(milk_still);
	    event.getMap().registerSprite(milk_flow);
	    
	    //Mushroom Stew
	//    ResourceLocation mushroom_stew_still = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_still");
	//    ResourceLocation mushroom_stew_flow = new ResourceLocation(TravellersBackpack.MODID + ":blocks/mushroom_stew_flow");
	    
	//    event.getMap().registerSprite(mushroom_stew_still);
	//    event.getMap().registerSprite(mushroom_stew_flow);
	}
	
	@SubscribeEvent
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
	{
		if(ConfigHandler.enableOverlay)
        {
			if(event.getType() == ElementType.EXPERIENCE)
			{
				GuiOverlay gui = new GuiOverlay();
				gui.renderOverlay();
			}
        }
	}
	
	@SubscribeEvent
    public void handleKeyInputEvent(KeyInputEvent event)
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
    public void mouseWheelDetect(MouseEvent event)
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