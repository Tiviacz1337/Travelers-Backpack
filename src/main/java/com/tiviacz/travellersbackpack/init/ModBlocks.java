package com.tiviacz.travellersbackpack.init;

import java.util.ArrayList;
import java.util.List;

import com.tiviacz.travellersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travellersbackpack.blocks.BlockTravellersBackpack;
import com.tiviacz.travellersbackpack.blocks.materials.Materials;

import net.minecraft.block.Block;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	public static final Block TRAVELLERS_BACKPACK = new BlockTravellersBackpack("travellers_backpack", Materials.BACKPACK);
	public static final Block SLEEPING_BAG_TOP = new BlockSleepingBag("sleeping_bag_top", Materials.SLEEPING_BAG);
	public static final Block SLEEPING_BAG_BOTTOM = new BlockSleepingBag("sleeping_bag_bottom", Materials.SLEEPING_BAG);
}