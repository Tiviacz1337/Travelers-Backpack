package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;

public class SleepingBagItem extends BlockItem
{
    public SleepingBagItem(Properties properties)
    {
        super(ModBlocks.SLEEPING_BAG.get(), properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        return InteractionResult.FAIL;
    }
}
