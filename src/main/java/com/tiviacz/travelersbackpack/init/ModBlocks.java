package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travelersbackpack.blocks.BlockTravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.materials.Materials;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<>();
	
	public static final Block TRAVELERS_BACKPACK = new BlockTravelersBackpack("travelers_backpack", Materials.BACKPACK);
	public static final Block SLEEPING_BAG_TOP = new BlockSleepingBag("sleeping_bag_top", Materials.SLEEPING_BAG);
	public static final Block SLEEPING_BAG_BOTTOM = new BlockSleepingBag("sleeping_bag_bottom", Materials.SLEEPING_BAG);
}