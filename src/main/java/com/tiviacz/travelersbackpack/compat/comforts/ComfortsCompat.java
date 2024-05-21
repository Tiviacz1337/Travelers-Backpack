package com.tiviacz.travelersbackpack.compat.comforts;

import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.item.SleepingBagItem;

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