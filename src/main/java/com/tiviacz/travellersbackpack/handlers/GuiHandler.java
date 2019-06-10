package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.gui.GuiTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.container.ContainerTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.EnumSource;
import com.tiviacz.travellersbackpack.util.Reference;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

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
		
		if(ID == Reference.TRAVELLERS_BACKPACK_ITEM_GUI_ID && heldItem.getItem() == ModItems.TRAVELLERS_BACKPACK)
		{
			return new ContainerTravellersBackpack(world, player.inventory, new InventoryTravellersBackpack(heldItem, player), EnumSource.ITEM);
		}
		
		if(ID == Reference.TRAVELLERS_BACKPACK_TILE_GUI_ID)
		{
			return new ContainerTravellersBackpack(world, player.inventory, (TileEntityTravellersBackpack)world.getTileEntity(new BlockPos(x,y,z)), EnumSource.TILE);
		}
		
		if(ID == Reference.TRAVELLERS_BACKPACK_WEARABLE_GUI_ID)
		{
			return new ContainerTravellersBackpack(world, player.inventory, new InventoryTravellersBackpack(WearableUtils.getWearingBackpack(player), player), EnumSource.WEARABLE);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		ItemStack heldItem = player.getHeldItemMainhand();
		
		if(ID == Reference.TRAVELLERS_BACKPACK_ITEM_GUI_ID && heldItem.getItem() == ModItems.TRAVELLERS_BACKPACK)
		{
			return new GuiTravellersBackpack(world, player.inventory, new InventoryTravellersBackpack(heldItem, player), false);
		}
		
		if(ID == Reference.TRAVELLERS_BACKPACK_TILE_GUI_ID)
		{
			return new GuiTravellersBackpack(world, player.inventory, (TileEntityTravellersBackpack)world.getTileEntity(new BlockPos(x,y,z)));
		}
		
		if(ID == Reference.TRAVELLERS_BACKPACK_WEARABLE_GUI_ID)
		{
			return new GuiTravellersBackpack(world, player.inventory, new InventoryTravellersBackpack(WearableUtils.getWearingBackpack(player), player), true);
		}
		return null;
	}
}