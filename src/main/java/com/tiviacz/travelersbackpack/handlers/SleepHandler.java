package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;

public class SleepHandler {
    public static void registerListener() //#TODO check on other fabrics
    {
        //Handled in SleepingBagBlock to create forced spawn point
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) -> !(!player.level().isClientSide && player.level().getBlockState(sleepingPos).getBlock() instanceof SleepingBagBlock));
    }
}