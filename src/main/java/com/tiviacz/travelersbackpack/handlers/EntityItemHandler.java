package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

public class EntityItemHandler {
    public static void registerListeners() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof TravelersBackpackItem backpack) {
                if (itemEntity.getType() != ModItems.BACKPACK_ITEM_ENTITY) {
                    Entity backpackEntity = backpack.createEntity(world, itemEntity, itemEntity.getItem());
                    if (backpackEntity != null) {
                        entity.discard();
                        world.addFreshEntity(backpackEntity);
                    }
                }
            }
        });
    }
}