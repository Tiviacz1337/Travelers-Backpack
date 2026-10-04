package com.tiviacz.travelersbackpack.compat.polymorph;

import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ResultSlotExt;

public class PolymorphCompatClient {
    public static void registerWidget() {
        PolymorphWidgets.getInstance().registerWidget(screen -> {
            if(screen instanceof BackpackScreen backpackScreen) {
                for(var slot : backpackScreen.getMenu().slots) {
                    if(slot instanceof ResultSlotExt) {
                        return new PolymorphWidget(backpackScreen, slot);
                    }
                }
            }
            return null;
        });
    }
}