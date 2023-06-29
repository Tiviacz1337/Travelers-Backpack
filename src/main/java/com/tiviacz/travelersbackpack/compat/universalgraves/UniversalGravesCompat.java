package com.tiviacz.travelersbackpack.compat.universalgraves;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import eu.pb4.graves.event.PlayerGraveItemAddedEvent;
import eu.pb4.graves.event.PlayerGraveItemsEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class UniversalGravesCompat
{
    public static void register()
    {
        PlayerGraveItemsEvent.EVENT.register((player, items) ->
        {
            if(ComponentUtils.isWearingBackpack(player))
            {
                ItemStack stack = ComponentUtils.getWearingBackpack(player);

                if(PlayerGraveItemAddedEvent.EVENT.invoker().canAddItem(player, stack) != ActionResult.FAIL)
                {
                    items.add(stack);
                    ComponentUtils.getComponent(player).removeWearable();

                    //Sync
                    ComponentUtils.sync(player);
                    ComponentUtils.syncToTracking(player);
                }
            }
        });
    }
}