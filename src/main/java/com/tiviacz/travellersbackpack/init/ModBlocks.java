package com.tiviacz.travellersbackpack.init;

import java.util.ArrayList;
import java.util.List;

import com.tiviacz.travellersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travellersbackpack.blocks.BlockTravellersBackpack;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	public static final Block TRAVELLERS_BACKPACK = new BlockTravellersBackpack("travellers_backpack", Material.CLOTH);
	public static final Block SLEEPING_BAG_TOP = new BlockSleepingBag("sleeping_bag_top", Material.CLOTH);
	public static final Block SLEEPING_BAG_BOTTOM = new BlockSleepingBag("sleeping_bag_bottom", Material.CLOTH);
}