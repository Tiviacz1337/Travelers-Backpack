package com.tiviacz.travelersbackpack.compat.comforts;

import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.common.item.SleepingBagItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;

public class ComfortsCompat
{
    public static int getComfortsSleepingBagColor(Item item)
    {
        if(item instanceof SleepingBagItem sleepingBagItem && sleepingBagItem.getBlock() instanceof SleepingBagBlock sleepingBagBlock)
        {
            return sleepingBagBlock.getColor().getId();
        }
        return DyeColor.RED.getId();
    }
}