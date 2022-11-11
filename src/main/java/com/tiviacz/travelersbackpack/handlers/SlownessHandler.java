package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.atomic.AtomicInteger;

public class SlownessHandler
{
    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    public static void registerListener()
    {
        ServerTickEvents.START_WORLD_TICK.register((ServerWorld world) ->
        {
            if(!TravelersBackpackConfig.tooManyBackpacksSlowness || nextBackpackCountCheck > world.getTime())
            {
                return;
            }
            nextBackpackCountCheck = world.getTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

            world.getPlayers().forEach(player ->
            {
                AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);
                if(numberOfBackpacks.get() == 0) return;

                int maxNumberOfBackpacks = TravelersBackpackConfig.maxNumberOfBackpacks;
                if(numberOfBackpacks.get() > maxNumberOfBackpacks)
                {
                    int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.slownessPerExcessedBackpack));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
                }
            });
        });
    }

    public static AtomicInteger checkBackpacksForSlowness(PlayerEntity player)
    {
        AtomicInteger atomic = new AtomicInteger(0);

        for(int i = 0; i < player.inventory.main.size() + 1; i++)
        {
            if(i != 36)
            {
                if(player.inventory.main.get(i).getItem() instanceof TravelersBackpackItem)
                {
                    atomic.incrementAndGet();
                }
            }
            else
            {
                if(player.inventory.offHand.get(0).getItem() instanceof TravelersBackpackItem)
                {
                    atomic.incrementAndGet();
                }
            }
        }
        return atomic;
    }
}
