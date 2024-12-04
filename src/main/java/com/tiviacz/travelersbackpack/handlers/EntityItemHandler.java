package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpackneo.init.ModItems;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

public class EntityItemHandler {
    public static void registerListeners() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity itemEntity && itemEntity.getStack().getItem() instanceof TravelersBackpackItem backpack) {
                if (itemEntity.getType() != ModItems.BACKPACK_ITEM_ENTITY) {
                    Entity backpackEntity = backpack.createBackpackEntity(world, itemEntity, itemEntity.getStack());
                    if (backpackEntity != null) {
                        entity.discard();
                        world.spawnEntity(backpackEntity);
                    }
                }
            }
        });
    }
}