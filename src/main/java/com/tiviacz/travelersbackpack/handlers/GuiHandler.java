package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.gui.GuiTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.container.ContainerTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.EnumSource;
import com.tiviacz.travelersbackpack.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

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
}