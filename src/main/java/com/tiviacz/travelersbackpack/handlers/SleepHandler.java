package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;

public class SleepHandler
{
    public static void registerListener()
    {
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) ->
                !(!player.getWorld().isClient && player.getWorld().getBlockState(sleepingPos).getBlock() instanceof SleepingBagBlock && !TravelersBackpackConfig.getConfig().backpackSettings.enableSleepingBagSpawnPoint));
    }
}
