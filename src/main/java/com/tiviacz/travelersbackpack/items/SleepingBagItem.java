package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class SleepingBagItem extends BlockItem
{
    public SleepingBagItem(Settings settings)
    {
        super(ModBlocks.SLEEPING_BAG, settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        return ActionResult.FAIL;
    }
}