package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class SleepingBagItem extends BlockItem
{
    public SleepingBagItem(Properties properties)
    {
        super(ModBlocks.SLEEPING_BAG.get(), properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        return ActionResultType.FAIL;
    }
}