package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class SleepingBagItem extends BlockItem
{
    public SleepingBagItem(Block block, Settings settings)
    {
        super(block, settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        return ActionResult.FAIL;
    }
}