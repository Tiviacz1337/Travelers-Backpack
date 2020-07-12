package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.gui.GuiTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.GuiWarning;
import com.tiviacz.travelersbackpack.gui.container.ContainerTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.EnumSource;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		ItemStack heldItem = player.getHeldItemMainhand();
		
		if(ID == Reference.TRAVELERS_BACKPACK_ITEM_GUI_ID && heldItem.getItem() == ModItems.TRAVELERS_BACKPACK)
		{
			return new ContainerTravelersBackpack(world, player.inventory, new InventoryTravelersBackpack(heldItem, player), EnumSource.ITEM);
		}
		
		if(ID == Reference.TRAVELERS_BACKPACK_TILE_GUI_ID)
		{
			return new ContainerTravelersBackpack(world, player.inventory, (TileEntityTravelersBackpack)world.getTileEntity(new BlockPos(x,y,z)), EnumSource.TILE);
		}
		
		if(ID == Reference.TRAVELERS_BACKPACK_WEARABLE_GUI_ID)
		{
			return new ContainerTravelersBackpack(world, player.inventory, new InventoryTravelersBackpack(CapabilityUtils.getWearingBackpack(player), player), EnumSource.WEARABLE);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		ItemStack heldItem = player.getHeldItemMainhand();
		
		if(ID == Reference.TRAVELERS_BACKPACK_ITEM_GUI_ID && heldItem.getItem() == ModItems.TRAVELERS_BACKPACK)
		{
			return new GuiTravelersBackpack(world, player.inventory, new InventoryTravelersBackpack(heldItem, player), false);
		}
		
		if(ID == Reference.TRAVELERS_BACKPACK_TILE_GUI_ID)
		{
			return new GuiTravelersBackpack(world, player.inventory, (TileEntityTravelersBackpack)world.getTileEntity(new BlockPos(x,y,z)));
		}
		
		if(ID == Reference.TRAVELERS_BACKPACK_WEARABLE_GUI_ID)
		{
			return new GuiTravelersBackpack(world, player.inventory, new InventoryTravelersBackpack(CapabilityUtils.getWearingBackpack(player), player), true);
		}
		return null;
	}

	@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Side.CLIENT)
	public static class GuiScreenHandler
	{
		@SubscribeEvent
		public static void onGuiOpen(GuiOpenEvent event)
		{
			GuiScreen gui = event.getGui();

			if(ConfigHandler.server.enableWarning && gui instanceof GuiMainMenu)
			{
				event.setGui(new GuiWarning((GuiMainMenu) gui));
				ConfigHandler.server.enableWarning = false;
				MinecraftForge.EVENT_BUS.post(new ConfigChangedEvent.OnConfigChangedEvent(TravelersBackpack.MODID, TravelersBackpack.NAME, false, false));
			}
		}
	}
}