package com.tiviacz.travelersbackpack.compat.comforts;

import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.common.item.SleepingBagItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

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