package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.gui.GuiOverlay;
import com.tiviacz.travelersbackpack.gui.container.slots.SlotTool;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.items.ItemHose;
import com.tiviacz.travelersbackpack.items.ItemTravelersBackpack;
import com.tiviacz.travelersbackpack.network.CycleToolPacket;
import com.tiviacz.travelersbackpack.network.GuiPacket;
import com.tiviacz.travelersbackpack.proxy.ClientProxy;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Side.CLIENT)
public class ClientEventHandler
{
	@SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) 
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
	public static void stitcherEventPre(TextureStitchEvent.Pre event) 
	{
	    event.getMap().registerSprite(ModFluids.POTION_STILL);
	    event.getMap().registerSprite(ModFluids.POTION_FLOW);

	    event.getMap().registerSprite(ModFluids.MILK_STILL);
	    event.getMap().registerSprite(ModFluids.MILK_FLOW);
	}
	
	@SubscribeEvent
	public static void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
	{
		if(ConfigHandler.client.overlay.enableOverlay)
        {
			if(event.getType() != ElementType.EXPERIENCE) return;
			
			if(CapabilityUtils.isWearingBackpack(Minecraft.getMinecraft().player))
		    {
				GuiOverlay gui = new GuiOverlay();
				gui.renderOverlay();
		    }
        }
	}
	
	@SubscribeEvent
    public static void handleKeyInputEvent(KeyInputEvent event)
    {
		KeyBinding key1 = ClientProxy.openBackpack;
		KeyBinding key2 = ClientProxy.toggleTank;
		
		if(key1.isPressed())
		{
			if(Minecraft.getMinecraft().player != null)
			{
				TravelersBackpack.NETWORK.sendToServer(new GuiPacket(GuiPacket.Handler.BACKPACK_GUI, GuiPacket.Handler.FROM_KEYBIND));
			}
		}
		
		if(key2.isPressed())
		{
			if(Minecraft.getMinecraft().player != null)
			{
				if(CapabilityUtils.isWearingBackpack(Minecraft.getMinecraft().player))
				{
					TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(0, Reference.TOGGLE_HOSE_TANK));
				}
			}
		}
    }
	
	@SubscribeEvent
    public static void mouseWheelDetect(MouseEvent event)
    {
		Minecraft mc = Minecraft.getMinecraft();
		KeyBinding key1 = ClientProxy.cycleTool;
	    int dWheel = event.getDwheel();
	        
	    if(dWheel != 0)
	    {
	    	EntityPlayerSP player = mc.player;

			if(player != null && !player.isDead && key1.isKeyDown())
			{
				ItemStack backpack = CapabilityUtils.getWearingBackpack(player);

				if(backpack != null && backpack.getItem() instanceof ItemTravelersBackpack)
				{
					player.getHeldItemMainhand();
					ItemStack heldItem = player.getHeldItemMainhand();

					if(ConfigHandler.client.enableToolCycling)
					{
						if(SlotTool.isValid(heldItem))
						{
							TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(dWheel, Reference.CYCLE_TOOL_ACTION));
							event.setCanceled(true);
						}
					}

					if(heldItem.getItem() instanceof ItemHose)
					{
						if(heldItem.getTagCompound() != null)
						{
							TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(dWheel, Reference.SWITCH_HOSE_ACTION));
							event.setCanceled(true);
						}
					}
				}
			}
	    }
    }
}