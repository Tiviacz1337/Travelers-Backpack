package com.tiviacz.travelersbackpack.blocks.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class Materials
{
	public static final Material BACKPACK = new MaterialBackpack(MapColor.BROWN);
	public static final Material SLEEPING_BAG = new MaterialSleepingBag();

	public static class MaterialBackpack extends Material
	{
		public MaterialBackpack(MapColor color)
		{
			super(color);
		}

		@Override
		public boolean blocksLight()
		{
			return false;
		}
	}

	public static class MaterialSleepingBag extends MaterialBackpack
	{
		public MaterialSleepingBag()
		{
			super(MapColor.RED);
			setImmovableMobility();
		}
	}
}