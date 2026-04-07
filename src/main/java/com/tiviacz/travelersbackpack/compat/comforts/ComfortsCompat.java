package com.tiviacz.travelersbackpack.compat.comforts;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class ComfortsCompat {
    public static int getComfortsSleepingBagColor(Item item) {
        return DyeColor.RED.getId();
        /*if(item instanceof SleepingBagItem sleepingBagItem && sleepingBagItem.getBlock() instanceof SleepingBagBlock sleepingBagBlock) {
            return sleepingBagBlock.getColor().getId();
        }
        return DyeColor.RED.getId();*/
    }
}