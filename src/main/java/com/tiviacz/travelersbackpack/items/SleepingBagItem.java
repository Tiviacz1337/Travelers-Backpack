package com.tiviacz.travelersbackpack.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class SleepingBagItem extends BlockItem
{
    public SleepingBagItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        return ActionResultType.FAIL;
    }
}