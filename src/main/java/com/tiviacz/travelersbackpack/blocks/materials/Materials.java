package com.tiviacz.travelersbackpack.blocks.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class Materials extends Material
{
	public static final Materials BACKPACK = new Materials(MapColor.BROWN);
	public static final Materials SLEEPING_BAG = new Materials(MapColor.RED);
	
	public Materials(MapColor color) 
	{
		super(color);
	}
	
	@Override
	public boolean blocksLight()
    {
        return false;
    }
}