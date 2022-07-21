package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.ItemEntity;

public class EntityItemHandler
{
    public static void registerListeners()
    {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
        {
            if(!(entity instanceof ItemEntity) || !TravelersBackpackConfig.invulnerableBackpack) return;

            if(((ItemEntity)entity).getStack().getItem() instanceof TravelersBackpackItem)
            {
                ((ItemEntity)entity).setCovetedItem();
                entity.setInvulnerable(true);
            }
        });
    }
}